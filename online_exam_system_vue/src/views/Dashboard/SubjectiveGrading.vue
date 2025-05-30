<template>
  <div class="subjective-grading-container">
    <el-card class="header-card">
      <div class="header-content">
        <h2>主观题智能评分管理</h2>
        <p class="description">查看和调整需要人工复审的主观题评分结果</p>
      </div>
    </el-card>

    <!-- 考试选择 -->
    <el-card class="exam-selector-card">
      <el-form :inline="true" class="exam-form">
        <el-form-item label="选择考试:">
          <el-select 
            v-model="selectedExamId" 
            placeholder="请选择考试" 
            @change="loadReviewItems"
            style="width: 300px"
          >
            <el-option
              v-for="exam in examList"
              :key="exam.id"
              :label="exam.title"
              :value="exam.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadReviewItems" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 待复审列表 -->
    <el-card class="review-list-card" v-if="selectedExamId">
      <template #header>
        <div class="card-header">
          <span>待复审主观题 ({{ reviewItems.length }})</span>
          <el-tag v-if="reviewItems.length === 0" type="success">暂无需要复审的题目</el-tag>
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>

      <div v-else-if="reviewItems.length === 0" class="empty-container">
        <el-empty description="暂无需要复审的主观题" />
      </div>

      <div v-else>
        <el-collapse v-model="activeNames" accordion>
          <el-collapse-item 
            v-for="(item, index) in reviewItems" 
            :key="index"
            :name="index.toString()"
          >
            <template #title>
              <div class="collapse-title">
                <el-tag type="warning" size="small">待复审</el-tag>
                <span class="question-preview">{{ item.questionContent.substring(0, 50) }}...</span>
                <el-tag size="small">{{ item.currentScore }}/{{ item.maxScore }}分</el-tag>
              </div>
            </template>

            <div class="review-item-content">
              <!-- 题目信息 -->
              <el-row :gutter="20">
                <el-col :span="24">
                  <div class="question-section">
                    <h4>题目内容</h4>
                    <div class="question-content">{{ item.questionContent }}</div>
                  </div>
                </el-col>
              </el-row>

              <el-row :gutter="20" style="margin-top: 20px">
                <!-- 学生答案 -->
                <el-col :span="12">
                  <div class="answer-section">
                    <h4>学生答案</h4>
                    <el-input
                      type="textarea"
                      v-model="item.studentAnswer"
                      :rows="6"
                      readonly
                      class="answer-textarea"
                    />
                  </div>
                </el-col>

                <!-- 标准答案 -->
                <el-col :span="12">
                  <div class="answer-section">
                    <h4>标准答案</h4>
                    <el-input
                      type="textarea"
                      v-model="item.standardAnswer"
                      :rows="6"
                      readonly
                      class="answer-textarea"
                    />
                  </div>
                </el-col>
              </el-row>

              <!-- AI评分建议 -->
              <el-row style="margin-top: 20px">
                <el-col :span="24">
                  <div class="ai-suggestion-section">
                    <h4>AI评分建议</h4>
                    <el-alert
                      :title="`当前得分: ${item.currentScore}/${item.maxScore}分`"
                      type="info"
                      :description="item.aiSuggestion"
                      show-icon
                      :closable="false"
                    />
                  </div>
                </el-col>
              </el-row>

              <!-- 手动调分 -->
              <el-row style="margin-top: 20px">
                <el-col :span="24">
                  <div class="manual-grade-section">
                    <h4>手动调分</h4>
                    <el-form :inline="true">
                      <el-form-item label="调整分数:">
                        <el-input-number
                          v-model="item.newScore"
                          :min="0"
                          :max="item.maxScore"
                          :step="1"
                          size="default"
                        />
                        <span class="score-hint">/ {{ item.maxScore }}分</span>
                      </el-form-item>
                      <el-form-item label="教师评语:">
                        <el-input
                          v-model="item.teacherComment"
                          placeholder="请输入评语（可选）"
                          style="width: 300px"
                        />
                      </el-form-item>
                      <el-form-item>
                        <el-button 
                          type="primary" 
                          @click="submitManualGrade(item)"
                          :loading="item.grading"
                        >
                          确认调分
                        </el-button>
                        <el-button 
                          type="default" 
                          @click="reGrade(item)"
                          :loading="item.regrading"
                        >
                          重新AI评分
                        </el-button>
                      </el-form-item>
                    </el-form>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import api from '@/api/axios.js'

