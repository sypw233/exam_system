package ccc.sypw.onlineExamSystem.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("courses")
data class Course(
    @Id
    val id: Long? = null,  // 如果是自动生成的字段，建议使用 Long? 类型
    val courseName: String,
    val teacherId: Long
)
