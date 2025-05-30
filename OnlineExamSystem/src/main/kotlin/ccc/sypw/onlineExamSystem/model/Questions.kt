package ccc.sypw.onlineExamSystem.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("questions")
data class Question(
    @Id
    val id: Long? = null,              // 问题ID
    val content: String,               // 问题内容
    val type: String,            // 问题类型
    var options: Any?, // 选项（JSON格式）
    val answer: String?,                // 答案
    val difficulty: String,   // 难度
    val category: String,             // 分类
    @Column("create_time")
    val createTime: LocalDateTime?,            // 创建时间
    @Column("creator_id")
    val creatorId: Long               // 创建者ID
)


