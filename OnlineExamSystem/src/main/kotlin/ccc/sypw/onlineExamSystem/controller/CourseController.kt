package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.model.Course
import ccc.sypw.onlineExamSystem.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/courses")
class CourseController(private val courseService: CourseService) {

    @GetMapping
    fun getAllCourses(): List<Course> {
        val allCourses = courseService.getAllCourses()
        return allCourses
    }

    @GetMapping("/teacher/{id}")
    fun getCourseByTeacherId(@PathVariable id: Long): List<Course>? =
        courseService.getCourseByTeacherId(id)

    @GetMapping("/{id}")
    fun getCourseById(@PathVariable id: Long): Course? = courseService.getCourseById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourse(@RequestBody course: Course): Course = courseService.createCourse(course)

    @PutMapping("/{id}")
    fun updateCourse(@PathVariable id: Long, @RequestBody course: Course): Course =
        courseService.updateCourse(id, course)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourse(@PathVariable id: Long) {
        courseService.deleteCourse(id)
    }
}