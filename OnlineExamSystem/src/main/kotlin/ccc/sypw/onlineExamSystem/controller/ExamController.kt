package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.PageResponse
import ccc.sypw.onlineExamSystem.dto.ExamDetail
import ccc.sypw.onlineExamSystem.dto.RandomExamDto
import ccc.sypw.onlineExamSystem.model.Exams
import ccc.sypw.onlineExamSystem.service.*
import ccc.sypw.onlineExamSystem.util.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/exams")
class ExamController(
    private val examService: ExamService,
    private val examSubmissionService: ExamSubmissionService,
    private val examQuestionsService: ExamQuestionService,
    private val questionsService: QuestionService,
    private val userService: UserService,
) {

    // 获取试卷列表（分页）
    @GetMapping("/{page}/{size}")
    fun getExams(@PathVariable page: Int, @PathVariable size: Int): ResponseEntity<Any> {
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
    
    // 根据科目/课程ID获取试卷列表（分页）
    @GetMapping("/course/{courseId}/{page}/{size}")
    fun getExamsByCourseId(
        @PathVariable courseId: Long,
        @PathVariable page: Int,
        @PathVariable size: Int
    ): ResponseEntity<Any> {
        return try {
            val data = examService.getExamsByCourseId(courseId, page, size)
            val response = ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = PageResponse(
                    current = page,
                    total = examService.getExamsCountByCourseId(courseId),
                    size = size,
                    records = data
                )
            )
            ResponseEntity.status(HttpStatus.OK).body(response)
        } catch (e: Exception) {
            val response = ApiResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "error",
                data = e.message
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 获取学生试卷列表（分页）
    @GetMapping("/student/{studentId}/{page}/{size}")
    fun getExamsByStudentId(
        @PathVariable studentId: Long,
        @PathVariable page: Int,
        @PathVariable size: Int
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

    @GetMapping("/{examId}/details")
    fun getExamDetails(
        @PathVariable examId: Long,
        @RequestHeader("Authorization") authorizationHeader: String
    )
            : ResponseEntity<Any> {
        // 获取考试基本信息
        try {
            val token = authorizationHeader.replace("Bearer ", "")
            println("token: $token")
            // 从 token 中提取用户 ID (假设 token 是一个 JWT)
            val userName = JwtUtils.getUsernameFromToken(token)
            println("userName$userName")
            val user = userName?.let { userService.getUserByUserName(it) }
            val userId = user?.id
            // 检查该用户是否有考试提交记录
            val hasSubmission = userId?.let { examSubmissionService.isHaveUserSubmission(it, examId) }
            println("examid $examId userid$userId")
            println(hasSubmission)
            if (hasSubmission == true) {
                // 如果用户已提交过考试记录，返回相应的错误信息
                return ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
            val exam = examService.findById(examId)
            if (exam != null) {
                val currentTime = LocalDateTime.now()
                if (currentTime.isBefore(exam.startTime) || currentTime.isAfter(exam.endTime)) {
                    // 当前时间不在考试时间范围内，返回错误响应
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                }
            }
            // 获取关联的题目列表
            val questionIds = examQuestionsService.findByExamId(examId)
            val examDetail = exam?.let {
                ExamDetail(
                    id = exam.id,
                    title = it.title,
                    description = exam.description,
                    startTime = exam.startTime,
                    endTime = exam.endTime,
                    creatorId = exam.creatorId,
                    createTime = exam.createTime,
                    totalScore = exam.totalScore,
                    questions = questionIds?.let { it1 -> questionsService.getQuestionsByIds(it1) }
                )
            }
            return ResponseEntity.status(HttpStatus.OK).body(examDetail)
        } catch (e: Exception) {
            throw e
        }

    }

    // 根据试卷名称进行分页搜索
    @GetMapping("/search")
    fun searchExams(@RequestParam key: String, @RequestParam page: Int, @RequestParam size: Int): ResponseEntity<Any> {
        return try {
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
            val response = ApiResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "error",
                data = e.toString()
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 创建考试
    @PostMapping
    fun createExam(@RequestBody exam: Exams): ResponseEntity<Any> {
        println(exam)
        val createdExam = examService.createExam(exam)
//        val questionsIdList = exam.questions
        return ResponseEntity.ok(createdExam)
    }
    
    // 随机组卷
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
                    message = "随机组卷失败",
                    data = null
                )
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
            }
        } catch (e: Exception) {
            val response = ApiResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "随机组卷失败: ${e.message}",
                data = null
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    // 删除考试
    @DeleteMapping("/{id}")
    fun deleteExam(@PathVariable id: Long): ResponseEntity<Any> {
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
//            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                ApiResponse(
//                    code = HttpStatus.NOT_FOUND.value(),
//                    message = "failed",
//                    data = null
//                )
//            )
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                ApiResponse(
//                    code = HttpStatus.BAD_REQUEST.value(),
//                    message = "failed",
//                    data = null
//                )
//            )
//        }
    }
}
