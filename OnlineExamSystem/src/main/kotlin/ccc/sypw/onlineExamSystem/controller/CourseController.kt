package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.model.Course
import ccc.sypw.onlineExamSystem.service.CourseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/courses")
@Tag(name = "课程管理", description = "课程相关的API接口")
class CourseController(private val courseService: CourseService) {

    /**
     * 获取所有课程
     */
    @Operation(summary = "获取所有课程", description = "获取系统中所有课程的列表")
    @GetMapping
    fun getAllCourses(): List<Course> {
        val allCourses = courseService.getAllCourses()
        return allCourses
    }

    /**
     * 根据教师ID获取课程
     */
    @Operation(summary = "获取教师课程", description = "根据教师ID获取该教师的所有课程")
    @GetMapping("/teacher/{id}")
    fun getCourseByTeacherId(
        @Parameter(description = "教师ID", required = true) @PathVariable id: Long
    ): List<Course>? = courseService.getCourseByTeacherId(id)

    /**
     * 根据ID获取课程
     */
    @Operation(summary = "根据ID获取课程", description = "根据课程ID获取单个课程的详细信息")
    @GetMapping("/{id}")
    fun getCourseById(
        @Parameter(description = "课程ID", required = true) @PathVariable id: Long
    ): Course? = courseService.getCourseById(id)

    /**
     * 创建课程
     */
    @Operation(summary = "创建课程", description = "创建一个新的课程")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourse(@RequestBody course: Course): Course = courseService.createCourse(course)

    /**
     * 更新课程
     */
    @Operation(summary = "更新课程", description = "根据课程ID更新课程信息")
    @PutMapping("/{id}")
    fun updateCourse(
        @Parameter(description = "课程ID", required = true) @PathVariable id: Long,
        @RequestBody course: Course
    ): Course = courseService.updateCourse(id, course)

    /**
     * 删除课程
     */
    @Operation(summary = "删除课程", description = "根据课程ID删除指定课程")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourse(
        @Parameter(description = "课程ID", required = true) @PathVariable id: Long
    ) {
        courseService.deleteCourse(id)
    }
}