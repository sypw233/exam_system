package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.PageResponse
import ccc.sypw.onlineExamSystem.dto.QuestionResponse
import ccc.sypw.onlineExamSystem.model.Question
import ccc.sypw.onlineExamSystem.service.QuestionService
import ccc.sypw.onlineExamSystem.util.StringProcessUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/questions")
@Tag(name = "试题管理", description = "试题相关的API接口")
class QuestionController @Autowired constructor(
    private val questionService: QuestionService
) {

    /**
     * 获取所有试题
     */
    @Operation(summary = "获取所有试题", description = "获取系统中所有试题的列表")
    @GetMapping
    fun getAllQuestions(): ResponseEntity<Any> {
        return try {
            val data = questionService.getAllQuestions()
            StringProcessUtils.updateQuestionOptions(data)
            val response = QuestionResponse(
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
     * 获取试题列表（分页）
     */
    @Operation(summary = "分页获取试题", description = "分页获取试题列表")
    @GetMapping("/{page}/{size}")
    fun getQuestions(
        @Parameter(description = "页码", required = true) @PathVariable page: Int,
        @Parameter(description = "每页大小", required = true) @PathVariable size: Int
    ): ResponseEntity<Any> {
        return try {
            val data = questionService.getQuestions(page, size)
            StringProcessUtils.updateQuestionOptions(data)
            val response = QuestionResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = PageResponse(
                    current = page,
                    total = questionService.getQuestionsDataNum(),
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
     * 根据ID获取单个试题
     */
    @Operation(summary = "根据ID获取试题", description = "根据试题ID获取单个试题的详细信息")
    @GetMapping("/{id}")
    fun getQuestionById(@Parameter(description = "试题ID", required = true) @PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val data = questionService.getQuestionById(id)
            val response = QuestionResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = PageResponse(
                    current = 1,
                    total = 1,
                    size = 10,
                    records = data
                )
            )
            ResponseEntity.status(HttpStatus.OK).body(response)
        } catch (e: Exception) {
            val response = ApiResponse(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "error",
                data = e
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

    }

    /**
     * 创建新试题
     */
    @Operation(summary = "创建试题", description = "创建一个新的试题")
    @PostMapping
    fun createQuestion(@RequestBody question: Question): ResponseEntity<Boolean> {
//        return try {
        println(question)
        val savedQuestion = questionService.createQuestion(question)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion)
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
//        }
    }

    // 更新试题
//    @PutMapping("/{id}")
//    fun updateQuestion(@PathVariable id: Long, @RequestBody question: Question): ResponseEntity<Question> {
//        return try {
//            val updatedQuestion = questionService.updateQuestion(id, question)
//            ResponseEntity.ok(updatedQuestion)
//        } catch (e: IllegalArgumentException) {
//            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
//        }
//    }

    /**
     * 删除试题
     */
    @Operation(summary = "删除试题", description = "根据试题ID删除指定试题")
    @DeleteMapping("/{id}")
    fun deleteQuestion(@Parameter(description = "试题ID", required = true) @PathVariable id: Long): ResponseEntity<Any> {
//        return try {
        println(questionService.deleteQuestionById(id))
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
