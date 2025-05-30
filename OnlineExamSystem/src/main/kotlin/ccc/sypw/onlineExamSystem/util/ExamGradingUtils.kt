package ccc.sypw.onlineExamSystem.util

import ccc.sypw.onlineExamSystem.model.Question
import ccc.sypw.onlineExamSystem.service.ExamQuestionService
import ccc.sypw.onlineExamSystem.service.QuestionService
import ccc.sypw.onlineExamSystem.service.SubjectiveGradingService
import org.springframework.stereotype.Component
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@Component
class ExamGradingUtils(
    private val questionService: QuestionService, // 注入题目服务
    private val examQuestionService: ExamQuestionService, // 注入考试题目关联服务
    private val subjectiveGradingService: SubjectiveGradingService // 注入主观题判分服务
) {

    /**
     * 自动阅卷方法
     *
     * @param answers 前端提供的答题数据 (题目ID -> 学生答案)
     * @param examId 考试ID，用于获取题目分数
     * @return Pair，其中第一个元素是总得分，第二个元素是每道题的得分情况 (题目ID -> 得分)
     */
    fun gradeExam(answers: Map<String, Any>, examId: Long? = null): Pair<Int, Map<String, Any>> {
        var totalScore = 0 // 总得分
        val gradingResult = mutableMapOf<String, Any>() // 评分结果
        
        // 获取所有题目ID
        val questionIds = answers.keys.map { it.toLong() }
        
        // 获取题目详情，包括题型
        val questions = questionService.getQuestionsByIds(questionIds)
        val questionsMap = questions.associateBy { it.id.toString() }
        
        // 获取题目分数（如果提供了examId）
        val questionScores = if (examId != null) {
            // TODO: 获取考试中每道题的分数
            val examQuestions = examQuestionService.findByExamId(examId)
            // 这里需要实现获取每道题分数的逻辑
            mutableMapOf<String, Int>()
        } else {
            // 如果没有提供examId，假设每道题分数相等
            val scorePerQuestion = 100 / answers.size
            answers.keys.associateWith { scorePerQuestion }.toMutableMap()
        }

        answers.forEach { (questionId, studentAnswer) ->
            val question = questionsMap[questionId]
            if (question != null) {
                val correctAnswer = questionService.getQuestionAnswer(questionId.toLong()) // 查询正确答案
                val questionType = question.type // 获取题目类型
                val score = questionScores[questionId] ?: 0 // 获取题目分数
                
                // 根据题型进行评分
                val (isCorrect, earnedScore, feedback) = gradeQuestionByType(questionType, studentAnswer, correctAnswer, score)
                
                totalScore += earnedScore // 累加得分
                
                // 保存题目的评分详情
                gradingResult[questionId] = mapOf(
                    "correct" to isCorrect,
                    "score" to earnedScore,
                    "feedback" to feedback,
                    "maxScore" to score,
                    "studentAnswer" to studentAnswer,
                    "correctAnswer" to correctAnswer
                )
            }
        }

        return Pair(totalScore, gradingResult)
    }
    
    /**
     * 根据题目类型评分
     * 
     * @param questionType 题目类型
     * @param studentAnswer 学生答案
     * @param correctAnswer 正确答案
     * @param maxScore 该题目满分
     * @return Triple，包含是否正确, 得分, 反馈信息
     */
    private fun gradeQuestionByType(
        questionType: String, 
        studentAnswer: Any, 
        correctAnswer: String?, 
        maxScore: Int
    ): Triple<Boolean, Int, String> {
        if (correctAnswer == null) {
            return Triple(false, 0, "题目答案不存在")
        }
        
        // 对于主观题，使用智能判分
        if (questionType == "short_answer") {
            val studentAnswerStr = studentAnswer.toString().trim()
            if (studentAnswerStr.isEmpty()) {
                return Triple(false, 0, "答案为空")
            }
            
            val (score, similarity, details) = subjectiveGradingService.gradeSubjectiveQuestion(
                studentAnswerStr,
                correctAnswer,
                maxScore
            )
            
            // 判断是否需要人工复审（得分率低于30%或相似度低于20%）
            val scoreRate = score.toDouble() / maxScore
            val needsReview = scoreRate < 0.3 || similarity < 20.0
            
            val feedback = if (needsReview) {
                "$details\n\n⚠️ 建议人工复审"
            } else {
                details
            }
            
            return Triple(scoreRate >= 0.6, score, feedback)
        }
        
        // 对于客观题，自动评分
        return when (questionType) {
            "single" -> { // 单选题
                val isCorrect = studentAnswer.toString().trim().equals(correctAnswer.trim(), ignoreCase = true)
                Triple(isCorrect, if (isCorrect) maxScore else 0, if (isCorrect) "正确" else "错误")
            }
            "multiple" -> { // 多选题
                val studentAnswerSet = if (studentAnswer is List<*>) {
                    studentAnswer.map { it.toString().trim() }.toSet()
                } else {
                    // 如果前端提供的不是列表，尝试转换
                    try {
                        when (studentAnswer) {
                            is String -> jacksonObjectMapper().readValue(studentAnswer, List::class.java).map { it.toString().trim() }.toSet()
                            else -> setOf(studentAnswer.toString().trim())
                        }
                    } catch (e: Exception) {
                        setOf(studentAnswer.toString().trim())
                    }
                }
                
                val correctAnswerSet = correctAnswer.split(",").map { it.trim() }.toSet()
                val isCorrect = studentAnswerSet == correctAnswerSet
                
                // 部分正确的情况，可以给部分分数
                val correctCount = studentAnswerSet.intersect(correctAnswerSet).size
                val wrongCount = studentAnswerSet.minus(correctAnswerSet).size
                
                // 计算得分：正确选项 - 错误选项，最低0分
                val earnedScore = if (isCorrect) {
                    maxScore
                } else {
                    val partialScore = (correctCount * maxScore / correctAnswerSet.size) - (wrongCount * maxScore / correctAnswerSet.size)
                    maxOf(0, partialScore)
                }
                
                Triple(isCorrect, earnedScore, if (isCorrect) "完全正确" else "部分正确")
            }
            "true_false" -> { // 判断题
                val isCorrect = studentAnswer.toString().trim().equals(correctAnswer.trim(), ignoreCase = true)
                Triple(isCorrect, if (isCorrect) maxScore else 0, if (isCorrect) "正确" else "错误")
            }
            "fill_blank" -> { // 填空题
                // 填空题可能有多个空，用逗号分隔
                val studentAnswers = studentAnswer.toString().split(",").map { it.trim() }
                val correctAnswers = correctAnswer.split(",").map { it.trim() }
                
                // 如果空的数量不匹配，判为错误
                if (studentAnswers.size != correctAnswers.size) {
                    return Triple(false, 0, "填空数量不匹配")
                }
                
                // 计算正确的空的数量
                var correctCount = 0
                for (i in studentAnswers.indices) {
                    if (studentAnswers[i].equals(correctAnswers[i], ignoreCase = true)) {
                        correctCount++
                    }
                }
                
                // 计算得分
                val earnedScore = correctCount * maxScore / correctAnswers.size
                val isAllCorrect = correctCount == correctAnswers.size
                
                Triple(isAllCorrect, earnedScore, if (isAllCorrect) "完全正确" else "部分正确: $correctCount/${correctAnswers.size}")
            }
            else -> Triple(false, 0, "未知题型")
        }
    }
}
