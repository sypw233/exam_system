package ccc.sypw.onlineExamSystem.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course_selections")
data class CourseSelection(
    @Id
    val id: Long? = null,
    val studentId: Long,
    val courseId: Long
)