package ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.CourseSelection
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseSelectionRepository : CrudRepository<CourseSelection, Long>{

    @Query("SELECT * FROM course_selections WHERE student_id=:id")
    fun findCourseSelectionByStudentId(id:Long):List<CourseSelection>?

    @Query("SELECT * FROM course_selections WHERE course_id=:id")
    fun findCourseSelectionByCourseId(id:Long):List<CourseSelection>?

    @Modifying
    @Query("DELETE FROM course_selections WHERE course_id=:courseId AND student_id=:studentId")
    fun deleteStudentSelection(courseId:Long,studentId:Long)
}