package ccc.sypw.onlineExamSystem.controller


import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.ExamSubmissionResponse
import ccc.sypw.onlineExamSystem.model.ExamSubmission
import ccc.sypw.onlineExamSystem.repository.ExamSubmissionRepository
import ccc.sypw.onlineExamSystem.service.ExamService
import ccc.sypw.onlineExamSystem.service.ExamSubmissionService
import ccc.sypw.onlineExamSystem.service.UserService
import ccc.sypw.onlineExamSystem.service.QuestionService
import ccc.sypw.onlineExamSystem.util.ExamGradingUtils
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

data class ManualGradingRequest(
    val examId: Long,
    val questionGrades: Map<Long, QuestionGradeInfo>
)

data class QuestionGradeInfo(
    val score: Int,
    val feedback: String,
    val graderId: Long
)

@RestController
@RequestMapping("/api/exam-submissions")
class ExamSubmissionController(
    private val examSubmissionService: ExamSubmissionService,
    private val examGradingUtils: ExamGradingUtils,
    private val examService: ExamService,
    private val userService: UserService,
    private val questionService: QuestionService
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
            ApiResponse<Any?>(  // 将Any改为Any?，表示可空类型
                code = HttpStatus.OK.value(),
                message = "success",
                data = null
            )
        )
    }

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
        
        // 获取原始提交记录以获取提交时间
        val originalSubmissions = examSubmissionService.getOriginalSubmissionsByExam(examId)
        
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
            
            // 获取对应的原始提交记录
            val originalSubmission = originalSubmissions.find { it.userId == submission.userId }
            
            row.createCell(0).setCellValue(submission.userId.toString())
            row.createCell(1).setCellValue(user?.username ?: "未知")
            submission.submissionScore?.let { row.createCell(2).setCellValue(it.toDouble()) }
            row.createCell(3).setCellValue(exam.totalScore.toDouble())
            row.createCell(4).setCellValue(originalSubmission?.submitTime?.toString() ?: "未知")
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
    
    /**
     * 查看考试详情，包括学生做题情况
     * @param examId 考试ID
     * @return 考试详情和学生答题情况
     */
    @GetMapping("/exam/{examId}/details")
    fun getExamDetails(@PathVariable examId: Long): ResponseEntity<Map<String, Any>> {
        try {
            // 获取考试信息
            val exam = examService.findById(examId) 
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "考试不存在"))
            
            // 获取考试的所有提交记录
            val submissions = examSubmissionService.getOriginalSubmissionsByExam(examId)
            
            // 获取考试题目
            val examQuestions = exam.questions ?: emptyList()
            
            // 构建学生答题详情
            val studentDetails = submissions.map { submission ->
                val user = userService.getUserById(submission.userId)
                val answersMap = try {
                    jacksonObjectMapper().readValue<Map<String, Any>>(submission.answers.toString())
                } catch (e: Exception) {
                    emptyMap<String, Any>()
                }
                
                val submitDetailMap = try {
                    jacksonObjectMapper().readValue<Map<String, Any>>(submission.submitDetail ?: "{}")
                } catch (e: Exception) {
                    emptyMap<String, Any>()
                }
                
                mapOf(
                    "studentId" to submission.userId,
                    "studentName" to (user?.username ?: "未知学生"),
                    "submitTime" to submission.submitTime,
                    "totalScore" to (submission.submitScore ?: 0),
                    "answers" to answersMap,
                    "submitDetail" to submitDetailMap
                )
            }
            
            // 统计信息
            val statistics = mapOf(
                "totalStudents" to submissions.size,
                "averageScore" to if (submissions.isNotEmpty()) {
                    submissions.mapNotNull { it.submitScore }.average()
                } else 0.0,
                "maxScore" to (submissions.mapNotNull { it.submitScore }.maxOrNull() ?: 0),
                "minScore" to (submissions.mapNotNull { it.submitScore }.minOrNull() ?: 0)
            )
            
            val result = mapOf(
                "examInfo" to mapOf(
                    "id" to exam.id,
                    "title" to exam.title,
                    "description" to exam.description,
                    "startTime" to exam.startTime,
                    "endTime" to exam.endTime,
                    "totalScore" to exam.totalScore,
                    "questionCount" to examQuestions.size
                ),
                "questions" to examQuestions,
                "studentDetails" to studentDetails,
                "statistics" to statistics
            )
            
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "获取考试详情失败: ${e.message}"))
        }
    }
    
    /**
     * 手动阅卷接口 - 用于主观题的人工评分
     * @param submissionId 提交记录ID
     * @param gradingData 阅卷数据
     * @return 阅卷结果
     */
    @PostMapping("/{submissionId}/manual-grading")
    fun manualGrading(
        @PathVariable submissionId: Long,
        @RequestBody gradingData: ManualGradingRequest
    ): ResponseEntity<ApiResponse<Any?>> {
        try {
            // 获取提交记录
            val submission = examSubmissionService.getOriginalSubmissionsByExam(gradingData.examId)
                .find { it.id == submissionId }
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse<Any?>(HttpStatus.NOT_FOUND.value(), "提交记录不存在", null))
            
            // 解析原始答题详情
            val originalSubmitDetail = try {
                jacksonObjectMapper().readValue<MutableMap<String, Any>>(submission.submitDetail ?: "{}")
            } catch (e: Exception) {
                mutableMapOf<String, Any>()
            }
            
            var totalScore = submission.submitScore ?: 0
            
            // 处理每个题目的手动评分
            gradingData.questionGrades.forEach { (questionId, gradeInfo) ->
                val questionIdStr = questionId.toString()
                
                // 获取原始得分
                val originalQuestionDetail = originalSubmitDetail[questionIdStr] as? Map<String, Any>
                val originalScore = (originalQuestionDetail?.get("score") as? Number)?.toInt() ?: 0
                
                // 更新题目得分
                val updatedQuestionDetail = (originalQuestionDetail?.toMutableMap() ?: mutableMapOf()).apply {
                    put("score", gradeInfo.score)
                    put("feedback", gradeInfo.feedback)
                    put("manualGraded", true)
                    put("gradedBy", gradeInfo.graderId)
                    put("gradedAt", System.currentTimeMillis())
                }
                
                originalSubmitDetail[questionIdStr] = updatedQuestionDetail
                
                // 更新总分
                totalScore = totalScore - originalScore + gradeInfo.score
            }
            
            // 更新提交记录
            val updatedSubmission = submission.copy(
                submitScore = totalScore,
                submitDetail = jacksonObjectMapper().writeValueAsString(originalSubmitDetail)
            )
            
            examSubmissionService.submitExam(updatedSubmission)
            
            return ResponseEntity.ok(
                ApiResponse<Any?>(
                    HttpStatus.OK.value(),
                    "阅卷完成",
                    mapOf(
                        "submissionId" to submissionId,
                        "newTotalScore" to totalScore,
                        "gradedQuestions" to gradingData.questionGrades.keys
                    )
                )
            )
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse<Any?>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "阅卷失败: ${e.message}", null))
        }
    }
    
    /**
     * 获取需要人工阅卷的提交记录
     * @param examId 考试ID
     * @return 需要人工阅卷的记录列表
     */
    @GetMapping("/exam/{examId}/pending-review")
    fun getPendingReviewSubmissions(@PathVariable examId: Long): ResponseEntity<List<Map<String, Any>>> {
        try {
            val submissions = examSubmissionService.getOriginalSubmissionsByExam(examId)
            val exam = examService.findById(examId) ?: return ResponseEntity.notFound().build()
            
            val pendingReviews = mutableListOf<Map<String, Any>>()
            
            submissions.forEach { submission ->
                val user = userService.getUserById(submission.userId)
                val submitDetailMap = try {
                    jacksonObjectMapper().readValue<Map<String, Any>>(submission.submitDetail ?: "{}")
                } catch (e: Exception) {
                    emptyMap<String, Any>()
                }
                
                val answersMap = try {
                    jacksonObjectMapper().readValue<Map<String, Any>>(submission.answers.toString())
                } catch (e: Exception) {
                    emptyMap<String, Any>()
                }
                
                // 检查是否有需要人工复审的题目
                val needsReviewQuestions = mutableListOf<Map<String, Any>>()  
                
                exam.questions?.forEach { question ->
                    val questionId = (question as? Map<*, *>)?.get("id")?.toString() ?: return@forEach
                    val questionDetail = submitDetailMap[questionId] as? Map<String, Any>
                    val feedback = questionDetail?.get("feedback") as? String ?: ""
                    
                    // 检查是否为主观题且需要人工复审
                    if (((question as? Map<*, *>)?.get("type") == "short_answer" || (question as? Map<*, *>)?.get("type") == "essay") && 
                        (feedback.contains("建议人工复审") || 
                         (questionDetail?.get("manualGraded") as? Boolean) != true)) {
                        
                        needsReviewQuestions.add(mapOf(
                            "questionId" to ((question as? Map<*, *>)?.get("id") as? Long ?: 0L),
                            "questionContent" to ((question as? Map<*, *>)?.get("content") as? String ?: ""),
                            "questionType" to ((question as? Map<*, *>)?.get("type") as? String ?: ""),
                            "studentAnswer" to (answersMap[questionId] ?: ""),
                            "currentScore" to (questionDetail?.get("score") ?: 0),
                            "maxScore" to (questionDetail?.get("maxScore") ?: 0),
                            "feedback" to feedback,
                            "isManualGraded" to (questionDetail?.get("manualGraded") as? Boolean ?: false)
                        ))
                    }
                }
                
                if (needsReviewQuestions.isNotEmpty()) {
                    pendingReviews.add(mapOf(
                        "submissionId" to (submission.id ?: 0L),
                        "studentId" to submission.userId,
                        "studentName" to (user?.username ?: "未知学生"),
                        "submitTime" to submission.submitTime,
                        "currentTotalScore" to (submission.submitScore?.toInt() ?: 0),
                        "needsReviewQuestions" to needsReviewQuestions
                    ))
                }
            }
            
            return ResponseEntity.ok(pendingReviews)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
