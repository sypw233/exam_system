package ccc.sypw.onlineExamSystem.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("exam_submissions")
data class ExamSubmission(
    @Id
    var id: Long? = null,
    val userId: Long,
    val examId: Long,
    var answers: Any?,  // 存储为 JSON 字符串
    var submitScore:Int?,
    var submitDetail:String?,  //Map<String, Boolean>? => JSON字符串
    val submitTime: LocalDateTime = LocalDateTime.now(),
    var isGraded: Boolean = false // 是否已阅卷
)
