package ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.Course
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : CrudRepository<Course, Long> {


    @Query("SELECT * FROM courses WHERE teacher_id=:id")
    fun findByTeacherId(id: Long): List<Course>
}