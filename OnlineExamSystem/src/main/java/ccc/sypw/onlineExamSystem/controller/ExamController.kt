package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.PageResponse
import ccc.sypw.onlineExamSystem.dto.ExamDetail
import ccc.sypw.onlineExamSystem.dto.RandomExamDto
import ccc.sypw.onlineExamSystem.model.Exams
import ccc.sypw.onlineExamSystem.service.*
import ccc.sypw.onlineExamSystem.util.JwtUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/exams")
@Tag(name = "考试管理", description = "考试相关的API接口")
class ExamController(
    private val examService: ExamService,
    private val examSubmissionService: ExamSubmissionService,
    private val examQuestionsService: ExamQuestionService,
    private val questionsService: QuestionService,
    private val userService: UserService,
) {

    /**
     * 获取试卷列表（分页）
     */
    @Operation(summary = "分页获取考试列表", description = "分页获取考试列表")
    @GetMapping("/{page}/{size}")
    fun getExams(@Parameter(description = "页码", required = true) @PathVariable page: Int, @Parameter(description = "每页大小", required = true) @PathVariable size: Int): ResponseEntity<Any> {
        return try {
            val data = examService.getExams(page, size)
            val response = ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = PageResponse(
                    current = page,
                    total = examService.getExamsDataNum(),
                    size = size,
                    records = data
                )
            )
            ResponseEntity.status(HttpStatus.OK).body(response)
        } catch (e: Exception) {
            val response = ApiResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "error",
                data = null
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

    }

    /**
     * 获取学生试卷列表（分页）
     */
    @Operation(summary = "获取学生考试列表", description = "根据学生ID分页获取该学生的考试列表")
    @GetMapping("/student/{studentId}/{page}/{size}")
    fun getExamsByStudentId(
        @Parameter(description = "学生ID", required = true) @PathVariable studentId: Long,
        @Parameter(description = "页码", required = true) @PathVariable page: Int,
        @Parameter(description = "每页大小", required = true) @PathVariable size: Int
    ): ResponseEntity<Any> {
//        return try {
        val data = examService.getExamsByStudentId(studentId, page, size)
        val response = ApiResponse(
            code = HttpStatus.OK.value(),
            message = "success",
            data = PageResponse(
                current = page,
                total = examService.getExamsDataNumByStudentId(studentId),
                size = size,
                records = data
            )
        )
        return ResponseEntity.status(HttpStatus.OK).body(response)
//        } catch (e: Exception) {
//            val response = ApiResponse(
//                code = HttpStatus.BAD_REQUEST.value(),
//                message = "error",
//                data = null
//            )
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
//        }

    }

    /**
     * 获取所有考试
     */
    @Operation(summary = "获取所有考试", description = "获取系统中所有考试的列表")
    @GetMapping("/all")
    fun getAllExams(): ResponseEntity<Any> {
        return try {
            val data = examService.getAllExams()
            val response = ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = data
            )
            ResponseEntity.status(HttpStatus.OK).body(response)
        } catch (e: Exception) {
            val response = ApiResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "error",
                data = null
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

    }

    /**
     * 获取考试详情
     */
    @Operation(summary = "获取考试详情", description = "根据考试ID获取考试的详细信息，包括题目列表")
    @GetMapping("/{examId}/details")
    fun getExamDetails(
        @Parameter(description = "考试ID", required = true) @PathVariable examId: Long,
        @Parameter(description = "授权令牌", required = true) @RequestHeader("Authorization") authorizationHeader: String
    )
            : ResponseEntity<Any> {
        return try {
            val token = authorizationHeader.replace("Bearer ", "")
            println("token: $token")
            // 从 token 中提取用户 ID (假设 token 是一个 JWT)
            val userName = JwtUtils.getUsernameFromToken(token)
            println("userName$userName")
            val user = userName?.let { userService.getUserByUserName(it) }
            val userId = user?.id
            
            // 获取考试基本信息
            val exam = examService.findById(examId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse(
                        code = HttpStatus.NOT_FOUND.value(),
                        message = "考试不存在",
                        data = null
                    )
                )
            
            // 只对学生进行时间和提交状态检查
            if (user?.role == "STUDENT") {
                // 检查该用户是否有考试提交记录
                val hasSubmission = userId?.let { examSubmissionService.isHaveUserSubmission(it, examId) }
                println("examid $examId userid$userId")
                println(hasSubmission)
                if (hasSubmission == true) {
                    // 如果用户已提交过考试记录，返回相应的错误信息
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        ApiResponse(
                            code = HttpStatus.CONFLICT.value(),
                            message = "您已经提交过该考试",
                            data = null
                        )
                    )
                }
                
                // 检查考试时间（只对学生限制）
                val currentTime = LocalDateTime.now()
                if (currentTime.isBefore(exam.startTime) || currentTime.isAfter(exam.endTime)) {
                    // 当前时间不在考试时间范围内，返回错误响应
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        ApiResponse(
                            code = HttpStatus.FORBIDDEN.value(),
                            message = "考试时间未开始或已结束",
                            data = null
                        )
                    )
                }
            }
            
            // 获取关联的题目列表
            val questionIds = examQuestionsService.findByExamId(examId)
            val questions = if (!questionIds.isNullOrEmpty()) {
                questionsService.getQuestionsByIds(questionIds)
            } else {
                emptyList()
            }
            
            val examDetail = ExamDetail(
                id = exam.id,
                title = exam.title,
                description = exam.description,
                startTime = exam.startTime,
                endTime = exam.endTime,
                creatorId = exam.creatorId,
                createTime = exam.createTime,
                totalScore = exam.totalScore,
                questions = questions
            )
            
            ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse(
                    code = HttpStatus.OK.value(),
                    message = "获取考试详情成功",
                    data = examDetail
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse(
                    code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    message = "服务器内部错误: ${e.message}",
                    data = null
                )
            )
        }
    }

    /**
     * 根据试卷名称进行分页搜索
     */
    @Operation(summary = "搜索考试", description = "根据关键字搜索考试，支持分页")
    @GetMapping("/search")
    fun searchExams(
        @Parameter(description = "搜索关键字", required = true) @RequestParam key: String, 
        @Parameter(description = "页码", required = true) @RequestParam page: Int, 
        @Parameter(description = "每页大小", required = true) @RequestParam size: Int
    ): ResponseEntity<Any> {
        return try {
            // 参数验证
            if (key.isBlank()) {
                val response = ApiResponse(
                    code = HttpStatus.BAD_REQUEST.value(),
                    message = "搜索关键字不能为空",
                    data = null
                )
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
            }
            
            if (page < 1 || size < 1) {
                val response = ApiResponse(
                    code = HttpStatus.BAD_REQUEST.value(),
                    message = "页码和每页大小必须大于0",
                    data = null
                )
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
            }
            
            val data = examService.searchExams(key, page, size)
            val response = ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = PageResponse(
                    current = page,
                    total = examService.searchExamsNum(key),
                    size = size,
                    records = data
                )
            )
            ResponseEntity.status(HttpStatus.OK).body(response)
        } catch (e: Exception) {
            e.printStackTrace()
            val response = ApiResponse(
                code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = "服务器内部错误: ${e.message}",
                data = null
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    /**
     * 创建考试
     */
    @Operation(summary = "创建考试", description = "创建一个新的考试")
    @PostMapping
    fun createExam(@RequestBody exam: Exams): ResponseEntity<Any> {
        println(exam)
        val createdExam = examService.createExam(exam)
//        val questionsIdList = exam.questions
        return ResponseEntity.ok(createdExam)
    }

    /**
     * 随机组卷
     */
    @Operation(summary = "随机组卷", description = "根据题型分布自动生成考试试卷")
    @PostMapping("/random")
    fun createRandomExam(@RequestBody randomExamDto: RandomExamDto): ResponseEntity<Any> {
        return try {
            val createdExam = examService.createRandomExam(randomExamDto)
            if (createdExam != null) {
                val response = ApiResponse(
                    code = HttpStatus.OK.value(),
                    message = "随机组卷成功",
                    data = createdExam
                )
                ResponseEntity.status(HttpStatus.OK).body(response)
            } else {
                val response = ApiResponse(
                    code = HttpStatus.BAD_REQUEST.value(),
                    message = "随机组卷失败，可能是题目数量不足",
                    data = null
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
            }
        } catch (e: Exception) {
            val response = ApiResponse(
                code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message = "随机组卷失败：${e.message}",
                data = null
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }


    /**
     * 删除考试
     */
    @Operation(summary = "删除考试", description = "根据考试ID删除指定考试")
    @DeleteMapping("/{id}")
    fun deleteExam(@Parameter(description = "考试ID", required = true) @PathVariable id: Long): ResponseEntity<Any> {
//        return try {
        println(examService.deleteExams(id))
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = null
            )
        )
//        } catch (e: IllegalArgumentException) {
//            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse(
//                code = HttpStatus.NOT_FOUND.value(),
//                message = "failed",
//                data = null
//            ))
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse(
//                code = HttpStatus.BAD_REQUEST.value(),
//                message = "failed",
//                data = null
//            ))
//        }
    }
}
