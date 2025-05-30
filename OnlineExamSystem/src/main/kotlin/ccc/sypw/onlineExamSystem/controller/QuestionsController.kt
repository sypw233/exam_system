package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.PageResponse
import ccc.sypw.onlineExamSystem.dto.QuestionResponse
import ccc.sypw.onlineExamSystem.model.Question
import ccc.sypw.onlineExamSystem.service.QuestionService
import ccc.sypw.onlineExamSystem.util.StringProcessUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/questions")
class QuestionController @Autowired constructor(
    private val questionService: QuestionService
) {


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

    // 获取所有分类
    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<Any> {
        return try {
            val categories = questionService.getAllCategories()
            val response = ApiResponse(
                code = HttpStatus.OK.value(),
                message = "success",
                data = categories
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
    
    // 根据分类获取题目
    @GetMapping("/category/{category}")
    fun getQuestionsByCategory(@PathVariable category: String): ResponseEntity<Any> {
        return try {
            val data = questionService.getQuestionsByCategory(category)
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
    
    // 根据难度获取题目
    @GetMapping("/difficulty/{difficulty}")
    fun getQuestionsByDifficulty(@PathVariable difficulty: String): ResponseEntity<Any> {
        return try {
            val data = questionService.getQuestionsByDifficulty(difficulty)
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
    
    // 根据分类和难度获取题目
    @GetMapping("/category/{category}/difficulty/{difficulty}")
    fun getQuestionsByCategoryAndDifficulty(
        @PathVariable category: String,
        @PathVariable difficulty: String
    ): ResponseEntity<Any> {
        return try {
            val data = questionService.getQuestionsByCategoryAndDifficulty(category, difficulty)
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
    
    // 随机获取指定数量的题目
    @GetMapping("/random/{count}")
    fun getRandomQuestions(@PathVariable count: Int): ResponseEntity<Any> {
        return try {
            val data = questionService.getRandomQuestions(count)
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
    
    // 根据类型随机获取指定数量的题目
    @GetMapping("/random/type/{type}/{count}")
    fun getRandomQuestionsByType(
        @PathVariable type: String,
        @PathVariable count: Int
    ): ResponseEntity<Any> {
        return try {
            val data = questionService.getRandomQuestionsByType(type, count)
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
    
    // 根据分类随机获取指定数量的题目
    @GetMapping("/random/category/{category}/{count}")
    fun getRandomQuestionsByCategory(
        @PathVariable category: String,
        @PathVariable count: Int
    ): ResponseEntity<Any> {
        return try {
            val data = questionService.getRandomQuestionsByCategory(category, count)
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

    // 获取试题列表（分页）
    @GetMapping("/{page}/{size}")
    fun getQuestions(
        @PathVariable page: Int,
        @PathVariable size: Int
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

    // 获取单个试题
    @GetMapping("/{id}")
    fun getQuestionById(@PathVariable id: Long): ResponseEntity<Any> {
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

    // 创建试题
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

    // 删除试题
    @DeleteMapping("/{id}")
    fun deleteQuestion(@PathVariable id: Long): ResponseEntity<Any> {
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
