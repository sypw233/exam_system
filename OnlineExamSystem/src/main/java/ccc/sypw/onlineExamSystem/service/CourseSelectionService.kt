package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.model.CourseSelection
import ccc.sypw.onlineExamSystem.repository.CourseSelectionRepository
import org.springframework.stereotype.Service

@Service
class CourseSelectionService(private val courseSelectionRepository: CourseSelectionRepository) {

    fun getAllCourseSelections(): List<CourseSelection> {
        val courseSelections = courseSelectionRepository.findAll().toList()
        println(courseSelections)
        return courseSelections
    }

    fun getCourseSelectionByStudentId(id: Long): List<CourseSelection>? =
        courseSelectionRepository.findCourseSelectionByStudentId(id)?.toList()

    fun getCourseSelectionByCourseId(id: Long): List<CourseSelection>? =
        courseSelectionRepository.findCourseSelectionByCourseId(id)?.toList()

    fun createCourseSelection(courseSelection: CourseSelection): CourseSelection =
        courseSelectionRepository.save(courseSelection)

    fun deleteStudentSelection(courseId: Long, studentId: Long) {
        println("删除课程$courseId,$studentId 的记录")
        courseSelectionRepository.deleteStudentSelection(courseId,studentId)

    }

    fun deleteCourseSelection(id: Long) {
        if (courseSelectionRepository.existsById(id)) {
            courseSelectionRepository.deleteById(id)
        } else {
            throw IllegalArgumentException("Course selection not found")
        }
    }
}