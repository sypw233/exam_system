package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.dto.GradingDetailDto
import ccc.sypw.onlineExamSystem.dto.QuestionAnswerDto
import ccc.sypw.onlineExamSystem.dto.UpdateScoreDto
import ccc.sypw.onlineExamSystem.repository.ExamSubmissionRepository
import ccc.sypw.onlineExamSystem.repository.ExamRepository
import ccc.sypw.onlineExamSystem.repository.QuestionRepository
import ccc.sypw.onlineExamSystem.repository.UserRepository
import ccc.sypw.onlineExamSystem.repository.ExamQuestionsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GradingService(
    private val examSubmissionRepository: ExamSubmissionRepository,
    private val examRepository: ExamRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    private val examQuestionsRepository: ExamQuestionsRepository,
    private val objectMapper: ObjectMapper
) {

    fun getExamSubmissions(examId: Long): List<GradingDetailDto> {
        val submissions = examSubmissionRepository.findByExamId(examId)
        return submissions.mapNotNull { submission ->
            val exam = examRepository.findById(submission.examId) ?: return@mapNotNull null
            val student = userRepository.findById(submission.userId).orElse(null)
            
            GradingDetailDto(
                submissionId = submission.id!!,
                studentId = submission.userId,
                studentName = student.username,
                examId = submission.examId,
                examTitle = exam.title,
                submitTime = submission.submitTime,
                isGraded = submission.isGraded,
                totalScore = exam.totalScore,
                currentScore = submission.submitScore ?: 0,
                questions = getQuestionAnswers(submission.examId, submission.id!!, submission.answers.toString(), submission.submitDetail)
            )
        }
    }

    fun getSubmissionDetail(submissionId: Long): GradingDetailDto? {
        val submission = examSubmissionRepository.findById(submissionId).orElse(null) ?: return null
        val exam = examRepository.findById(submission.examId) ?: return null
        val student = userRepository.findById(submission.userId).orElse(null)
        
        return GradingDetailDto(
            submissionId = submission.id!!,
            studentId = submission.userId,
            studentName = student.username,
            examId = submission.examId,
            examTitle = exam.title,
            submitTime = submission.submitTime,
            isGraded = submission.isGraded,
            totalScore = exam.totalScore,
            currentScore = submission.submitScore ?: 0,
            questions = getQuestionAnswers(submission.examId, submission.id!!, submission.answers.toString(), submission.submitDetail)
        )
    }

    @Transactional
    fun updateQuestionScore(submissionId: Long, updateScoreDto: UpdateScoreDto): Boolean {
        val submission = examSubmissionRepository.findById(submissionId).orElse(null)
        
        try {
            // 解析当前的提交详情
            val submitDetail = if (submission.submitDetail.isNullOrBlank()) {
                mutableMapOf<String, Any>()
            } else {
                objectMapper.readValue<MutableMap<String, Any>>(submission.submitDetail!!)
            }
            
            // 更新指定题目的得分
            submitDetail["question_${updateScoreDto.questionId}_score"] = updateScoreDto.score
            
            // 如果是手动更新分数，标记为非自动阅卷（表示已人工干预）
            submitDetail["question_${updateScoreDto.questionId}_auto_graded"] = false
            
            // 重新计算总分
            val totalScore = calculateTotalScore(submitDetail)
            
            // 更新数据库
            val success = examSubmissionRepository.updateSubmissionScore(
                submissionId,
                totalScore,
                objectMapper.writeValueAsString(submitDetail)
            )
            
            return success
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    @Transactional
    fun completeGrading(submissionId: Long): Boolean {
        return examSubmissionRepository.updateGradingStatus(submissionId, true)
    }

    fun getNeedsGradingSubmissions(examId: Long): List<GradingDetailDto> {
        // 首先检查考试是否需要阅卷
        val exam = examRepository.findById(examId) ?: return emptyList()
        if (!exam.needsGrading) return emptyList()
        
        val submissions = examSubmissionRepository.findByExamIdAndIsGraded(examId, false)
        return submissions.map { submission ->
            val student = userRepository.findById(submission.userId).orElse(null)
            
            GradingDetailDto(
                submissionId = submission.id!!,
                studentId = submission.userId,
                studentName = student.username,
                examId = submission.examId,
                examTitle = exam.title,
                submitTime = submission.submitTime,
                isGraded = submission.isGraded,
                totalScore = exam.totalScore,
                currentScore = submission.submitScore ?: 0,
                questions = getQuestionAnswers(submission.examId, submission.id!!, submission.answers.toString(), submission.submitDetail)
            )
        }
    }

    private fun getQuestionAnswers(examId: Long, submissionId: Long, answers: String?, submitDetail: String?): List<QuestionAnswerDto> {
        if (answers.isNullOrBlank()) return emptyList()
        
        try {
            val answersMap = objectMapper.readValue<Map<String, String>>(answers)
            val detailMap = if (submitDetail.isNullOrBlank()) {
                emptyMap<String, Any>()
            } else {
                objectMapper.readValue<Map<String, Any>>(submitDetail)
            }
            
            return answersMap.map { (questionIdStr, studentAnswer) ->
                val questionId = questionIdStr.toLong()
                val question = questionRepository.findById(questionId)
                val scoreKey = "question_${questionId}_score"
                val autoGradedKey = "question_${questionId}_auto_graded"
                val currentScore = (detailMap[scoreKey] as? Number)?.toInt() ?: 0
                val isAutoGraded = detailMap[autoGradedKey] as? Boolean ?: false
                
                // 获取该题在此考试中的实际分数
                val maxScore = examQuestionsRepository.getQuestionScore(examId, questionId) ?: 1
                
                // 对于客观题且答案正确的情况，在阅卷界面自动给满分
                val finalScore = if (isAutoGraded && isObjectiveQuestionCorrect(question, studentAnswer)) {
                    maxScore
                } else {
                    currentScore
                }
                
                QuestionAnswerDto(
                    questionId = questionId,
                    questionContent = question?.content ?: "题目不存在",
                    questionType = question?.type ?: "unknown",
                    correctAnswer = question?.answer,
                    studentAnswer = studentAnswer,
                    maxScore = maxScore,
                    currentScore = finalScore,
                    options = question?.options,
                    isAutoGraded = isAutoGraded // 添加自动阅卷标识
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
    
    /**
     * 检查客观题答案是否正确
     */
    private fun isObjectiveQuestionCorrect(question: ccc.sypw.onlineExamSystem.model.Question?, studentAnswer: String): Boolean {
        if (question?.answer == null) return false
        
        return when (question.type) {
            "single", "true_false" -> {
                studentAnswer.trim().equals(question.answer.trim(), ignoreCase = true)
            }
            "multiple" -> {
                val studentAnswerSet = studentAnswer.split(",").map { it.trim() }.toSet()
                val correctAnswerSet = question.answer.split(",").map { it.trim() }.toSet()
                studentAnswerSet == correctAnswerSet
            }
            else -> false
        }
    }

    private fun calculateTotalScore(submitDetail: Map<String, Any>): Int {
        return submitDetail.entries
            .filter { it.key.endsWith("_score") }
            .sumOf { (it.value as? Number)?.toInt() ?: 0 }
    }
}