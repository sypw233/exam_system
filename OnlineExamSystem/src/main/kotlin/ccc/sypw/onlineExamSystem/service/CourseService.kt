package ccc.sypw.onlineExamSystem.service


import ccc.sypw.onlineExamSystem.model.Course
import ccc.sypw.onlineExamSystem.repository.CourseRepository
import org.springframework.stereotype.Service

@Service
class CourseService(private val courseRepository: CourseRepository) {

    fun getAllCourses(): List<Course> {
        val courseList = courseRepository.findAll().toList()
        return courseList
    }

    fun getCourseById(id: Long): Course? = courseRepository.findById(id).orElse(null)

    fun getCourseByTeacherId(id: Long): List<Course>? = courseRepository.findByTeacherId(id)

    fun createCourse(course: Course): Course = courseRepository.save(course)

    fun updateCourse(id: Long, course: Course): Course {
        if (courseRepository.existsById(id)) {
            return courseRepository.save(course)
        }
        throw IllegalArgumentException("Course not found")
    }

    fun deleteCourse(id: Long) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id)
        } else {
            throw IllegalArgumentException("Course not found")
        }
    }
}