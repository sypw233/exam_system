package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.model.CourseSelection
import ccc.sypw.onlineExamSystem.service.CourseSelectionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/course-selections")
class CourseSelectionController(private val courseSelectionService: CourseSelectionService) {

    @GetMapping
    fun getAllCourseSelections(): List<CourseSelection> {
        return courseSelectionService.getAllCourseSelections()
    }


    //    根据学生id获取其选课记录
    @GetMapping("/student/{id}")
    fun getCourseSelectionByStudentId(@PathVariable id: Long): List<CourseSelection>? =
        courseSelectionService.getCourseSelectionByStudentId(id)

    //    根据课程id获取所有学生
    @GetMapping("/course/{id}")
    fun getCourseSelectionByCourseId(@PathVariable id: Long): List<CourseSelection>? =
        courseSelectionService.getCourseSelectionByCourseId(id)


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCourseSelection(@RequestBody courseSelection: CourseSelection): CourseSelection =
        courseSelectionService.createCourseSelection(courseSelection)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourseSelection(@PathVariable id: Long) {
        courseSelectionService.deleteCourseSelection(id)
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteStudentSelection(@PathVariable courseId: Long,@PathVariable studentId: Long) {
        courseSelectionService.deleteStudentSelection(courseId, studentId)
    }
}