package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.model.CourseSelection
import ccc.sypw.onlineExamSystem.service.CourseSelectionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/course-selections")
@Tag(name = "选课管理", description = "选课记录相关的API接口")
class CourseSelectionController(private val courseSelectionService: CourseSelectionService) {

    /**
     * 获取所有选课记录
     */
    @Operation(summary = "获取所有选课记录", description = "获取系统中所有的选课记录")
    @GetMapping
    fun getAllCourseSelections(): List<CourseSelection> {
        return courseSelectionService.getAllCourseSelections()
    }

    /**
     * 根据学生ID获取选课记录
     */
    @Operation(summary = "获取学生选课记录", description = "根据学生ID获取该学生的所有选课记录")
    @GetMapping("/student/{id}")
    fun getCourseSelectionByStudentId(
        @Parameter(description = "学生ID", required = true) @PathVariable id: Long
    ): List<CourseSelection>? = courseSelectionService.getCourseSelectionByStudentId(id)

    /**
     * 根据课程ID获取选课记录
     */
    @Operation(summary = "获取课程选课记录", description = "根据课程ID获取选择该课程的所有学生记录")
    @GetMapping("/course/{id}")
    fun getCourseSelectionByCourseId(
        @Parameter(description = "课程ID", required = true) @PathVariable id: Long
    ): List<CourseSelection>? = courseSelectionService.getCourseSelectionByCourseId(id)

    /**
     * 创建选课记录
     */
    @Operation(summary = "创建选课记录", description = "创建一个新的选课记录")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourseSelection(@RequestBody courseSelection: CourseSelection): CourseSelection =
        courseSelectionService.createCourseSelection(courseSelection)

    /**
     * 删除选课记录
     */
    @Operation(summary = "删除选课记录", description = "根据选课记录ID删除指定的选课记录")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourseSelection(
        @Parameter(description = "选课记录ID", required = true) @PathVariable id: Long
    ) {
        courseSelectionService.deleteCourseSelection(id)
    }

    /**
     * 删除学生选课
     */
    @Operation(summary = "删除学生选课", description = "根据课程ID和学生ID删除指定的选课记录")
    @DeleteMapping("/{courseId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteStudentSelection(
        @Parameter(description = "课程ID", required = true) @PathVariable courseId: Long,
        @Parameter(description = "学生ID", required = true) @PathVariable studentId: Long
    ) {
        courseSelectionService.deleteStudentSelection(courseId, studentId)
    }
}