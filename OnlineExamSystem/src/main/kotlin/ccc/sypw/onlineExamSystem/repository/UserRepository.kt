package ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE role='teacher'")
    fun findAllTeacher(): List<User>

    @Query("SELECT * FROM users WHERE role='student'")
    fun findAllStudent(): List<User>

    @Query("SELECT username FROM users WHERE id = :userid")
    fun findUsernameById(userid: Long): String?
}

