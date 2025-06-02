package ccc.sypw.onlineExamSystem.util

import ccc.sypw.onlineExamSystem.model.ExamSubmission
import ccc.sypw.onlineExamSystem.model.Question
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
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
                        // 如果是 String 类型，尝试解析为不同格式
                        try {
                            // 首先尝试解析为数组格式
                            if (options.trim().startsWith("[")) {
                                val optionsList: List<String> = objectMapper.readValue(options, object : TypeReference<List<String>>() {})
                                // 将数组转换为Map格式，使用索引作为key
                                val optionsMap = optionsList.mapIndexed { index, value -> 
                                    ('A' + index).toString() to value 
                                }.toMap()
                                question.options = optionsMap
                            } else {
                                // 尝试解析为Map格式
                                val optionsMap: Map<String, String> = objectMapper.readValue(options, object : TypeReference<Map<String, String>>() {})
                                question.options = optionsMap
                            }
                        } catch (e: Exception) {
                            println("解析 options 字符串失败: ${e.message}")
                            // 如果解析失败，保持原始字符串
                            question.options = options
                        }
                    }

                }
            }
        }



    }
}