package ccc.sypw.onlineExamSystem.model

import org.springframework.data.relational.core.mapping.Table

@Table("exam_questions")
data class ExamQuestions(
    val examId: Long,
    val questionId: Long

)
