package ccc.sypw.onlineExamSystem.dto


data class UserDataResponse(
    val id: Long?,
    val username: String,
    val role: String,
    val email: String
)