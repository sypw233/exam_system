package ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.ExamQuestions
import ccc.sypw.onlineExamSystem.model.Question
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ExamQuestionsRepository : CrudRepository<ExamQuestions, String> {


    @Query("SELECT question_id FROM exam_questions WHERE exam_id=:id")
    fun findByExamId(id: Long): List<Long>?


    fun deleteByExamId(examId: Long)


    @Modifying
    @Query("INSERT INTO exam_questions (exam_id, question_id) VALUES (:examId, :questionId)")
    fun save(examId: Long, questionId: Long)
    
    @Modifying
    @Query("INSERT INTO exam_questions (exam_id, question_id, score) VALUES (:examId, :questionId, :score)")
    fun saveExamQuestion(examId: Long, questionId: Long, score: Int)
}