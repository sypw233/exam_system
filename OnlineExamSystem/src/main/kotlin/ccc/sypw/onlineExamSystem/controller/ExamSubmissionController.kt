package ccc.sypw.onlineExamSystem.controller


import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.ExamSubmissionResponse
import ccc.sypw.onlineExamSystem.model.ExamSubmission
import ccc.sypw.onlineExamSystem.repository.ExamSubmissionRepository
import ccc.sypw.onlineExamSystem.service.ExamService
import ccc.sypw.onlineExamSystem.service.ExamSubmissionService
import ccc.sypw.onlineExamSystem.service.UserService
import ccc.sypw.onlineExamSystem.util.ExamGradingUtils
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@RestController
@RequestMapping("/api/exam-submissions")
class ExamSubmissionController(
    private val examSubmissionService: ExamSubmissionService,
    private val examGradingUtils: ExamGradingUtils,
    private val examService: ExamService,
    private val userService: UserService
) {

    // 查询某个考试的所有提交记录
    @GetMapping("/exam/{examId}")
    fun getExamSubmissionsByExam(
        @PathVariable examId: Long
    ): ResponseEntity<List<ExamSubmissionResponse>> {
        val submissions = examSubmissionService.getSubmissionsByExam(examId)
        return if (submissions.isNotEmpty()) {
            ResponseEntity(submissions, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }

    // 查询所有提交记录
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

    // 查询某个用户的所有考试提交记录
    @GetMapping("/user/{userId}")
    fun getExamSubmissionsByUser(
        @PathVariable userId: Long
    ): ResponseEntity<List<ExamSubmissionResponse>> {
        val submissions = examSubmissionService.getSubmissionsByUser(userId)
        return if (submissions.isNotEmpty()) {
            ResponseEntity(submissions, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NO_CONTENT)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteExamSubmission(@PathVariable id: Long): ResponseEntity<Any> {
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

    // 提交考试记录（假设考试提交记录是通过 POST 请求提交）
    @PostMapping("/submit")
    fun submitExam(@RequestBody examSubmission: ExamSubmission): ResponseEntity<ExamSubmission> {
        // 使用改进的自动评分系统评分
        val gradeExam = examGradingUtils.gradeExam(
            examSubmission.answers as Map<String, Any>,
            examSubmission.examId // 传递考试ID以获取题目分数
        )
        
        val updatedExamSubmission = examSubmission.copy(
            answers = jacksonObjectMapper().writeValueAsString(examSubmission.answers),
            submitScore = gradeExam.first,
            submitDetail = jacksonObjectMapper().writeValueAsString(gradeExam.second)
        )
        println("考试数据提交处理结果$updatedExamSubmission")
        val savedSubmission = examSubmissionService.submitExam(updatedExamSubmission)
        return ResponseEntity(savedSubmission, HttpStatus.CREATED)
    }
    
    // 导出某次考试的成绩（Excel格式）
    @GetMapping("/export/exam/{examId}")
    fun exportExamResults(@PathVariable examId: Long): ResponseEntity<ByteArray> {
        // 获取考试信息
        val exam = examService.findById(examId) ?: return ResponseEntity.notFound().build()
        
        // 获取考试提交记录
        val submissions = examSubmissionService.getSubmissionsByExam(examId)
        if (submissions.isEmpty()) {
            return ResponseEntity.noContent().build()
        }
        
        // 创建Excel工作簿
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("考试成绩")
        
        // 创建标题行
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("学号")
        headerRow.createCell(1).setCellValue("姓名")
        headerRow.createCell(2).setCellValue("得分")
        headerRow.createCell(3).setCellValue("满分")
        headerRow.createCell(4).setCellValue("提交时间")
        
        // 填充数据行
        submissions.forEachIndexed { index, submission ->
            val row = sheet.createRow(index + 1)
            
            // 获取用户信息
            val user = userService.getUserById(submission.userId)
            
            row.createCell(0).setCellValue(submission.userId.toString())
            row.createCell(1).setCellValue(user?.username ?: "未知")
            submission.submissionScore?.let { row.createCell(2).setCellValue(it.toDouble()) }
            row.createCell(3).setCellValue(exam.totalScore.toDouble())
            row.createCell(4).setCellValue(submission.submitTime?.toString() ?: "")
        }
        
        // 调整列宽
        for (i in 0..4) {
            sheet.autoSizeColumn(i)
        }
        
        // 转换为字节数组
        val baos = ByteArrayOutputStream()
        workbook.write(baos)
        workbook.close()
        
        // 构建响应
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "exam_${examId}_results_$timestamp.xlsx"
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.setContentDispositionFormData("attachment", fileName)
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(baos.toByteArray())
    }
}
