<template>
  <div class="exam-container">
    <!-- 考试头部信息 -->
    <div class="exam-header">
      <div class="exam-info">
        <h1 class="exam-title">{{ examData.title }}</h1>
        <div class="exam-meta">
          <div class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>考试时间：{{ formatDateTime(examData.startTime) }} - {{ formatDateTime(examData.endTime) }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Document /></el-icon>
            <span>总分：{{ examData.totalScore }}分</span>
          </div>
          <div class="meta-item">
            <el-icon><EditPen /></el-icon>
            <span>题目数量：{{ examData.questions?.length || 0 }}题</span>
          </div>
        </div>
        <div class="exam-description" v-if="examData.description">
          <p>{{ examData.description }}</p>
        </div>
      </div>
      
      <!-- 倒计时和提交按钮 -->
      <div class="exam-actions">
        <div class="countdown-container">
          <div class="countdown-display" :class="getCountdownClass()">
            <el-icon><Timer /></el-icon>
            <span v-if="remainingTime > 0">{{ formattedTime }}</span>
            <span v-else-if="remainingTime === 0 && !isSubmitting">时间已到</span>
            <span v-else-if="isSubmitting">提交中...</span>
          </div>
        </div>
        <el-button 
          type="primary" 
          size="large" 
          @click="submitExam" 
          :disabled="remainingTime <= 0 || isSubmitting" 
          :loading="isSubmitting"
          class="submit-button"
        >
          {{ isSubmitting ? '提交中...' : '提交考试' }}
        </el-button>
      </div>
    </div>

    <!-- 考试内容区域 -->
    <div class="exam-content">
      <!-- 题目导航 -->
      <div class="question-nav">
        <div class="nav-title">题目导航</div>
        <div class="nav-grid">
          <div 
            v-for="(question, index) in examData.questions" 
            :key="question.id"
            class="nav-item"
            :class="getNavItemClass(question)"
            @click="scrollToQuestion(question.id)"
          >
            {{ index + 1 }}
          </div>
        </div>
        <div class="nav-legend">
          <div class="legend-item">
            <div class="legend-color answered"></div>
            <span>已答</span>
          </div>
          <div class="legend-item">
            <div class="legend-color unanswered"></div>
            <span>未答</span>
          </div>
        </div>
      </div>

      <!-- 题目列表 -->
      <div class="questions-container">
        <el-form :model="answers" label-position="top">
          <div 
            v-for="(question, index) in examData.questions" 
            :key="question.id"
            :id="`question-${question.id}`"
            class="question-card"
          >
            <!-- 题目头部 -->
            <div class="question-header">
              <div class="question-number">第{{ index + 1 }}题</div>
              <div class="question-type">{{ getQuestionTypeText(question.type) }}</div>
              <div class="question-difficulty">{{ getDifficultyText(question.difficulty) }}</div>
            </div>

            <!-- 题目内容 -->
            <div class="question-content">
              <h3 class="question-title">{{ question.content }}</h3>

              <!-- 单选题 -->
              <template v-if="question.type === 'single'">
                <el-radio-group v-model="answers[question.id]" class="question-options">
                  <div 
                    v-for="(option, optionIndex) in parseOptions(question.options)" 
                    :key="optionIndex"
                    class="option-item"
                  >
                    <el-radio :value="getOptionKey(optionIndex)" class="option-radio">
                      <span class="option-label">{{ getOptionKey(optionIndex) }}.</span>
                      <span class="option-text">{{ option }}</span>
                    </el-radio>
                  </div>
                </el-radio-group>
              </template>

              <!-- 多选题 -->
              <template v-if="question.type === 'multiple'">
                <el-checkbox-group v-model="answers[question.id]" class="question-options">
                  <div 
                    v-for="(option, optionIndex) in parseOptions(question.options)" 
                    :key="optionIndex"
                    class="option-item"
                  >
                    <el-checkbox :value="getOptionKey(optionIndex)" class="option-checkbox">
                      <span class="option-label">{{ getOptionKey(optionIndex) }}.</span>
                      <span class="option-text">{{ option }}</span>
                    </el-checkbox>
                  </div>
                </el-checkbox-group>
              </template>

              <!-- 填空题 -->
              <template v-if="question.type === 'fill_blank'">
                <div class="fill-blank-container">
                  <el-input 
                    v-model="answers[question.id]" 
                    placeholder="请输入答案" 
                    class="fill-blank-input"
                    clearable
                  />
                </div>
              </template>

              <!-- 简答题 -->
              <template v-if="question.type === 'short_answer'">
                <div class="textarea-container">
                  <el-input 
                    type="textarea" 
                    v-model="answers[question.id]" 
                    placeholder="请详细回答问题..." 
                    class="textarea-input"
                    :rows="6"
                    maxlength="1000"
                    show-word-limit
                    resize="vertical"
                  />
                </div>
              </template>

              <!-- 判断题 -->
              <template v-if="question.type === 'true_false'">
                <el-radio-group v-model="answers[question.id]" class="question-options">
                  <div class="option-item">
                    <el-radio value="true" class="option-radio">
                      <span class="option-label">A.</span>
                      <span class="option-text">正确</span>
                    </el-radio>
                  </div>
                  <div class="option-item">
                    <el-radio value="false" class="option-radio">
                      <span class="option-label">B.</span>
                      <span class="option-text">错误</span>
                    </el-radio>
                  </div>
                </el-radio-group>
              </template>

              <!-- 论述题 -->
              <template v-if="question.type === 'essay'">
                <div class="textarea-container">
                  <el-input 
                    type="textarea" 
                    v-model="answers[question.id]" 
                    placeholder="请详细论述..." 
                    class="textarea-input"
                    :rows="10"
                    maxlength="2000"
                    show-word-limit
                    resize="vertical"
                  />
                </div>
              </template>
            </div>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="exam-footer">
      <div class="footer-info">
        <span>已答题：{{ getAnsweredCount() }}/{{ examData.questions?.length || 0 }}</span>
      </div>
      <div class="footer-actions">
        <el-button size="large" @click="scrollToTop">返回顶部</el-button>
        <el-button 
          type="primary" 
          size="large" 
          @click="submitExam" 
          :disabled="remainingTime <= 0 || isSubmitting" 
          :loading="isSubmitting"
        >
          {{ isSubmitting ? '提交中...' : '提交考试' }}
        </el-button>
      </div>
    </div>

    <!-- 回到顶部按钮 -->
    <el-backtop :visibility-height="200" :right="50" :bottom="100" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Document, EditPen, Timer } from '@element-plus/icons-vue'
