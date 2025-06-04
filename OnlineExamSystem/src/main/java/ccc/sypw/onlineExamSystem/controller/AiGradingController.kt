package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.AiGradingRequest
import ccc.sypw.onlineExamSystem.dto.AiGradingResponse
import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.dto.StreamAiGradingRequest
import ccc.sypw.onlineExamSystem.service.AiGradingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/ai-grading")
@Tag(name = "AI辅助阅卷", description = "AI辅助阅卷相关接口")
class AiGradingController(
    private val aiGradingService: AiGradingService
) {
    
    /**
     * AI评分接口 - 单轮对话
     */
    @PostMapping("/grade")
    @Operation(summary = "AI评分", description = "使用AI对学生答案进行评分")
    fun gradeAnswer(
        @RequestBody @Parameter(description = "AI评分请求") request: AiGradingRequest
    ): ResponseEntity<ApiResponse<AiGradingResponse>> {
        return try {
            val result = aiGradingService.gradeAnswer(request)
            ResponseEntity.ok(ApiResponse.success(result))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error<AiGradingResponse>("AI评分失败: ${e.message}")
            )
        }
    }

    /**
     * AI评分接口 - 流式输出
     */
    @PostMapping("/grade/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @Operation(summary = "AI流式评分", description = "使用AI对学生答案进行流式评分")
    fun gradeAnswerStream(
        @RequestBody @Parameter(description = "流式AI评分请求") request: StreamAiGradingRequest
    ): SseEmitter {
        return aiGradingService.gradeAnswerStream(request)
    }

    /**
     * 测试AI连接
     */
    @GetMapping("/test")
    @Operation(summary = "测试AI连接", description = "测试与阿里云DashScope的连接")
    fun testConnection(): ResponseEntity<ApiResponse<String>> {
        return try {
            val testRequest = AiGradingRequest(
                subject = "测试",
                question = "1+1等于多少？",
                referenceAnswer = "2",
                totalScore = "10",
                studentAnswer = "2"
            )
            
            val result = aiGradingService.gradeAnswer(testRequest)
            ResponseEntity.ok(ApiResponse.success("AI连接测试成功，返回结果: ${result.score}"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error<String>("AI连接测试失败: ${e.message}")
            )
        }
    }
}