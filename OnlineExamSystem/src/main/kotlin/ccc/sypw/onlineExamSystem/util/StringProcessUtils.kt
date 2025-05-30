package ccc.sypw.onlineExamSystem.util

import ccc.sypw.onlineExamSystem.model.ExamSubmission
import ccc.sypw.onlineExamSystem.model.Question
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.jvm.Throws

class StringProcessUtils {
    companion object {

        fun updateQuestionOptions(questions: List<Question>) {
            val objectMapper = ObjectMapper()

            // 遍历问题列表
            for (question in questions) {
                // 判断 options 类型
                when (val options = question.options) {
                    is String -> {
                        // 如果是 String 类型，将其转换为 Map
                        try {
                            val optionsMap: Map<String, String> =
                                objectMapper.readValue(options, Map::class.java) as Map<String, String>
                            question.options = optionsMap // 更新 options 为 Map
                        } catch (e: Exception) {
                            println("解析 options 字符串失败: ${e.message}")
                        }
                    }

                }
            }
        }



    }
}