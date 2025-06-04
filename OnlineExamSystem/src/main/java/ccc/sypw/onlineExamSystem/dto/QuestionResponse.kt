package ccc.sypw.onlineExamSystem.dto

import ccc.sypw.onlineExamSystem.model.Question

// 定义返回的数据类
data class QuestionResponse<T>(
    val code: Int,            // 状态码
    val message: String,      // 提示消息
    val data: T  // 试题数据
)

