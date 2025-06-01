package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.dto.ExamSubmissionResponse
import ccc.sypw.onlineExamSystem.repository.ExamSubmissionRepository
import ccc.sypw.onlineExamSystem.model.ExamSubmission
import ccc.sypw.onlineExamSystem.repository.ExamRepository
import ccc.sypw.onlineExamSystem.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class ExamSubmissionService(
    private val examSubmissionRepository: ExamSubmissionRepository,
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository,
) {

    fun ExamSubmission.toExamSubmissionResponse(): ExamSubmissionResponse {
        return ExamSubmissionResponse(
            id = this.id,
            userId = this.userId,
            examId = this.examId,
            userName = userRepository.findUsernameById(this.userId),  // 根据实际需求获取
            examTitle = examRepository.findTitleById(this.examId),  // 根据实际需求获取
            examTotalScore = examRepository.findTotalScoreById(examId),  // 根据实际需求获取
            submissionScore = this.submitScore ?: 0,  // 如果 submitScore 为 null，设置为 0
            answers = this.answers,
            isGraded = this.isGraded,
        )
    }

    fun getAllSubmissions(): List<ExamSubmissionResponse> {
        val examSubmissions = examSubmissionRepository.findAllExamSubmission()
        return examSubmissions.map { examSubmission ->
            examSubmission.toExamSubmissionResponse()
        }
    }

    fun getSubmissionsByExam(examId: Long): List<ExamSubmissionResponse> {
        val examSubmissions = examSubmissionRepository.findByExamId(examId)
        return examSubmissions.map { examSubmission ->
            examSubmission.toExamSubmissionResponse()
        }
    }

    fun getSubmissionsByUser(userId: Long): List<ExamSubmissionResponse> {
        val examSubmissions = examSubmissionRepository.findByUserId(userId)
        return examSubmissions.map { examSubmission ->
            examSubmission.toExamSubmissionResponse()
        }
    }

    fun submitExam(examSubmission: ExamSubmission): ExamSubmission {
        return examSubmissionRepository.save(examSubmission)
    }

    fun deleteExamSubmission(id: Long): Any {
        return examSubmissionRepository.deleteExamSubmissionsById(id)
    }


    fun isHaveUserSubmission(userId: Long,examId:Long): Boolean {
        return examSubmissionRepository.isHaveUserSubmission(userId,examId)
    }
}
