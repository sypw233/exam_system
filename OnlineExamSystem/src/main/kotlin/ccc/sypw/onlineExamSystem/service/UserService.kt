package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.dto.CreateUserResponse
import ccc.sypw.onlineExamSystem.dto.LoginResponse
import ccc.sypw.onlineExamSystem.dto.UserDataResponse
import ccc.sypw.onlineExamSystem.exception.InvalidCredentialsException
import ccc.sypw.onlineExamSystem.exception.UserEmailAlreadyExistsException

import ccc.sypw.onlineExamSystem.model.User
import ccc.sypw.onlineExamSystem.repository.UserRepository
import ccc.sypw.onlineExamSystem.util.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository
) {

    // 获取所有用户
    fun getAllUsers(): List<User> {
        return userRepository.findAll().toList()
    }

    fun getAllTeacherUsers(): List<User> {
        return userRepository.findAllTeacher()
    }

    fun getAllStudentUsers(): List<User> {
        return userRepository.findAllStudent()
    }

    // 根据 ID 获取用户
    fun getUserById(id: Long): User? {
        val user = userRepository.findById(id)
        return user.orElse(null)
    }

    fun getUserByUserName(userName: String): User? {
        return userRepository.findByUsername(userName)
    }

    fun isUsernameOrEmailTaken(username: String, email: String): Boolean {
        val existingUser = userRepository.findByUsername(username)
        val existingEmail = userRepository.findByEmail(email)
        return existingUser != null || existingEmail != null
    }

    fun createUser(createUserResponse: CreateUserResponse): UserDataResponse {
        if (isUsernameOrEmailTaken(createUserResponse.username, createUserResponse.email)) {
            throw UserEmailAlreadyExistsException("Username or email is already taken")
        }

        // 创建 User 实体对象
        val user = User(
            username = createUserResponse.username,
            password = createUserResponse.password,
            email = createUserResponse.email,
            role = createUserResponse.role
        )

        // 保存用户并返回 UserDTO
        val savedUser = userRepository.save(user)
        return UserDataResponse(savedUser.id, savedUser.username, savedUser.role, savedUser.email)
    }


    // 更新用户
    fun updateUser(id: Long, userDataResponse: UserDataResponse): User? {
        val existingUser = userRepository.findById(id).orElse(null)
        return if (existingUser != null) {
            val updatedUser = existingUser.copy(
                username = userDataResponse.username,
                email = userDataResponse.email,
                role = userDataResponse.role
            )
            userRepository.save(updatedUser)
        } else {
            null
        }
    }

    // 删除用户
    fun deleteUser(id: Long): Boolean {
        val user = userRepository.findById(id).orElse(null)
        return if (user != null) {
            userRepository.delete(user)
            true
        } else {
            false
        }
    }

    // 用户登录
    fun login(loginResponse: LoginResponse): Map<String, Any?>? {
        // 检查用户名和密码是否为空
        if (loginResponse.username.isBlank() || loginResponse.password.isBlank()) {
            throw InvalidCredentialsException("Username or password is empty")
        }

        // 查找用户
        val user = userRepository.findByUsername(loginResponse.username)
            ?: throw InvalidCredentialsException("User not found")

        println("查找到用户$user")
        // 校验密码
        if (user.password == loginResponse.password) {
            // 登录成功，返回 JWT token
            if (user.id != null) {
                println("userId:" + user.id)
                val token = JwtUtils.generateToken(user.username, user.role)
                val response = mapOf(
                    "message" to "Login successful",
                    "token" to token, // 这里可以根据实际需要生成真实的 token
                    "username" to user.username,
                    "role" to user.role, // 如果有角色字段，可以返回角色信息
                    "id" to user.id
                )
                return response
            } else {
                return null
            }
        } else {
            // 密码错误
            throw InvalidCredentialsException("Invalid password")
        }
    }
}