import api from '@/api/axios.js'

// 路由相关
const route = useRoute()
const router = useRouter()

// 响应式数据
const examId = ref(null)
const examData = ref({
  id: null,
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  totalScore: 0,
  questions: []
})
const answers = ref({})
const remainingTime = ref(0)
const intervalId = ref(null)
const isSubmitting = ref(false)

/**
 * 获取考试详情数据
 */
const getExamData = async () => {
  try {
    console.log('开始获取考试数据，examId:', examId.value)
    const response = await api.get(`/exams/${examId.value}/details`)
    console.log('API响应:', response)
    // 修正：API返回的数据结构是 {code: 200, message: '', data: {...}}
    const apiData = response.data
    const data = apiData.data // 获取实际的考试数据
    console.log('考试数据:', data)
    
    // 更新考试数据
    examData.value = {
      id: data.id,
      title: data.title,
      description: data.description,
      startTime: data.startTime,
      endTime: data.endTime,
      totalScore: data.totalScore,
      questions: data.questions || []
    }
    
    console.log('更新后的examData:', examData.value)
    
    // 初始化答案对象
    initializeAnswers()
    
    // 验证考试时间并启动倒计时
    validateExamTime()
    
  } catch (error) {
    console.error('获取考试详情失败:', error)
    console.error('错误详情:', error.response)
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
    }
    handleApiError(error)
  }
}

/**
 * 初始化答案对象
 */
const initializeAnswers = () => {
  examData.value.questions.forEach(question => {
    if (question.type === 'multiple') {
      answers.value[question.id] = []
    } else {
      answers.value[question.id] = ''
    }
  })
}

/**
 * 验证考试时间
 */
const validateExamTime = () => {
  const startTime = new Date(examData.value.startTime).getTime()
  const endTime = new Date(examData.value.endTime).getTime()
  const currentTime = Date.now()
  
  if (currentTime < startTime) {
    ElMessage.error('考试尚未开始，请在考试时间内参加')
    router.push({ path: '/' })
    return
  }
  
  if (currentTime >= endTime) {
    ElMessage.error('考试时间已结束，无法参加考试')
    router.push({ path: '/' })
    return
  }
  
  // 计算剩余时间并启动倒计时
  remainingTime.value = Math.floor((endTime - currentTime) / 1000)
  startCountdown()
}

/**
 * 启动倒计时
 */
