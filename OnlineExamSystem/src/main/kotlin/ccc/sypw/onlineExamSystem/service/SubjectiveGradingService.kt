package ccc.sypw.onlineExamSystem.service

import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.math.min

/**
 * 主观题智能判分服务
 * 提供基于关键词匹配、相似度计算等方式的主观题自动评分功能
 */
@Service
class SubjectiveGradingService {

    /**
     * 主观题智能判分
     * @param studentAnswer 学生答案
     * @param standardAnswer 标准答案
     * @param maxScore 满分
     * @param keywords 关键词列表（可选）
     * @return Triple<得分, 相似度百分比, 评分详情>
     */
    fun gradeSubjectiveQuestion(
        studentAnswer: String,
        standardAnswer: String,
        maxScore: Int,
        keywords: List<String> = emptyList()
    ): Triple<Int, Double, String> {
        
        // 预处理答案文本
        val cleanStudentAnswer = cleanText(studentAnswer)
        val cleanStandardAnswer = cleanText(standardAnswer)
        
        if (cleanStudentAnswer.isEmpty()) {
            return Triple(0, 0.0, "答案为空")
        }
        
        // 计算文本相似度
        val similarity = calculateTextSimilarity(cleanStudentAnswer, cleanStandardAnswer)
        
        // 关键词匹配评分
        val keywordScore = if (keywords.isNotEmpty()) {
            calculateKeywordScore(cleanStudentAnswer, keywords)
        } else {
            // 如果没有提供关键词，从标准答案中提取关键词
            val extractedKeywords = extractKeywords(cleanStandardAnswer)
            calculateKeywordScore(cleanStudentAnswer, extractedKeywords)
        }
        
        // 综合评分：相似度占60%，关键词匹配占40%
        val finalScore = (similarity * 0.6 + keywordScore * 0.4) * maxScore
        val roundedScore = finalScore.toInt()
        
        // 生成评分详情
        val details = generateGradingDetails(similarity, keywordScore, keywords.ifEmpty { extractKeywords(cleanStandardAnswer) }, cleanStudentAnswer)
        
        return Triple(roundedScore, similarity * 100, details)
    }
    
    /**
     * 文本预处理：去除多余空格、标点符号，转换为小写
     */
    private fun cleanText(text: String): String {
        return text.trim()
            .replace(Regex("[\\p{Punct}\\s]+"), " ")
            .lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()
    }
    
    /**
     * 计算两个文本的相似度（基于编辑距离）
     */
    private fun calculateTextSimilarity(text1: String, text2: String): Double {
        if (text1.isEmpty() && text2.isEmpty()) return 1.0
        if (text1.isEmpty() || text2.isEmpty()) return 0.0
        
        val distance = levenshteinDistance(text1, text2)
        val maxLength = max(text1.length, text2.length)
        
        return 1.0 - (distance.toDouble() / maxLength)
    }
    
    /**
     * 计算编辑距离（Levenshtein Distance）
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }
        
        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j
        
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        
        return dp[len1][len2]
    }
    
    /**
     * 计算关键词匹配得分
     */
    private fun calculateKeywordScore(studentAnswer: String, keywords: List<String>): Double {
        if (keywords.isEmpty()) return 0.0
        
        var matchedCount = 0
        val studentWords = studentAnswer.split(" ")
        
        for (keyword in keywords) {
            val cleanKeyword = cleanText(keyword)
            if (studentWords.any { word -> 
                word.contains(cleanKeyword) || cleanKeyword.contains(word) ||
                calculateTextSimilarity(word, cleanKeyword) > 0.8
            }) {
                matchedCount++
            }
        }
        
        return matchedCount.toDouble() / keywords.size
    }
    
    /**
     * 从标准答案中提取关键词
     */
    private fun extractKeywords(standardAnswer: String): List<String> {
        // 简单的关键词提取：去除停用词，保留长度大于2的词
        val stopWords = setOf("的", "了", "在", "是", "有", "和", "就", "不", "人", "都", "一", "个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这", "那", "里", "为", "能", "下", "过", "他", "她", "它")
        
        return standardAnswer.split(" ")
            .filter { word -> word.length > 2 && !stopWords.contains(word) }
            .distinct()
    }
    
    /**
     * 生成评分详情
     */
    private fun generateGradingDetails(
        similarity: Double, 
        keywordScore: Double, 
        keywords: List<String>,
        studentAnswer: String
    ): String {
        val similarityPercent = (similarity * 100).toInt()
        val keywordPercent = (keywordScore * 100).toInt()
        
        val matchedKeywords = keywords.filter { keyword ->
            val cleanKeyword = cleanText(keyword)
            studentAnswer.split(" ").any { word ->
                word.contains(cleanKeyword) || cleanKeyword.contains(word) ||
                calculateTextSimilarity(word, cleanKeyword) > 0.8
            }
        }
        
        return buildString {
            append("智能评分详情:\n")
            append("文本相似度: ${similarityPercent}%\n")
            append("关键词匹配: ${keywordPercent}% (${matchedKeywords.size}/${keywords.size})\n")
            if (matchedKeywords.isNotEmpty()) {
                append("匹配的关键词: ${matchedKeywords.joinToString(", ")}\n")
            }
            append("综合得分基于: 相似度60% + 关键词40%")
        }
    }
    
    /**
     * 批量处理主观题评分
     */
    fun batchGradeSubjectiveQuestions(
        submissions: List<SubjectiveSubmission>
    ): List<SubjectiveGradingResult> {
        return submissions.map { submission ->
            val (score, similarity, details) = gradeSubjectiveQuestion(
                submission.studentAnswer,
                submission.standardAnswer,
                submission.maxScore,
                submission.keywords
            )
            
            SubjectiveGradingResult(
                questionId = submission.questionId,
                studentId = submission.studentId,
                score = score,
                maxScore = submission.maxScore,
                similarity = similarity,
                details = details,
                needsManualReview = score < submission.maxScore * 0.3 // 得分低于30%需要人工复审
            )
        }
    }
}

/**
 * 主观题提交数据
 */
data class SubjectiveSubmission(
    val questionId: Long,
    val studentId: Long,
    val studentAnswer: String,
    val standardAnswer: String,
    val maxScore: Int,
    val keywords: List<String> = emptyList()
)

/**
 * 主观题评分结果
 */
data class SubjectiveGradingResult(
    val questionId: Long,
    val studentId: Long,
    val score: Int,
    val maxScore: Int,
    val similarity: Double,
    val details: String,
    val needsManualReview: Boolean
)