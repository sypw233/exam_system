package ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.Question
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional

interface QuestionRepository : CrudRepository<Question, String> {

    //    分页获取
    @Query("SELECT * FROM questions LIMIT :limit OFFSET :offset")
    fun findQuestionsWithPagination(limit: Int, offset: Int): List<Question>

    @Query("SELECT * FROM questions")
    fun findAllQuestion(): List<Question>

    //    获取总条目数
    @Query("SELECT COUNT(*) FROM questions")
    fun findQuestionsDataNum(): Int

    // 根据 id 查找问题
    @Query("SELECT * FROM questions WHERE id = :id")
    fun findById(id: Long): Question?

    // 根据类型查找问题
    @Query("SELECT * FROM questions WHERE type = :type")
    fun findByType(type: String): List<Question>

    // 根据分类查找问题
    @Query("SELECT * FROM questions WHERE category = :category")
    fun findByCategory(category: String): List<Question>

    // 根据难度查找问题
    @Query("SELECT * FROM questions WHERE difficulty = :difficulty")
    fun findByDifficulty(difficulty: String): List<Question>

    // 根据分类和难度查找问题
    @Query("SELECT * FROM questions WHERE category = :category AND difficulty = :difficulty")
    fun findByCategoryAndDifficulty(category: String, difficulty: String): List<Question>

    // 随机获取指定数量的题目
    @Query("SELECT * FROM questions ORDER BY RAND() LIMIT :count")
    fun findRandomQuestions(count: Int): List<Question>

    // 根据类型随机获取指定数量的题目
    @Query("SELECT * FROM questions WHERE type = :type ORDER BY RAND() LIMIT :count")
    fun findRandomQuestionsByType(type: String, count: Int): List<Question>

    // 根据分类随机获取指定数量的题目
    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RAND() LIMIT :count")
    fun findRandomQuestionsByCategory(category: String, count: Int): List<Question>

    // 根据难度随机获取指定数量的题目
    @Query("SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY RAND() LIMIT :count")
    fun findRandomQuestionsByDifficulty(difficulty: String, count: Int): List<Question>

    // 根据类型和分类随机获取指定数量的题目
    @Query("SELECT * FROM questions WHERE type = :type AND category = :category ORDER BY RAND() LIMIT :count")
    fun findRandomQuestionsByTypeAndCategory(type: String, category: String, count: Int): List<Question>

    // 根据类型和难度随机获取指定数量的题目
    @Query("SELECT * FROM questions WHERE type = :type AND difficulty = :difficulty ORDER BY RAND() LIMIT :count")
    fun findRandomQuestionsByTypeAndDifficulty(type: String, difficulty: String, count: Int): List<Question>

    // 根据类型、分类和难度随机获取指定数量的题目
    @Query("SELECT * FROM questions WHERE type = :type AND category = :category AND difficulty = :difficulty ORDER BY RAND() LIMIT :count")
    fun findRandomQuestionsByTypeAndCategoryAndDifficulty(type: String, category: String, difficulty: String, count: Int): List<Question>

    // 保存（添加）问题
    @Modifying
    @Transactional
    @Query("INSERT INTO questions (content, type, options, answer, difficulty, category, creator_id) VALUES (:content, :type, :options, :answer, :difficulty, :category, :creatorId)")
    fun saveQuestion(
        content: String,
        type: String,
        options: String?,
        answer: String?,
        difficulty: String,
        category: String?,
        creatorId: Long?
    ): Boolean


    //    fun deleteById(id: Long): Any?{
//        jdbcTemplate.update("DELETE FROM questions WHERE id = ?", id)
//    }
//    fun deleteQuestionById(id: Long):Any?
    @Modifying
    @Query("DELETE FROM questions WHERE id = :id")
    fun deleteById(id: Long): Any?


    @Query("SELECT * FROM questions WHERE id IN (:idList)")
    fun getByIdList(idList: List<Long>): List<Question>

    @Query("SELECT answer FROM questions WHERE id = :id")
    fun findAnswerById(id: Long): String?
}