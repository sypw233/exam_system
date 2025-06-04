package ccc.sypw.onlineExamSystem.dto


data class CreateUserResponse(
    val username: String,
    val password: String,
    val email: String,
    val role: String
)
