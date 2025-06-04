package ccc.sypw.onlineExamSystem.dto

import java.time.LocalDateTime

data class GradingDetailDto(
    val submissionId: Long,
    val studentId: Long,
    val studentName: String?,
    val examId: Long,
    val examTitle: String,
    val submitTime: LocalDateTime,
    val isGraded: Boolean,
    val totalScore: Int,
    val currentScore: Int,
    val questions: List<QuestionAnswerDto>
)

data class QuestionAnswerDto(
    val questionId: Long,
    val questionContent: String,
    val questionType: String,
    val correctAnswer: String?,
    val studentAnswer: String?,
    val maxScore: Int,
    val currentScore: Int,
    val options: Any? = null,  // 选择题选项（单选题、多选题）
    val isAutoGraded: Boolean = false  // 是否为自动阅卷
)

data class UpdateScoreDto(
    val questionId: Long,
    val score: Int
)