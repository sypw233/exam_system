package ccc.sypw.onlineExamSystem.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * AI评分请求DTO
 */
data class AiGradingRequest(
    @JsonProperty("考试科目")
    val subject: String,
    
    @JsonProperty("题目")
    val question: String,
    
    @JsonProperty("参考答案")
    val referenceAnswer: String,
    
    @JsonProperty("题目总分")
    val totalScore: String,
    
    @JsonProperty("学生答案")
    val studentAnswer: String
)

/**
 * AI评分响应DTO
 */
data class AiGradingResponse(
    @JsonProperty("得分")
    val score: String,
    
    @JsonProperty("评分依据")
    val reasoning: String
)

/**
 * 流式AI评分请求DTO
 */
data class StreamAiGradingRequest(
    val subject: String,
    val question: String,
    val referenceAnswer: String,
    val totalScore: Int,
    val studentAnswer: String
)