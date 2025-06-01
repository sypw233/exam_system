package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.CreateUserResponse
import ccc.sypw.onlineExamSystem.dto.LoginResponse
import ccc.sypw.onlineExamSystem.dto.UserDataResponse
import ccc.sypw.onlineExamSystem.exception.InvalidCredentialsException
import ccc.sypw.onlineExamSystem.exception.UserEmailAlreadyExistsException
import ccc.sypw.onlineExamSystem.exception.UserNameAlreadyTakenException

import ccc.sypw.onlineExamSystem.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关的API接口")
class UserController @Autowired constructor(
    private val userService: UserService
) {

    /**
     * 获取所有用户列表
     */
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户的列表")
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserDataResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users.map { UserDataResponse(it.id, it.username, it.role, it.email) })
    }

    /**
     * 获取所有教师用户
     */
    @Operation(summary = "获取所有教师", description = "获取系统中所有教师用户的列表")
    @GetMapping("teachers")
    fun getAllTeacherUsers(): ResponseEntity<List<UserDataResponse>> {
        val users = userService.getAllTeacherUsers()
        return ResponseEntity.ok(users.map { UserDataResponse(it.id, it.username, it.role, it.email) })
    }

    /**
     * 获取所有学生用户
     */
    @Operation(summary = "获取所有学生", description = "获取系统中所有学生用户的列表")
    @GetMapping("students")
    fun getAllStudentsUsers(): ResponseEntity<List<UserDataResponse>> {
        val users = userService.getAllStudentUsers()
        return ResponseEntity.ok(users.map { UserDataResponse(it.id, it.username, it.role, it.email) })
    }


    /**
     * 根据ID获取单个用户信息
     */
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取单个用户的详细信息")
    @GetMapping("/{id}")
    fun getUserById(@Parameter(description = "用户ID", required = true) @PathVariable id: Long): ResponseEntity<UserDataResponse> {

        val user = userService.getUserById(id)
        return ResponseEntity.ok(user?.let { UserDataResponse(user.id, it.username, user.role, user.email) })

    }

    /**
     * 创建新用户
     */
    @Operation(summary = "创建用户", description = "创建一个新的用户账户")
    @PostMapping
    fun createUser(@RequestBody createUserResponse: CreateUserResponse): ResponseEntity<String> {
        val createdUser = userService.createUser(createUserResponse)
        return ResponseEntity.status(HttpStatus.CREATED).body("User ${createdUser.username} created successfully")
    }

    /**
     * 用户注册接口
     */
    @Operation(summary = "用户注册", description = "新用户注册账户")
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

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户的详细信息")
    @PutMapping("/{id}")
    fun updateUser(@Parameter(description = "用户ID", required = true) @PathVariable id: Long, @RequestBody userDataResponse: UserDataResponse): ResponseEntity<String> {
        val updatedUser = userService.updateUser(id, userDataResponse)
        return if (updatedUser != null) {
            ResponseEntity.ok("User ${updatedUser.username} updated successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id $id not found")
        }
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除指定用户")
    @DeleteMapping("/{id}")
    fun deleteUser(@Parameter(description = "用户ID", required = true) @PathVariable id: Long): ResponseEntity<String> {
        val result = userService.deleteUser(id)
        return if (result) {
            ResponseEntity.ok("User with id $id deleted successfully")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with id $id not found")
        }
    }

    /**
     * 用户登录接口
     */
    @Operation(summary = "用户登录", description = "用户使用用户名和密码进行登录认证")
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