const startCountdown = () => {
  if (intervalId.value) {
    console.warn('倒计时已在运行，避免重复启动')
    return
  }
  
  console.log('启动倒计时，剩余时间:', remainingTime.value, '秒')
  
  intervalId.value = setInterval(() => {
    if (remainingTime.value > 1) {
      remainingTime.value--
    } else if (remainingTime.value === 1) {
      remainingTime.value = 0
      console.log('考试时间结束，准备自动提交')
      clearInterval(intervalId.value)
      intervalId.value = null
      
      // 只有在未提交状态下才自动提交
      if (!isSubmitting.value) {
        ElMessage.warning('考试时间已到，系统将自动提交试卷')
        setTimeout(() => {
          submitExam()
        }, 1000)
      }
    }
  }, 1000)
}

/**
 * 停止倒计时
 */
const stopCountdown = () => {
  if (intervalId.value) {
    console.log('停止倒计时')
    clearInterval(intervalId.value)
    intervalId.value = null
  }
}

/**
 * 提交考试
 */
const submitExam = async () => {
  // 防止重复提交
  if (isSubmitting.value) {
    console.warn('正在提交中，请勿重复操作')
    return
  }
  
  // 检查是否有未答题目
  const unansweredCount = getUnansweredCount()
  if (unansweredCount > 0) {
    try {
      await ElMessageBox.confirm(
        `还有 ${unansweredCount} 道题未作答，确定要提交吗？`,
        '提交确认',
        {
          confirmButtonText: '确定提交',
          cancelButtonText: '继续答题',
          type: 'warning'
        }
      )
    } catch {
      return // 用户取消提交
    }
  }
  
  console.log('开始提交考试')
  isSubmitting.value = true
  stopCountdown()
  
  const submitData = {
    userId: localStorage.getItem('id'),
    examId: examId.value,
    answers: answers.value
  }
  
  try {
    const response = await api.post('/exam-submissions/submit', submitData)
    console.log('提交成功，跳转到结果页面')
    ElMessage.success('提交成功')
    
    // 跳转到结果页面
    await router.push({
      path: '/answer/result',
      query: {
        totalScore: response.data.submitScore,
        submitTime: response.data.submitTime
      }
    })
  } catch (error) {
    console.error('提交失败', error)
    ElMessage.error('提交失败，请重试')
    isSubmitting.value = false
  }
}

/**
 * 处理API错误
 */
const handleApiError = (error) => {
  const status = error.response?.status
  switch (status) {
    case 409:
      ElMessage.error('获取试卷失败：已经提交过该试卷')
      break
    case 403:
      ElMessage.error('获取试卷失败：当前考试不在时间内')
      break
    default:
      ElMessage.error('获取考试详情失败，请重试')
  }
  router.push({ path: '/' })
}

