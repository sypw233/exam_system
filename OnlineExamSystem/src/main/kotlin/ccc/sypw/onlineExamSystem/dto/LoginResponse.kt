package ccc.sypw.onlineExamSystem.dto

data class LoginResponse(
    val username: String,
    val password: String,

) {
    override fun toString(): String {
        return "LoginRequest(username='$username', password='$password')"
    }
}
