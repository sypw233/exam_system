package ccc.sypw.onlineExamSystem.service

import ccc.sypw.onlineExamSystem.dto.QuestionDistribution
import ccc.sypw.onlineExamSystem.dto.RandomExamDto
import ccc.sypw.onlineExamSystem.model.Exams
import ccc.sypw.onlineExamSystem.model.Question
import ccc.sypw.onlineExamSystem.repository.ExamQuestionsRepository
import ccc.sypw.onlineExamSystem.repository.ExamRepository
import ccc.sypw.onlineExamSystem.repository.QuestionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ExamService(
    private val examRepository: ExamRepository,
    private val questionRepository: QuestionRepository,
    private val examQuestionsRepository: ExamQuestionsRepository
) {

    fun getAllExams(): List<Exams> {
        return examRepository.findAllExams()
    }

    // 获取试卷列表（分页）
    fun getExams(page: Int, size: Int): List<Exams> {
        val offset = (page - 1) * size
        return examRepository.findExamsWithPagination(size, offset)
    }

    // 获取试卷列表By学生Id（分页）
    fun getExamsByStudentId(id: Long, page: Int, size: Int): List<Exams> {
        val offset = (page - 1) * size
        return examRepository.findExamsByStudentIdWithPagination(id, size, offset)
    }


    //    获取数据总条目
    fun getExamsDataNum(): Int {
        return examRepository.findExamsDataNum()
    }

    fun getExamsDataNumByStudentId(id: Long): Int {
        return examRepository.findExamsDataNumByStudentId(id)
    }

    // 根据试卷名称进行分页搜索
    fun searchExams(key: String, page: Int, size: Int): List<Exams> {
        val offset = (page - 1) * size
        return examRepository.findByTitleContainingWithPagination("%$key%", size, offset)
    }



    fun searchExamsNum(key: String): Int {
        return examRepository.findExamsContainingDataNum("%$key%")
    }

    fun deleteExams(id: Long) {
        return examRepository.deleteById(id)
    }

    fun findById(id: Long): Exams? {
        return examRepository.findById(id)
    }

    fun findTitleById(id: Long): String? {
        return examRepository.findTitleById(id)
    }

    fun findTotalScoreById(id: Long): String? {
        return examRepository.findTitleById(id)
    }

    // 创建考试
    @Transactional
    fun createExam(exam: Exams): Exams? {
        // 检查是否有主观题（简答题）
        var needsGrading = false
        if (exam.questions != null) {
            for (questionId in exam.questions) {
                val question = questionRepository.findById(questionId)
                if (question != null && (question.type == "short_answer" || question.type == "fill_blank")) {
                    needsGrading = true
                    break
                }
            }
        }
        
        examRepository.insertExam(
            exam.title,
            exam.description,
            exam.startTime,
            exam.endTime,
            exam.totalScore,
            exam.creatorId,
            exam.courseId,
            needsGrading
        )
        val examId = examRepository.getLastInsertId()
        println("examId$examId")
        // 如果考试创建成功，并且有试题需要关联
        if (examId > 0 && exam.questions != null) {
            // 均分试题分数
            val scorePerQuestion: Int = exam.totalScore / exam.questions!!.size
            // 关联试题，并分配分数
            for (questionId in exam.questions) {
                examQuestionsRepository.save(examId = examId, questionId = questionId, score = scorePerQuestion)
            }
        }
        return if (examId != null && examId > 0) findById(examId) else null
    }

    // 随机组卷功能
    fun createRandomExam(randomExamDto: RandomExamDto): Exams? {
        // 解析日期时间字符串
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val startTime = LocalDateTime.parse(randomExamDto.startTime, formatter)
        val endTime = LocalDateTime.parse(randomExamDto.endTime, formatter)
        
        // 创建考试基本信息
        val exam = Exams(
            id = null,
            title = randomExamDto.title,
            description = randomExamDto.description,
            startTime = startTime,
            endTime = endTime,
            creatorId = randomExamDto.creatorId,
            createTime = LocalDateTime.now(),
            totalScore = randomExamDto.totalScore,
            questions = null,
            courseId = randomExamDto.courseId
        )
        
        // 根据分布获取随机题目
        val selectedQuestions = mutableListOf<Pair<Long, Int>>() // Pair<问题ID, 分数>
        var needsGrading = false
        
        // 遍历题目分布，抽取符合条件的随机题目
        for (distribution in randomExamDto.questionDistribution) {
            val questions = getRandomQuestions(distribution)
            
            // 检查是否有主观题
            if (distribution.type == "short_answer" || distribution.type == "fill_blank") {
                needsGrading = true
            }
            
            // 将抽取的题目添加到选中列表
            questions.forEach { question ->
                selectedQuestions.add(Pair(question.id!!, distribution.scorePerQuestion))
            }
        }
        
        // 创建考试，获取考试ID
        val examId = createExamWithQuestions(exam, selectedQuestions, needsGrading)
        
        return if (examId != null && examId > 0) findById(examId) else null
    }
    
    // 创建考试并关联题目的事务方法
    @Transactional
    fun createExamWithQuestions(exam: Exams, selectedQuestions: List<Pair<Long, Int>>, needsGrading: Boolean): Long? {
        // 创建考试
        examRepository.insertExam(
            exam.title,
            exam.description,
            exam.startTime,
            exam.endTime,
            exam.totalScore,
            exam.creatorId,
            exam.courseId,
            needsGrading
        )
        
        // 获取刚插入的考试ID
        val examId = examRepository.getLastInsertId()
        println("随机组卷 - 获取到的examId: $examId")
        
        // 关联题目到考试，并分配分数
        for ((questionId, score) in selectedQuestions) {
            println("随机组卷 - 准备插入: examId=$examId, questionId=$questionId, score=$score")
            examQuestionsRepository.save(examId = examId, questionId = questionId, score = score)
        }
        
        return examId
    }
    
    // 根据分布获取随机题目
    private fun getRandomQuestions(distribution: QuestionDistribution): List<Question> {
        return when {
            // 按类型、分类和难度筛选
            distribution.category != null && distribution.difficulty != null -> {
                questionRepository.findRandomQuestionsByTypeAndCategoryAndDifficulty(
                    distribution.type,
                    distribution.category,
                    distribution.difficulty,
                    distribution.count
                )
            }
            // 仅按类型和分类筛选
            distribution.category != null -> {
                questionRepository.findRandomQuestionsByTypeAndCategory(
                    distribution.type,
                    distribution.category,
                    distribution.count
                )
            }
            // 仅按类型和难度筛选
            distribution.difficulty != null -> {
                questionRepository.findRandomQuestionsByTypeAndDifficulty(
                    distribution.type,
                    distribution.difficulty,
                    distribution.count
                )
            }
            // 仅按类型筛选
            else -> {
                questionRepository.findRandomQuestionsByType(distribution.type, distribution.count)
            }
        }
    }
    
    // 获取特定科目/课程的考试
    fun getExamsByCourseId(courseId: Long, page: Int, size: Int): List<Exams> {
        val offset = (page - 1) * size
        return examRepository.findExamsByCourseId(courseId, size, offset)
    }

    // 获取特定科目/课程的考试总数
    fun getExamsCountByCourseId(courseId: Long): Int {
        return examRepository.findExamsCountByCourseId(courseId)
    }
}