// 计算属性
const formattedTime = computed(() => {
  const hours = Math.floor(remainingTime.value / 3600)
  const minutes = Math.floor((remainingTime.value % 3600) / 60)
  const seconds = remainingTime.value % 60
  
  if (hours > 0) {
    return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  }
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

// 工具函数
/**
 * 格式化日期时间
 */
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  
  try {
    const date = new Date(dateTimeString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (error) {
    console.error('日期格式化失败:', error)
    return dateTimeString
  }
}

/**
 * 解析选项
 */
const parseOptions = (optionsString) => {
  try {
    return JSON.parse(optionsString)
  } catch {
    return []
  }
}

/**
 * 获取选项键值
 */
const getOptionKey = (index) => {
  return String.fromCharCode(65 + index) // A, B, C, D...
}

/**
 * 获取题目类型文本
 */
const getQuestionTypeText = (type) => {
  const typeMap = {
    single: '单选题',
    multiple: '多选题',
    fill_blank: '填空题',
    short_answer: '简答题',
    true_false: '判断题',
    essay: '论述题'
  }
  return typeMap[type] || '未知题型'
}

/**
 * 获取难度文本
 */
const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return difficultyMap[difficulty] || '未知'
}

/**
 * 获取倒计时样式类
 */
const getCountdownClass = () => {
  if (remainingTime.value <= 0) return 'countdown-ended'
  if (remainingTime.value <= 300) return 'countdown-warning' // 5分钟警告
  return 'countdown-normal'
}

/**
 * 获取导航项样式类
 */
const getNavItemClass = (question) => {
  const answer = answers.value[question.id]
  const isAnswered = question.type === 'multiple' 
    ? Array.isArray(answer) && answer.length > 0
    : answer !== '' && answer !== null && answer !== undefined
  
  return isAnswered ? 'answered' : 'unanswered'
}

/**
 * 获取已答题数量
 */
const getAnsweredCount = () => {
  return examData.value.questions.filter(question => {
    const answer = answers.value[question.id]
    return question.type === 'multiple' 
      ? Array.isArray(answer) && answer.length > 0
      : answer !== '' && answer !== null && answer !== undefined
  }).length
}

/**
 * 获取未答题数量
 */
const getUnansweredCount = () => {
  return examData.value.questions.length - getAnsweredCount()
}

/**
 * 滚动到指定题目
 */
const scrollToQuestion = (questionId) => {
  const element = document.getElementById(`question-${questionId}`)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

/**
 * 滚动到顶部
 */
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 生命周期钩子
onMounted(() => {
  examId.value = route.query.examId
  if (!examId.value) {
    ElMessage.error('缺少考试ID参数')
    router.push({ path: '/' })
    return
  }
  getExamData()
})

onUnmounted(() => {
  stopCountdown()
})
</script>

<style scoped>
.exam-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 考试头部 */
.exam-header {
  background: white;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 16px 16px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.exam-info {
  flex: 1;
}

.exam-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 16px 0;
  line-height: 1.3;
}

.exam-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.meta-item .el-icon {
  color: #409eff;
}

.exam-description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.exam-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.countdown-container {
  text-align: center;
}

.countdown-display {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  min-width: 120px;
  justify-content: center;
}

.countdown-normal {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
}

.countdown-warning {
  background: linear-gradient(135deg, #e6a23c, #f0a020);
  color: white;
  animation: pulse 1s infinite;
}

.countdown-ended {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: white;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.submit-button {
  padding: 12px 32px;
  font-size: 16px;
  border-radius: 12px;
  font-weight: 600;
}

/* 考试内容区域 */
.exam-content {
  display: flex;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 题目导航 */
.question-nav {
  width: 280px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  height: fit-content;
  position: sticky;
  top: 24px;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 20px;
  text-align: center;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  margin-bottom: 20px;
}

.nav-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.nav-item.answered {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
}

.nav-item.unanswered {
  background: #f5f7fa;
  color: #909399;
  border-color: #dcdfe6;
}

.nav-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.nav-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-color.answered {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.legend-color.unanswered {
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
}

/* 题目容器 */
.questions-container {
  flex: 1;
  max-width: 900px;
}

.question-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.question-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.question-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.question-number {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.question-type {
  background: #f0f9ff;
  color: #409eff;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid #b3d8ff;
}

.question-difficulty {
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.question-difficulty:contains('简单') {
  background: #f0f9ff;
  color: #67c23a;
  border: 1px solid #c2e7b0;
}

.question-content {
  padding-left: 8px;
}

.question-title {
  font-size: 18px;
  font-weight: 500;
  color: #1a1a1a;
  line-height: 1.6;
  margin: 0 0 24px 0;
}

.question-options {
  margin-top: 20px;
}

.option-item {
  margin-bottom: 16px;
  padding: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.option-item:hover {
  background: #f8f9fa;
  border-color: #e9ecef;
}

.option-radio,
.option-checkbox {
  width: 100%;
  margin: 0;
}

.option-label {
  font-weight: 600;
  color: #409eff;
  margin-right: 12px;
  min-width: 24px;
  display: inline-block;
}

.option-text {
  font-size: 16px;
  line-height: 1.5;
}

.fill-blank-container,
.textarea-container {
  margin-top: 20px;
}

.fill-blank-input {
  font-size: 16px;
}

.textarea-input {
  font-size: 16px;
  line-height: 1.6;
}

/* 底部操作栏 */
.exam-footer {
  background: white;
  padding: 20px 24px;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px 16px 0 0;
  margin-top: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  bottom: 0;
  z-index: 100;
}

.footer-info {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.footer-actions {
  display: flex;
  gap: 16px;
}

.footer-actions .el-button {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 12px;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .exam-content {
    flex-direction: column;
  }
  
  .question-nav {
    width: 100%;
    position: static;
  }
  
  .nav-grid {
    grid-template-columns: repeat(8, 1fr);
  }
}

@media (max-width: 768px) {
  .exam-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .exam-actions {
    width: 100%;
    align-items: stretch;
  }
  
  .exam-meta {
    flex-direction: column;
    gap: 12px;
  }
  
  .question-card {
    padding: 20px;
  }
  
  .question-header {
    flex-wrap: wrap;
  }
  
  .nav-grid {
    grid-template-columns: repeat(6, 1fr);
  }
  
  .exam-footer {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .footer-actions {
    width: 100%;
    justify-content: center;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .exam-container {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d3748 100%);
  }
  
  .exam-header,
  .question-nav,
  .question-card,
  .exam-footer {
    background: #2d3748;
    color: #e2e8f0;
  }
  
  .exam-title {
    color: #f7fafc;
  }
  
  .question-title {
    color: #f7fafc;
  }
  
  .option-item:hover {
    background: #4a5568;
  }
}
</style>
