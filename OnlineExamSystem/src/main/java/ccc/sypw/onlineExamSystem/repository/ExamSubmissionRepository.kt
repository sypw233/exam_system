package  ccc.sypw.onlineExamSystem.repository

import ccc.sypw.onlineExamSystem.model.ExamSubmission
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

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

    // 根据考试ID和阅卷状态查询提交记录
    @Query("SELECT * FROM exam_submissions WHERE exam_id = :examId AND is_graded = :isGraded")
    fun findByExamIdAndIsGraded(examId: Long, isGraded: Boolean): List<ExamSubmission>

    // 更新提交的得分和详情
    @Modifying
    @Query("UPDATE exam_submissions SET submit_score = :score, submit_detail = :detail WHERE id = :id")
    fun updateSubmissionScore(id: Long, score: Int, detail: String): Boolean

    // 更新阅卷状态
    @Modifying
    @Query("UPDATE exam_submissions SET is_graded = :isGraded WHERE id = :id")
    fun updateGradingStatus(id: Long, isGraded: Boolean): Boolean

    // 根据ID查找提交记录
    @Query("SELECT * FROM exam_submissions WHERE id = :id")
    override fun findById(id: Long): Optional<ExamSubmission>
}
