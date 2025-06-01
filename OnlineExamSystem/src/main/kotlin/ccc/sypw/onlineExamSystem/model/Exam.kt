package ccc.sypw.onlineExamSystem.model

import org.springframework.data.annotation.Id

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("exams")
data class Exams(
    @Id
    val id: Long?, // 试卷编号
    val title: String, // 试卷名称*
    val description: String?, // 试卷描述
    val startTime: LocalDateTime, // 开始时间*
    val endTime: LocalDateTime, // 结束时间*
    val creatorId: Long,   //创建者id *
    val createTime: LocalDateTime?,
    val totalScore: Int, // 满分*
    val questions:List<Long>?,
    val courseId :Long,
    val needsGrading: Boolean = false // 是否需要阅卷
)
