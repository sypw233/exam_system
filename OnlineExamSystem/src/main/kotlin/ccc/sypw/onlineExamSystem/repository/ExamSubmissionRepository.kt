package  ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.ExamSubmission
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ExamSubmissionRepository : CrudRepository<ExamSubmission, Long> {


    @Query("SELECT * FROM exam_submissions")
    fun findAllExamSubmission(): List<ExamSubmission>

    // 自定义查询：按 examId 查询所有提交记录
    @Query("SELECT * FROM exam_submissions WHERE exam_id = :examId")
    fun findByExamId(examId: Long): List<ExamSubmission>

    // 自定义查询：按 userId 查询所有提交记录
    @Query("SELECT * FROM exam_submissions WHERE user_id = :userId")
    fun findByUserId(userId: Long): List<ExamSubmission>

    @Modifying
    @Query("DELETE FROM exam_submissions WHERE id = :id")
    fun deleteExamSubmissionsById(id: Long)

    @Query("SELECT COUNT(*) > 0 FROM exam_submissions WHERE user_id =:userId AND exam_id = :examId")
    fun isHaveUserSubmission(userId: Long,examId: Long): Boolean
}
