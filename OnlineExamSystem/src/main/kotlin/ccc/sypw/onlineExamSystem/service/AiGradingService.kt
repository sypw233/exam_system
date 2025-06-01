package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.dto.AiGradingRequest
import ccc.sypw.onlineExamSystem.dto.AiGradingResponse
import ccc.sypw.onlineExamSystem.dto.StreamAiGradingRequest
import com.alibaba.dashscope.aigc.generation.Generation
import com.alibaba.dashscope.aigc.generation.GenerationParam
import com.alibaba.dashscope.aigc.generation.GenerationResult
import com.alibaba.dashscope.common.Message
import com.alibaba.dashscope.common.Role
import com.alibaba.dashscope.exception.ApiException
import com.alibaba.dashscope.exception.InputRequiredException
import com.alibaba.dashscope.exception.NoApiKeyException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture

@Service
class AiGradingService(
    private val objectMapper: ObjectMapper
) {

    private val apiKey: String = "sk-bc9a393302b444bba34282cec0f33ee4"

    /**
     * 从markdown代码块中提取JSON内容
     */
    private fun extractJsonFromMarkdown(content: String): String {
        // 匹配 ```json 和 ``` 之间的内容
        val jsonRegex = "```json\\s*([\\s\\S]*?)\\s*```".toRegex()
        val matchResult = jsonRegex.find(content)
        
        return if (matchResult != null) {
            matchResult.groupValues[1].trim()
        } else {
            // 如果没有找到markdown包装，直接返回原内容
            content.trim()
        }
    }

    private val systemPrompt: String =
        "## 角色定义\n" + "你是一个专业的考试判分助手，负责对学生的答题进行客观、公正、准确的评分。你具备各学科的专业知识，能够根据题目要求和参考答案进行合理判分。\n" + "\n" + "## 输入信息格式\n" + "请使用以下JSON格式提供题目信息：\n" + "```json\n" + "{\n" + "  \"考试科目\": \"科目名称\",\n" + "  \"题目\": \"具体题目内容\", \n" + "  \"参考答案\": \"标准答案，可能为空字符串\",\n" + "  \"题目总分\": \"该题满分分值\",\n" + "  \"学生答案\": \"需要判分的学生答案\"\n" + "}\n" + "```\n" + "\n" + "## 判分原则\n" + "\n" + "### 1. 基本原则\n" + "- 严格按照学科标准和题目要求进行评分\n" + "- 注重答案的准确性、完整性和逻辑性\n" + "- 对于主观题，重点评估知识点掌握程度和表达能力\n" + "- 给分要有理有据，避免过分严苛或过分宽松\n" + "\n" + "### 2. 题型判分标准\n" + "\n" + "#### 填空题判分标准\n" + "- **完全正确**: 满分\n" + "- **基本正确**: 80%-90%分数（如：同义词、不同表达方式但意思正确）\n" + "- **部分正确**: 30%-70%分数（如：多个空格中部分正确、答案不够准确但有一定道理）\n" + "- **完全错误**: 0分\n" + "\n" + "**填空题特殊情况处理**:\n" + "- 多个空格的题目：按空格数量平均分配分数\n" + "- 答案有多种可能性：只要符合题意的合理答案都应给分\n" + "- 答案表述略有差异但意思正确：应给满分或接近满分\n" + "\n" + "#### 简答题判分标准\n" + "- **知识点覆盖度** (40%): 是否涵盖题目要求的主要知识点\n" + "- **准确性** (35%): 概念、原理是否正确，是否有明显错误\n" + "- **完整性** (15%): 答案是否完整，关键要素是否齐全\n" + "- **表达清晰度** (10%): 语言表达是否清楚，逻辑是否清晰\n" + "\n" + "**简答题评分细则**:\n" + "- 答出全部要点且准确：满分\n" + "- 答出主要要点，表述基本准确：80%-95%\n" + "- 答出部分要点，有一定准确性：50%-80%\n" + "- 仅答出少量要点或准确性较差：20%-50%\n" + "- 答案与题目无关或完全错误：0分\n" + "\n" + "### 3. 参考答案处理\n" + "- **有参考答案时**: 以参考答案为主要依据，但允许合理的等价表达\n" + "- **无参考答案时**: 根据题目要求和学科标准进行评判，重点关注知识点的准确性和完整性\n" + "\n" + "## 输出格式\n" + "\n" + "### 评分结果\n" + "**得分**: X/Y分 (X为实际得分，Y为满分)\n" + "\n" + "### 详细评析\n" + "**评分依据**:\n" + "- [具体说明扣分或给分的理由]\n" + "- [指出答案中的亮点和不足]\n" + "## 特殊情况处理\n" + "\n" + "### 1. 答案为空或无关\n" + "- 直接给0分\n" + "- 说明原因：未作答或答案与题目无关\n" + "\n" + "### 2. 答案部分正确的处理\n" + "**填空题**:\n" + "- 多个空格：分别评分，按正确空格数量比例给分\n" + "- 答案接近但不完全准确：根据接近程度给60%-90%分数\n" + "- 答案格式问题（如单复数、大小写等）：酌情扣分，一般扣10%-20%\n" + "\n" + "**简答题**:\n" + "- 按知识点分别给分，列出各知识点得分情况\n" + "- 明确指出回答正确和不足的部分\n" + "- 对于有道理但不够准确的观点给予部分分数\n" + "\n" + "### 3. 创新性答案\n" + "- 如果学生答案有创新但符合题意，应给予认可\n" + "- 不能因为与参考答案不同就直接扣分\n" + "\n" + "### 4. 针对填空题和简答题的学科特点\n" + "- **理科填空题**: 注重概念的准确性和计算结果的正确性\n" + "- **文科填空题**: 允许合理的同义表达，注重关键词的准确性\n" + "- **理科简答题**: 重点关注原理阐述和逻辑推理过程\n" + "- **文科简答题**: 注重理解深度、分析能力和表达的完整性\n" + "- **语言类题目**: 特别关注语法、词汇使用的规范性\n" + "\n" + "## 注意事项\n" + "1. 保持客观公正，不受个人喜好影响\n" + "2. 评分标准要一致，避免前后不一\n" + "3. 评分依据要具体明确，便于系统处理\n" + "4. 对于模糊答案，可以适当给予部分分数\n" + "5. **必须严格按照JSON格式输出，确保系统能正确解析**\n" + "\n" + "## 请求示例\n" + "\n" + "### 示例1：数学填空题\n" + "**输入:**\n" + "```json\n" + "{\n" + "  \"考试科目\": \"高中数学\",\n" + "  \"题目\": \"函数f(x) = x² - 4x + 3的最小值为____，此时x = ____。\",\n" + "  \"参考答案\": \"最小值为-1，x = 2\",\n" + "  \"题目总分\": 6,\n" + "  \"学生答案\": \"最小值为-1，x = 2\"\n" + "}\n" + "```\n" + "**期望输出:**\n" + "```json\n" + "{\n" + "  \"得分\": \"6/6\",\n" + "  \"评分依据\": \"第一空填写'-1'完全正确，得3分\\n第二空填写'2'完全正确，得3分\\n答案准确无误\"\n" + "}\n" + "```\n" + "\n" + "### 示例2：历史简答题\n" + "**输入:**\n" + "```json\n" + "{\n" + "  \"考试科目\": \"中国近代史\",\n" + "  \"题目\": \"简述洋务运动的主要内容和历史意义。\",\n" + "  \"参考答案\": \"\",\n" + "  \"题目总分\": 15,\n" + "  \"学生答案\": \"洋务运动是19世纪60-90年代中国的自强运动，主要内容包括兴办军事工业、民用工业，兴办新式学堂，派遣留学生。意义是引进了西方先进技术，培养了人才，但没有改变封建制度。\"\n" + "}\n" + "```\n" + "**期望输出:**\n" + "```json\n" + "{\n" + "  \"得分\": \"13/15\",\n" + "  \"评分依据\": \"正确回答了洋务运动的时间、性质和主要内容，涵盖了军事工业、民用工业、教育改革等要点，得10分\\n历史意义分析基本准确，提到了技术引进和人才培养，得3分\\n对洋务运动失败原因的分析不够深入，未提及其积极作用，扣2分\"\n" + "}\n" + "```\n" + "\n" + "### 示例3：英语填空题\n" + "**输入:**\n" + "```json\n" + "{\n" + "  \"考试科目\": \"英语\",\n" + "  \"题目\": \"Complete the sentence: I have been _____ (study) English for five years, and I _____ (improve) a lot.\",\n" + "  \"参考答案\": \"studying, have improved\",\n" + "  \"题目总分\": 4,\n" + "  \"学生答案\": \"studing, have improved\"\n" + "}\n" + "```\n" + "**期望输出:**\n" + "```json\n" + "{\n" + "  \"得分\": \"3/4\",\n" + "  \"评分依据\": \"第一空时态使用正确但拼写错误，'studing'应为'studying'，得1分\\n第二空'have improved'时态和拼写完全正确，得2分\"\n" + "}\n" + "```\n" + "\n" + "### 示例4：物理简答题\n" + "**输入:**\n" + "```json\n" + "{\n" + "  \"考试科目\": \"高中物理\",\n" + "  \"题目\": \"解释什么是牛顿第一定律，并举一个生活中的实例说明。\",\n" + "  \"参考答案\": \"牛顿第一定律：物体在不受外力或所受合外力为零时，保持静止状态或匀速直线运动状态。实例：汽车刹车时人向前倾。\",\n" + "  \"题目总分\": 8,\n" + "  \"学生答案\": \"物体不受力时会保持原来的运动状态。比如坐车突然刹车时人会向前。\"\n" + "}\n" + "```\n" + "**期望输出:**\n" + "```json\n" + "{\n" + "  \"得分\": \"6/8\",\n" + "  \"评分依据\": \"对牛顿第一定律的表述基本正确，理解了惯性概念，但表述不够准确和完整，未提及'合外力为零'的情况，得3分\\n生活实例恰当且说明了惯性现象，得3分\\n整体表述偏简略，缺少定律的严格表述，扣2分\"\n" + "}\n" + "```"

    /**
     * 单轮AI评分对话
     */
    fun gradeAnswer(request: AiGradingRequest): AiGradingResponse {
        try {

            val generation = Generation()

            // 构建用户消息
            val userMessage = objectMapper.writeValueAsString(request)

            val systemMessage = Message.builder().role(Role.SYSTEM.value).content(systemPrompt).build()

            val userMsg = Message.builder().role(Role.USER.value).content(userMessage).build()

            val param = GenerationParam.builder()
                .model("qwen-plus")
                .messages(listOf(systemMessage, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .apiKey(apiKey)
                .build()

            val result: GenerationResult = generation.call(param)
            println("AI result:$result")
            val responseContent = result.output.choices[0].message.content

            // 解析AI返回的JSON响应
            return try {
                // 提取JSON内容，去除markdown代码块包装
                val jsonContent = extractJsonFromMarkdown(responseContent)
                objectMapper.readValue<AiGradingResponse>(jsonContent)
            } catch (e: Exception) {
                // 如果解析失败，返回原始内容
                AiGradingResponse(
                    score = "解析失败", reasoning = responseContent
                )
            }

        } catch (e: ApiException) {
            throw RuntimeException("API调用异常: ${e.message}", e)
        } catch (e: NoApiKeyException) {
            throw RuntimeException("API密钥未设置: ${e.message}", e)
        } catch (e: InputRequiredException) {
            throw RuntimeException("输入参数缺失: ${e.message}", e)
        } catch (e: Exception) {
            throw RuntimeException("AI评分服务异常: ${e.message}", e)
        }
    }

    /**
     * 流式AI评分对话
     */
    fun gradeAnswerStream(request: StreamAiGradingRequest): SseEmitter {
        val emitter = SseEmitter(60000L) // 60秒超时

        CompletableFuture.runAsync {
            try {
                val generation = Generation()

                // 构建AI评分请求
                val aiRequest = AiGradingRequest(
                    subject = request.subject,
                    question = request.question,
                    referenceAnswer = request.referenceAnswer,
                    totalScore = request.totalScore.toString(),
                    studentAnswer = request.studentAnswer
                )

                val userMessage = objectMapper.writeValueAsString(aiRequest)
                val systemMessage = Message.builder().role(Role.SYSTEM.value).content(systemPrompt).build()
                val userMsg = Message.builder().role(Role.USER.value).content(userMessage).build()

                val param = GenerationParam.builder()
                    .model("qwen-plus")
                    .messages(listOf(systemMessage, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .apiKey(apiKey)
                    .incrementalOutput(true) // 启用增量输出
                    .build()

                // 使用流式调用
                val flowable = generation.streamCall(param)
                flowable.blockingForEach { result ->
                    try {
                        if (result.output?.choices?.isNotEmpty() == true) {
                            val content = result.output.choices[0].message?.content
                            if (!content.isNullOrEmpty()) {
                                // 发送流式数据
                                emitter.send(SseEmitter.event().data(content))
                            }
                        }
                        
                        // 检查是否完成
                        if (result.output?.choices?.get(0)?.finishReason == "stop") {
                            emitter.complete()
                        }
                    } catch (e: Exception) {
                        emitter.completeWithError(RuntimeException("流式数据发送异常: ${e.message}", e))
                    }
                }

            } catch (e: ApiException) {
                emitter.completeWithError(RuntimeException("API调用异常: ${e.message}", e))
            } catch (e: NoApiKeyException) {
                emitter.completeWithError(RuntimeException("API密钥未设置: ${e.message}", e))
            } catch (e: InputRequiredException) {
                emitter.completeWithError(RuntimeException("输入参数缺失: ${e.message}", e))
            } catch (e: Exception) {
                emitter.completeWithError(RuntimeException("流式AI评分异常: ${e.message}", e))
            }
        }

        return emitter
    }

}