package ccc.sypw.onlineExamSystem.dto

data class RandomExamDto(
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String,
    val creatorId: Long,
    val totalScore: Int,
    val courseId: Long,
    // 按题型、分类和难度分配题目数量和分数
    val questionDistribution: List<QuestionDistribution>
)

data class QuestionDistribution(
    val type: String,           // 题型：单选、多选、判断等
    val category: String?,      // 分类（可选）
    val difficulty: String?,    // 难度（可选）
    val count: Int,             // 题目数量
    val scorePerQuestion: Int   // 每题分数
) 