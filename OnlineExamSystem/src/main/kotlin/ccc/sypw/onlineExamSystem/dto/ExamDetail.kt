package ccc.sypw.onlineExamSystem.dto

import ccc.sypw.onlineExamSystem.model.Question
import java.time.LocalDateTime


data class ExamDetail(
    val id: Long?, // 试卷编号
    val title: String, // 试卷名称*
    val description: String?, // 试卷描述
    val startTime: LocalDateTime, // 开始时间*
    val endTime: LocalDateTime, // 结束时间*
    val creatorId: Long,   //创建者id *
    val createTime: LocalDateTime?,
    val totalScore: Int, // 满分*
    var questions:List<Question>?

)
