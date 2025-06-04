package ccc.sypw.onlineExamSystem.util

import ccc.sypw.onlineExamSystem.service.QuestionService
import ccc.sypw.onlineExamSystem.repository.ExamQuestionsRepository
import org.springframework.stereotype.Component

/**
 * 自动评分结果数据类
 */
data class AutoGradingResult(
    val totalScore: Int,
    val detailScores: Map<String, Any>,
    val isFullyGraded: Boolean
)

@Component
class ExamGradingUtils(
    private val questionService: QuestionService, // 注入题目服务
    private val examQuestionsRepository: ExamQuestionsRepository // 注入考试题目关联仓库
) {

    /**
     * 自动阅卷方法
     *
     * @param examId 考试ID，用于获取每题的分数
     * @param answers 前端提供的答题数据 (题目ID -> 学生答案)
     * @return Pair，其中第一个元素是总得分，第二个元素是每道题的评分情况 (题目ID -> 是否正确)
     */
    fun gradeExam(examId: Long, answers: Map<String, Any>): Pair<Int, Map<String, Boolean>> {
        var totalScore = 0 // 总得分
        val gradingResult = mutableMapOf<String, Boolean>() // 评分结果

        // 检查答题数据是否为空
        if (answers.isEmpty()) {
            return Pair(0, emptyMap())
        }

        answers.forEach { (questionId, studentAnswer) ->
            val correctAnswer = questionService.getQuestionAnswer(questionId.toLong()) // 查询正确答案
            val isCorrect = checkAnswer(studentAnswer, correctAnswer) // 判断是否正确

            if (isCorrect) {
                // 获取该题在此考试中的分数
                val questionScore = examQuestionsRepository.getQuestionScore(examId, questionId.toLong()) ?: 1
                totalScore += questionScore
            }

            gradingResult[questionId] = isCorrect // 保存评分情况
        }

        return Pair(totalScore, gradingResult)
    }

    /**
     * 带自动阅卷功能的评分方法
     * 自动对客观题进行评分，判断是否需要人工阅卷
     *
     * @param examId 考试ID
     * @param answers 学生答案
     * @return AutoGradingResult 包含总分、详细得分和是否完全阅卷完成
     */
    fun gradeExamWithAutoGrading(examId: Long, answers: Map<String, Any>): AutoGradingResult {
        var totalScore = 0
        val detailScores = mutableMapOf<String, Any>()
        var hasSubjectiveQuestions = false

        if (answers.isEmpty()) {
            return AutoGradingResult(0, emptyMap(), true)
        }

        answers.forEach { (questionId, studentAnswer) ->
            val questionIdLong = questionId.toLong()
            val question = questionService.getQuestionById(questionIdLong)
            val correctAnswer = questionService.getQuestionAnswer(questionIdLong)
            val questionScore = examQuestionsRepository.getQuestionScore(examId, questionIdLong) ?: 1

            when (question?.type) {
                "single", "multiple", "true_false" -> {
                    // 客观题：自动评分
                    val isCorrect = checkAnswer(studentAnswer, correctAnswer)
                    val earnedScore = if (isCorrect) questionScore else 0
                    totalScore += earnedScore
                    detailScores["question_${questionId}_score"] = earnedScore
                    detailScores["question_${questionId}_auto_graded"] = true
                }
                "fill_blank", "short_answer" -> {
                    // 主观题：标记需要人工阅卷
                    hasSubjectiveQuestions = true
                    detailScores["question_${questionId}_score"] = 0 // 初始分数为0，等待人工阅卷
                    detailScores["question_${questionId}_auto_graded"] = false
                }
                else -> {
                    // 未知题型：标记需要人工阅卷
                    hasSubjectiveQuestions = true
                    detailScores["question_${questionId}_score"] = 0
                    detailScores["question_${questionId}_auto_graded"] = false
                }
            }
        }

        // 如果没有主观题，则认为已完全阅卷
        val isFullyGraded = !hasSubjectiveQuestions

        return AutoGradingResult(totalScore, detailScores, isFullyGraded)
    }

    /**
     * 检查学生答案是否正确
     *
     * @param studentAnswer 学生的答案
     * @param correctAnswer 正确答案
     * @return true 如果答案正确，false 如果答案错误
     */
    private fun checkAnswer(studentAnswer: Any, correctAnswer: String?): Boolean {
        if (correctAnswer == null) {
            return false
        }
        return when (studentAnswer) {
            is String -> {
                // 单选题、填空题、判断题
                studentAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
            }
            is List<*> -> {
                // 多选题
                val studentAnswerSet = studentAnswer.map { it.toString().trim() }.toSet()
                val correctAnswerSet = correctAnswer.split(",").map { it.trim() }.toSet()
                studentAnswerSet == correctAnswerSet
            }
            else -> false
        }
    }
}
