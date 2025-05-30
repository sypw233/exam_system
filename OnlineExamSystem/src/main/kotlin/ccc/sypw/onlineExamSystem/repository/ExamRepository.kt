package ccc.sypw.onlineExamSystem.repository


import ccc.sypw.onlineExamSystem.model.Exams
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ExamRepository : CrudRepository<Exams, String> {

    //    获取全部
    @Query("SELECT * FROM exams ORDER BY end_time DESC LIMIT :limit OFFSET :offset")
    fun findExamsWithPagination(limit: Int, offset: Int): List<Exams>


    @Query("SELECT * FROM exams e JOIN course_selections cs ON e.course_id = cs.course_id WHERE cs.student_id = :studentId ORDER BY end_time DESC LIMIT :limit OFFSET :offset")
    fun findExamsByStudentId(studentId:Long, limit: Int, offset: Int): List<Exams>
    
    @Query("SELECT * FROM exams e JOIN course_selections cs ON e.course_id = cs.course_id WHERE cs.student_id = :studentId ORDER BY end_time DESC LIMIT :limit OFFSET :offset")
    fun findExamsByStudentIdWithPagination(studentId:Long, limit: Int, offset: Int): List<Exams>

    @Query("SELECT * FROM exams")
    fun findAllExams(): List<Exams>

    //    获取总条目数
    @Query("SELECT COUNT(*) FROM exams")
    fun findExamsDataNum(): Int

    //    获取总条目数by studentId
    @Query("SELECT COUNT(*) FROM exams e JOIN course_selections cs ON e.course_id = cs.course_id WHERE cs.student_id = :studentId")
    fun findExamsDataNumByStudentId(studentId: Long): Int
    
    // 根据科目/课程ID查询考试
    @Query("SELECT * FROM exams WHERE course_id = :courseId ORDER BY end_time DESC LIMIT :limit OFFSET :offset")
    fun findExamsByCourseId(courseId: Long, limit: Int, offset: Int): List<Exams>
    
    // 获取特定科目/课程的考试总数
    @Query("SELECT COUNT(*) FROM exams WHERE course_id = :courseId")
    fun findExamsCountByCourseId(courseId: Long): Int

    //    获取模糊查找总条目数
    @Query("SELECT COUNT(*) FROM exams WHERE title LIKE :keyword")
    fun searchExamsNum(keyword: String): Int

    //    模糊查找
    @Query("SELECT * FROM exams WHERE title LIKE :keyword LIMIT :limit OFFSET :offset")
    fun searchExams(keyword: String, limit: Int, offset: Int): List<Exams>
    
    @Query("SELECT * FROM exams WHERE title LIKE :keyword LIMIT :limit OFFSET :offset")
    fun findByTitleContainingWithPagination(keyword: String, limit: Int, offset: Int): List<Exams>
    
    @Query("SELECT COUNT(*) FROM exams WHERE title LIKE :keyword")
    fun findExamsContainingDataNum(keyword: String): Int

    @Query("SELECT * FROM exams WHERE id = :id")
    fun findById(id: Long): Exams?

    @Query("SELECT title FROM exams WHERE id = :id")
    fun findTitleById(id: Long): String?

    @Query("SELECT total_score FROM exams WHERE id = :id")
    fun findTotalScoreById(id: Long): Int?

    @Modifying
    @Query("DELETE FROM exams WHERE id = :id")
    fun deleteById(id: Long): Any?

    /**
     * 保存考试记录并返回生成的ID
     * 使用两步操作：先插入记录，再获取最后插入的ID
     */
    @Modifying
    @Query("INSERT INTO exams (title, description, start_time, end_time, creator_id, total_score, course_id) VALUES (:title, :description, :startTime, :endTime, :creatorId, :totalScore, :courseId)")
    fun insertExam(
        title: String,
        description: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        creatorId: Long,
        totalScore: Int,
        courseId: Long
    ): Int

    @Query("SELECT LAST_INSERT_ID()")
    fun getLastInsertId(): Long

    @Modifying
    @Query("INSERT INTO exams (title, description, start_time, end_time, creator_id, total_score, course_id) VALUES (:title, :description, :startTime, :endTime, :creatorId, :totalScore, :courseId)")
    fun saveExam(
        title: String,
        description: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        creatorId: Long,
        totalScore: Int,
        courseId: Long
    ): Int
    
    @Modifying
    @Query("INSERT INTO exams (title, description, start_time, end_time, creator_id, total_score, course_id) VALUES (:title, :description, :startTime, :endTime, :creatorId, :totalScore, :courseId)")
    fun saveExamAndReturnId(
        title: String,
        description: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        creatorId: Long,
        totalScore: Int,
        courseId: Long
    ): Int
}