export default {
  name: 'SubjectiveGrading',
  components: {
    Refresh
  },
  setup() {
    const loading = ref(false)
    const examList = ref([])
    const selectedExamId = ref(null)
    const reviewItems = ref([])
    const activeNames = ref([])

    /**
     * 加载考试列表
     */
    const loadExamList = async () => {
      try {
        const response = await api.get('/exams/1/100') // 获取前100个考试
        examList.value = response.data.records || []
      } catch (error) {
        console.error('加载考试列表失败:', error)
        ElMessage.error('加载考试列表失败')
      }
    }

    /**
     * 加载需要复审的项目
     */
    const loadReviewItems = async () => {
      if (!selectedExamId.value) {
        ElMessage.warning('请先选择考试')
        return
      }

      loading.value = true
      try {
        const response = await api.get(`/subjective-grading/review-needed/${selectedExamId.value}`)
        const items = response.data || []
        
        // 为每个项目添加响应式属性
        reviewItems.value = items.map(item => reactive({
          ...item,
          newScore: item.currentScore,
          teacherComment: '',
          grading: false,
          regrading: false
        }))
        
        ElMessage.success(`加载完成，找到 ${items.length} 个需要复审的题目`)
      } catch (error) {
        console.error('加载复审项目失败:', error)
        ElMessage.error('加载复审项目失败')
        reviewItems.value = []
      } finally {
        loading.value = false
      }
    }

    /**
     * 提交手动评分
     */
    const submitManualGrade = async (item) => {
      if (item.newScore < 0 || item.newScore > item.maxScore) {
        ElMessage.error('分数超出范围')
        return
      }

      try {
        await ElMessageBox.confirm(
          `确认将分数调整为 ${item.newScore}/${item.maxScore} 分吗？`,
          '确认调分',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        item.grading = true
        
        const response = await api.post('/subjective-grading/manual-grade', {
          submissionId: item.submissionId,
          questionId: item.questionId,
          newScore: item.newScore,
          teacherComment: item.teacherComment
        })

        ElMessage.success('调分成功')
        
        // 从列表中移除已处理的项目
        const index = reviewItems.value.findIndex(i => 
          i.submissionId === item.submissionId && i.questionId === item.questionId
        )
        if (index > -1) {
          reviewItems.value.splice(index, 1)
        }
        
      } catch (error) {
        if (error !== 'cancel') {
          console.error('调分失败:', error)
          ElMessage.error('调分失败')
        }
      } finally {
        item.grading = false
      }
    }

    /**
     * 重新AI评分
     */
    const reGrade = async (item) => {
      item.regrading = true
      try {
        const response = await api.post(
          `/subjective-grading/re-grade/${item.submissionId}/${item.questionId}`,
          {
            studentAnswer: item.studentAnswer,
            maxScore: item.maxScore,
            keywords: []
          }
        )

        const result = response.data
        item.currentScore = result.score
        item.newScore = result.score
        item.aiSuggestion = result.details
        
        ElMessage.success(`重新评分完成，得分: ${result.score}/${item.maxScore}`)
        
        // 如果不再需要复审，从列表中移除
        if (!result.needsReview) {
          const index = reviewItems.value.findIndex(i => 
            i.submissionId === item.submissionId && i.questionId === item.questionId
          )
          if (index > -1) {
            reviewItems.value.splice(index, 1)
            ElMessage.info('该题目已不需要复审，已从列表中移除')
          }
        }
        
      } catch (error) {
        console.error('重新评分失败:', error)
        ElMessage.error('重新评分失败')
      } finally {
        item.regrading = false
      }
    }

    onMounted(() => {
      loadExamList()
    })

    return {
      loading,
      examList,
      selectedExamId,
      reviewItems,
      activeNames,
      loadReviewItems,
      submitManualGrade,
      reGrade
    }
  }
}
</script>

<style scoped>
.subjective-grading-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-card {
  margin-bottom: 20px;
}

.header-content h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.exam-selector-card {
  margin-bottom: 20px;
}

.exam-form {
  margin: 0;
}

.review-list-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container,
.empty-container {
  padding: 40px;
  text-align: center;
}

.collapse-title {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.question-preview {
  flex: 1;
  color: #606266;
}

.review-item-content {
  padding: 20px;
  background-color: #fafafa;
  border-radius: 8px;
}

.question-section,
.answer-section,
.ai-suggestion-section,
.manual-grade-section {
  margin-bottom: 20px;
}

.question-section h4,
.answer-section h4,
.ai-suggestion-section h4,
.manual-grade-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.question-content {
  padding: 15px;
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  line-height: 1.6;
}

.answer-textarea {
  background-color: #fff;
}

.score-hint {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}

:deep(.el-collapse-item__header) {
  padding-left: 20px;
  padding-right: 20px;
}

:deep(.el-collapse-item__content) {
  padding: 0;
}

:deep(.el-alert__description) {
  white-space: pre-line;
  line-height: 1.6;
}
</style>