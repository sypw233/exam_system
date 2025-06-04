package ccc.sypw.onlineExamSystem.controller


import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.ExamSubmissionResponse
import ccc.sypw.onlineExamSystem.model.ExamSubmission
import ccc.sypw.onlineExamSystem.repository.ExamSubmissionRepository
import ccc.sypw.onlineExamSystem.service.ExamSubmissionService
import ccc.sypw.onlineExamSystem.util.ExamGradingUtils
import ccc.sypw.onlineExamSystem.util.ExcelExportUtils
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@RestController
@RequestMapping("/api/exam-submissions")
@Tag(name = "考试提交管理", description = "考试提交记录相关的API接口")
class ExamSubmissionController(
    private val examSubmissionService: ExamSubmissionService,
    private val examGradingUtils: ExamGradingUtils,
    private val excelExportUtils: ExcelExportUtils
) {

    /**
     * 查询某个考试的所有提交记录
     */
    @Operation(summary = "获取考试提交记录", description = "根据考试ID获取该考试的所有提交记录")
    @GetMapping("/exam/{examId}")
    fun getExamSubmissionsByExam(
        @Parameter(description = "考试ID", required = true) @PathVariable examId: Long
    ): ResponseEntity<List<ExamSubmissionResponse>> {
        val submissions = examSubmissionService.getSubmissionsByExam(examId)
        return if (submissions.isNotEmpty()) {
            ResponseEntity(submissions, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }

    /**
     * 查询所有提交记录
     */
    @Operation(summary = "获取所有提交记录", description = "获取系统中所有的考试提交记录")
    @GetMapping("/all")
    fun getAllExamSubmissionsByExam(
    ): ResponseEntity<List<ExamSubmissionResponse>> {
        val submissions = examSubmissionService.getAllSubmissions()
        println(submissions)
        return if (submissions.isNotEmpty()) {
            ResponseEntity(submissions, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }

    /**
     * 查询某个用户的所有考试提交记录
     */
    @Operation(summary = "获取用户提交记录", description = "根据用户ID获取该用户的所有考试提交记录")
    @GetMapping("/user/{userId}")
    fun getExamSubmissionsByUser(
        @Parameter(description = "用户ID", required = true) @PathVariable userId: Long
    ): ResponseEntity<List<ExamSubmissionResponse>> {
        val submissions = examSubmissionService.getSubmissionsByUser(userId)
        return if (submissions.isNotEmpty()) {
            ResponseEntity(submissions, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }

    /**
     * 删除考试提交记录
     */
    @Operation(summary = "删除提交记录", description = "根据提交记录ID删除指定的考试提交记录")
    @DeleteMapping("/{id}")
    fun deleteExamSubmission(
        @Parameter(description = "提交记录ID", required = true) @PathVariable id: Long
    ): ResponseEntity<Any> {
        println("ExamSubmission尝试删除$id")
        examSubmissionService.deleteExamSubmission(id)
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = null
            )
        )
    }

    /**
     * 提交考试记录
     */
    @Operation(summary = "提交考试", description = "提交考试答案并自动评分")
    @PostMapping("/submit")
    fun submitExam(@RequestBody examSubmission: ExamSubmission): ResponseEntity<ExamSubmission> {
        // 使用考试ID和答案进行自动评分
        val gradeResult = examGradingUtils.gradeExamWithAutoGrading(examSubmission.examId, examSubmission.answers as Map<String, Any>)
        
        val updatedExamSubmission = examSubmission.copy(
            answers = jacksonObjectMapper().writeValueAsString(examSubmission.answers),
            submitScore = gradeResult.totalScore,
            submitDetail = jacksonObjectMapper().writeValueAsString(gradeResult.detailScores),
            isGraded = gradeResult.isFullyGraded // 如果只有客观题则自动设置为已阅卷
        )
        println("考试数据提交处理结果$updatedExamSubmission")
        val savedSubmission = examSubmissionService.submitExam(updatedExamSubmission)
        return ResponseEntity(savedSubmission, HttpStatus.CREATED)
    }

    /**
     * 导出考试成绩为Excel文件
     */
    @Operation(summary = "导出考试成绩", description = "导出指定考试的所有学生成绩为Excel文件")
    @GetMapping("/exam/{examId}/export")
    fun exportExamScores(
        @Parameter(description = "考试ID", required = true) @PathVariable examId: Long
    ): ResponseEntity<ByteArray> {
        // 获取考试提交记录
        val submissions = examSubmissionService.getSubmissionsByExam(examId)
        
        if (submissions.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        
        // 获取考试标题
        val examTitle = submissions.firstOrNull()?.examTitle ?: "未知考试"
        
        // 生成Excel文件
        val excelData = excelExportUtils.exportExamScoresToExcel(submissions, examTitle)
        val fileName = excelExportUtils.generateFileName(examTitle)
        
        // 设置响应头
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.setContentDispositionFormData("attachment", fileName)
        headers.cacheControl = "must-revalidate, post-check=0, pre-check=0"
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(excelData)
    }

}
