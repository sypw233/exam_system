package ccc.sypw.onlineExamSystem.dto

data class ExamSubmissionResponse(
    val id: Long?,
    val userId: Long,
    val examId: Long,
    val userName: String?,
    val examTitle: String?,
    val examTotalScore: Int?,
    val submissionScore: Int?,
    val answers: Any?,
    val isGraded:Boolean
)