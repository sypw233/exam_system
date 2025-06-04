package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.model.Question
import ccc.sypw.onlineExamSystem.repository.QuestionRepository
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
) {

    fun getQuestions(page: Int, size: Int): List<Question> {
        val offset = (page - 1) * size
        return questionRepository.findQuestionsWithPagination(size, offset)
    }

    /**
     * 根据题目ID列表获取题目信息
     * @param idList 题目ID列表
     * @return 题目列表
     */
    fun getQuestionsByIds(idList: List<Long>): List<Question> {
        if (idList.isEmpty()) {
            return emptyList()
        }
        return questionRepository.getByIdList(idList)
    }

    fun getAllQuestions(): List<Question> {
        return questionRepository.findAllQuestion()
    }

    //    获取数据总条目
    fun getQuestionsDataNum(): Int {
        return questionRepository.findQuestionsDataNum()
    }

    // 根据 ID 查找问题
    fun getQuestionById(id: Long): Question? {
        return questionRepository.findById(id)
    }

    fun deleteQuestionById(id: Long): Any? {
        return questionRepository.deleteById(id)
    }

    // 创建试题
    fun createQuestion(question: Question): Boolean {
        return questionRepository.saveQuestion(
            question.content,
            question.type,
            question.options.toString(),
            question.answer,
            question.difficulty,
            question.category,
            question.creatorId
        )
    }

    fun getQuestionAnswer(id:Long):String?{
        return questionRepository.findAnswerById(id)
    }

    // 根据题目类型查找问题
    fun getQuestionsByType(type: String): List<Question> {
        return questionRepository.findByType(type)
    }


}