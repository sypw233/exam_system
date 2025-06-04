package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.service.GradingService
import ccc.sypw.onlineExamSystem.dto.GradingDetailDto
import ccc.sypw.onlineExamSystem.dto.UpdateScoreDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/grading")
@Tag(name = "阅卷管理", description = "考试阅卷相关的API接口")
class GradingController(private val gradingService: GradingService) {

    @GetMapping("/exam/{examId}/submissions")
    @Operation(summary = "获取考试的所有提交", description = "获取指定考试的所有学生提交记录")
    fun getExamSubmissions(
        @Parameter(description = "考试ID") @PathVariable examId: Long
    ): ResponseEntity<List<GradingDetailDto>> {
        val submissions = gradingService.getExamSubmissions(examId)
        return ResponseEntity.ok(submissions)
    }

    @GetMapping("/submission/{submissionId}")
    @Operation(summary = "获取提交详情", description = "获取指定提交的详细信息，包括试题和答案")
    fun getSubmissionDetail(
        @Parameter(description = "提交ID") @PathVariable submissionId: Long
    ): ResponseEntity<GradingDetailDto> {
        val detail = gradingService.getSubmissionDetail(submissionId)
        return if (detail != null) {
            ResponseEntity.ok(detail)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/submission/{submissionId}/score")
    @Operation(summary = "更新单题得分", description = "更新指定提交中某道题的得分")
    fun updateQuestionScore(
        @Parameter(description = "提交ID") @PathVariable submissionId: Long,
        @RequestBody updateScoreDto: UpdateScoreDto
    ): ResponseEntity<String> {
        val success = gradingService.updateQuestionScore(submissionId, updateScoreDto)
        return if (success) {
            ResponseEntity.ok("得分更新成功")
        } else {
            ResponseEntity.badRequest().body("得分更新失败")
        }
    }

    @PutMapping("/submission/{submissionId}/complete")
    @Operation(summary = "完成阅卷", description = "标记指定提交的阅卷为完成状态")
    fun completeGrading(
        @Parameter(description = "提交ID") @PathVariable submissionId: Long
    ): ResponseEntity<String> {
        val success = gradingService.completeGrading(submissionId)
        return if (success) {
            ResponseEntity.ok("阅卷完成")
        } else {
            ResponseEntity.badRequest().body("阅卷完成失败")
        }
    }

    @GetMapping("/exam/{examId}/needs-grading")
    @Operation(summary = "获取需要阅卷的提交", description = "获取指定考试中需要阅卷的提交记录")
    fun getNeedsGradingSubmissions(
        @Parameter(description = "考试ID") @PathVariable examId: Long
    ): ResponseEntity<List<GradingDetailDto>> {
        val submissions = gradingService.getNeedsGradingSubmissions(examId)
        return ResponseEntity.ok(submissions)
    }
}