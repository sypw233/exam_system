package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.repository.ExamQuestionsRepository
import org.springframework.stereotype.Service

@Service
class ExamQuestionService(
    private val examQuestionsRepository: ExamQuestionsRepository
) {
    fun findByExamId(id: Long):List<Long>? {
       return examQuestionsRepository.findByExamId(id)
    }
}