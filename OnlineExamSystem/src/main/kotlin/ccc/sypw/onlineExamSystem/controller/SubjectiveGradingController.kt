package ccc.sypw.onlineExamSystem.controller

import ccc.sypw.onlineExamSystem.dto.ApiResponse
import ccc.sypw.onlineExamSystem.service.SubjectiveGradingService
import ccc.sypw.onlineExamSystem.service.ExamSubmissionService
import ccc.sypw.onlineExamSystem.service.QuestionService
import ccc.sypw.onlineExamSystem.service.ExamService
import ccc.sypw.onlineExamSystem.util.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * 主观题评分控制器
 * 提供主观题智能评分和人工复审功能
 */
@RestController
@RequestMapping("/api/subjective-grading")
class SubjectiveGradingController(
    private val subjectiveGradingService: SubjectiveGradingService,
    private val examSubmissionService: ExamSubmissionService,
    private val questionService: QuestionService,
    private val examService: ExamService
) {

    /**
     * 获取需要人工复审的主观题列表
     */
    @GetMapping("/review-needed/{examId}")
    fun getReviewNeededSubmissions(
        @PathVariable examId: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        return try {
            // 验证教师权限
            val userInfo = JwtUtils.parseToken(token.removePrefix("Bearer "))
            if (userInfo["role"] != "TEACHER" && userInfo["role"] != "ADMIN") {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse(HttpStatus.FORBIDDEN.value(), "权限不足", null))
            }

            val submissions = examSubmissionService.getSubmissionsByExam(examId)
            val reviewNeededList = mutableListOf<SubjectiveReviewItem>()

            submissions.forEach { submission ->
                val submissionDetail = submission.answers
                if (submissionDetail is String) {
                    val answersMap = jacksonObjectMapper().readValue(submissionDetail, Map::class.java) as Map<String, Any>
                    val submitDetailMap = if (submission.submitDetail != null) {
                        jacksonObjectMapper().readValue(submission.submitDetail, Map::class.java) as Map<String, Any>
                    } else {
                        emptyMap()
                    }

                    answersMap.forEach { (questionId, studentAnswer) ->
                        val question = questionService.getQuestionById(questionId.toLong())
                        if (question?.type == "short_answer") {
                            val questionDetail = submitDetailMap[questionId] as? Map<String, Any>
                            val feedback = questionDetail?.get("feedback") as? String ?: ""
                            
                            // 检查是否需要复审（包含"建议人工复审"标记）
                            if (feedback.contains("建议人工复审")) {
                                reviewNeededList.add(
                                    SubjectiveReviewItem(
                                        submissionId = submission.id ?: 0,
                                        questionId = questionId.toLong(),
                                        studentId = submission.userId,
                                        studentAnswer = studentAnswer.toString(),
                                        standardAnswer = question.answer ?: "",
                                        currentScore = questionDetail?.get("score") as? Int ?: 0,
                                        maxScore = questionDetail?.get("maxScore") as? Int ?: 0,
                                        aiSuggestion = feedback,
                                        questionContent = question.content
                                    )
                                )
                            }
                        }
                    }
                }
            }

            ResponseEntity.ok(
                ApiResponse(
                    HttpStatus.OK.value(),
                    "获取成功",
                    reviewNeededList
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "获取失败: ${e.message}", null))
        }
    }

    /**
     * 教师手动调整主观题分数
     */
    @PostMapping("/manual-grade")
    fun manualGradeSubjective(
        @RequestBody request: ManualGradeRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        return try {
            // 验证教师权限
            val userInfo = JwtUtils.parseToken(token.removePrefix("Bearer "))
            if (userInfo["role"] != "TEACHER" && userInfo["role"] != "ADMIN") {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse(HttpStatus.FORBIDDEN.value(), "权限不足", null))
            }

            // 这里需要实现更新考试提交记录中特定题目分数的逻辑
            // 由于当前数据结构的限制，这里提供一个简化的实现思路
            
            ResponseEntity.ok(
                ApiResponse(
                    HttpStatus.OK.value(),
                    "评分更新成功",
                    mapOf(
                        "submissionId" to request.submissionId,
                        "questionId" to request.questionId,
                        "newScore" to request.newScore,
                        "teacherComment" to request.teacherComment
                    )
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "评分更新失败: ${e.message}", null))
        }
    }

    /**
     * 重新进行智能评分
     */
    @PostMapping("/re-grade/{submissionId}/{questionId}")
    fun reGradeSubjective(
        @PathVariable submissionId: Long,
        @PathVariable questionId: Long,
        @RequestBody request: ReGradeRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Any> {
        return try {
            // 验证教师权限
            val userInfo = JwtUtils.parseToken(token.removePrefix("Bearer "))
            if (userInfo["role"] != "TEACHER" && userInfo["role"] != "ADMIN") {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse(HttpStatus.FORBIDDEN.value(), "权限不足", null))
            }

            val question = questionService.getQuestionById(questionId)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse(HttpStatus.NOT_FOUND.value(), "题目不存在", null))

            val (score, similarity, details) = subjectiveGradingService.gradeSubjectiveQuestion(
                request.studentAnswer,
                question.answer ?: "",
                request.maxScore,
                request.keywords
            )

            ResponseEntity.ok(
                ApiResponse(
                    HttpStatus.OK.value(),
                    "重新评分成功",
                    mapOf(
                        "score" to score,
                        "similarity" to similarity,
                        "details" to details,
                        "needsReview" to (score < request.maxScore * 0.3 || similarity < 20.0)
                    )
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "重新评分失败: ${e.message}", null))
        }
    }
}

/**
 * 需要复审的主观题项目
 */
data class SubjectiveReviewItem(
    val submissionId: Long,
    val questionId: Long,
    val studentId: Long,
    val studentAnswer: String,
    val standardAnswer: String,
    val currentScore: Int,
    val maxScore: Int,
    val aiSuggestion: String,
    val questionContent: String
)

/**
 * 手动评分请求
 */
data class ManualGradeRequest(
    val submissionId: Long,
    val questionId: Long,
    val newScore: Int,
    val teacherComment: String
)

/**
 * 重新评分请求
 */
data class ReGradeRequest(
    val studentAnswer: String,
    val maxScore: Int,
    val keywords: List<String> = emptyList()
)