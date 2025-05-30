package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.CreateUserResponse
import ccc.sypw.onlineExamSystem.dto.LoginResponse
import ccc.sypw.onlineExamSystem.dto.UserDataResponse
import ccc.sypw.onlineExamSystem.exception.InvalidCredentialsException
import ccc.sypw.onlineExamSystem.exception.UserEmailAlreadyExistsException
import ccc.sypw.onlineExamSystem.exception.UserNameAlreadyTakenException

import ccc.sypw.onlineExamSystem.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController @Autowired constructor(
    private val userService: UserService
) {

    // 获取所有用户
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDataResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users.map { UserDataResponse(it.id, it.username, it.role, it.email) })
    }

    // 获取所有教师用户
    @GetMapping("teachers")
    fun getAllTeacherUsers(): ResponseEntity<List<UserDataResponse>> {
        val users = userService.getAllTeacherUsers()
        return ResponseEntity.ok(users.map { UserDataResponse(it.id, it.username, it.role, it.email) })
    }

    // 获取所有学生用户
    @GetMapping("students")
    fun getAllStudentsUsers(): ResponseEntity<List<UserDataResponse>> {
        val users = userService.getAllStudentUsers()
        return ResponseEntity.ok(users.map { UserDataResponse(it.id, it.username, it.role, it.email) })
    }


    // 根据 ID 获取单个用户
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDataResponse> {

        val user = userService.getUserById(id)
        return ResponseEntity.ok(user?.let { UserDataResponse(user.id, it.username, user.role, user.email) })

    }

    // 创建新用户
    @PostMapping
    fun createUser(@RequestBody createUserResponse: CreateUserResponse): ResponseEntity<String> {
        val createdUser = userService.createUser(createUserResponse)
        return ResponseEntity.status(HttpStatus.CREATED).body("User ${createdUser.username} created successfully")
    }

    // 注册接口
    @PostMapping("/register")
    fun register(@RequestBody createUserResponse: CreateUserResponse): ResponseEntity<Any> {
        return try {
            val userDTO = userService.createUser(createUserResponse)
            ResponseEntity.status(HttpStatus.CREATED).body(userDTO)
        } catch (e: UserEmailAlreadyExistsException) {
            // 捕获邮箱已存在的异常，返回 400 错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
        } catch (e: UserNameAlreadyTakenException) {
            // 捕获用户名已存在的异常，返回 400 错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            // 捕获其他未知异常，返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "An unexpected error occurred"))
        }
    }

    // 更新用户信息
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody userDataResponse: UserDataResponse): ResponseEntity<String> {
        val updatedUser = userService.updateUser(id, userDataResponse)
        return if (updatedUser != null) {
            ResponseEntity.ok("User ${updatedUser.username} updated successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id $id not found")
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<String> {
        val result = userService.deleteUser(id)
        return if (result) {
            ResponseEntity.ok("User with id $id deleted successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id $id not found")
        }
    }

    // 登录接口
    @PostMapping("/login")
    fun login(@RequestBody loginResponse: LoginResponse): ResponseEntity<Any> {
        return try {
            val user = userService.login(loginResponse)
            ResponseEntity.ok(user)
        } catch (e: InvalidCredentialsException) {
            // 返回 400 错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            // 捕获其他未知异常，返回 500 错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "An unexpected error occurred"))
        }
    }

}
