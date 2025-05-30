package ccc.sypw.onlineExamSystem.dto

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)