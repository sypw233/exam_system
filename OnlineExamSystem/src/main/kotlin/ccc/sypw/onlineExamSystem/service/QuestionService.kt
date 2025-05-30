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

    fun getQuestionsByIds(idList: List<Long>): List<Question> {
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
    
    // 根据分类查找问题
    fun getQuestionsByCategory(category: String): List<Question> {
        return questionRepository.findByCategory(category)
    }
    
    // 根据难度查找问题
    fun getQuestionsByDifficulty(difficulty: String): List<Question> {
        return questionRepository.findByDifficulty(difficulty)
    }
    
    // 根据分类和难度查找问题
    fun getQuestionsByCategoryAndDifficulty(category: String, difficulty: String): List<Question> {
        return questionRepository.findByCategoryAndDifficulty(category, difficulty)
    }
    
    // 随机获取指定数量的题目
    fun getRandomQuestions(count: Int): List<Question> {
        return questionRepository.findRandomQuestions(count)
    }
    
    // 根据类型随机获取指定数量的题目
    fun getRandomQuestionsByType(type: String, count: Int): List<Question> {
        return questionRepository.findRandomQuestionsByType(type, count)
    }
    
    // 根据分类随机获取指定数量的题目
    fun getRandomQuestionsByCategory(category: String, count: Int): List<Question> {
        return questionRepository.findRandomQuestionsByCategory(category, count)
    }
    
    // 根据难度随机获取指定数量的题目
    fun getRandomQuestionsByDifficulty(difficulty: String, count: Int): List<Question> {
        return questionRepository.findRandomQuestionsByDifficulty(difficulty, count)
    }
    
    // 根据类型、分类和难度随机获取指定数量的题目
    fun getRandomQuestionsByTypeAndCategoryAndDifficulty(type: String, category: String, difficulty: String, count: Int): List<Question> {
        return questionRepository.findRandomQuestionsByTypeAndCategoryAndDifficulty(type, category, difficulty, count)
    }
    
    // 获取所有分类
    fun getAllCategories(): List<String> {
        return questionRepository.findAllCategories()
    }
}