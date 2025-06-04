<template>
  <div class="submission-manager-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <el-icon class="title-icon">
            <Files/>
          </el-icon>
          交卷管理
        </h2>
        <p class="page-description">管理学生提交的所有考试答卷</p>
      </div>
    </div>
    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="never">
        <div class="search-content">
          <div class="search-left">
            <el-input
                v-model="searchKeyword"
                placeholder="输入关键词进行搜索"
                @input="searchSubmission"
                class="search-input"
                clearable
            >
              <template #prefix>
                <el-icon>
                  <Search/>
                </el-icon>
              </template>
            </el-input>
          </div>

          <div class="search-right">
            <el-button type="primary" @click="searchSubmission">
              <el-icon>
                <Refresh/>
              </el-icon>
              刷新数据
            </el-button>
          </div>
        </div>
        
        <!-- 筛选区域 -->
        <div class="filter-section">
          <div class="filter-row">
            <div class="filter-item">
              <label class="filter-label">阅卷状态：</label>
              <el-select
                  v-model="filters.gradingStatus"
                  placeholder="选择阅卷状态"
                  @change="applyFilters"
                  clearable
                  class="filter-select"
              >
                <el-option label="全部" value=""></el-option>
                <el-option label="已阅卷" value="graded"></el-option>
                <el-option label="待阅卷" value="ungraded"></el-option>
              </el-select>
            </div>
            
            <div class="filter-item">
              <label class="filter-label">考试筛选：</label>
              <el-select
                  v-model="filters.examId"
                  placeholder="选择考试"
                  @change="applyFilters"
                  clearable
                  class="filter-select"
              >
                <el-option label="全部考试" value=""></el-option>
                <el-option
                    v-for="exam in uniqueExams"
                    :key="exam.id"
                    :label="exam.title"
                    :value="exam.id"
                ></el-option>
              </el-select>
            </div>
            
            <div class="filter-item">
              <label class="filter-label">得分区间：</label>
              <div class="score-range">
                <el-input-number
                    v-model="filters.minScore"
                    placeholder="最低分"
                    @change="applyFilters"
                    :min="0"
                    :precision="0"
                    class="score-input"
                    size="small"
                ></el-input-number>
                <span class="score-separator">-</span>
                <el-input-number
                    v-model="filters.maxScore"
                    placeholder="最高分"
                    @change="applyFilters"
                    :min="0"
                    :precision="0"
                    class="score-input"
                    size="small"
                ></el-input-number>
              </div>
            </div>
            
            <div class="filter-item">
              <el-button @click="resetFilters" type="info" plain>
                <el-icon>
                  <Refresh/>
                </el-icon>
                重置筛选
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 交卷列表 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="table-header">
            <span class="table-title">交卷列表</span>
            <el-tag type="info" size="small">共 {{ filteredSubmissions.length }} 份答卷</el-tag>
          </div>
        </template>

        <el-table
            :data="filteredSubmissions"
            class="submission-table"
            stripe
            :header-cell-style="{ background: '#f8f9fa', color: '#495057' }"
        >
          <el-table-column prop="userName" label="用户名" min-width="120"></el-table-column>
          <el-table-column prop="examId" label="考试ID" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.examId }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="examTitle" label="考试" min-width="150"></el-table-column>
          <el-table-column prop="examTotalScore" label="总分" width="80" align="center"></el-table-column>
          <el-table-column prop="submissionScore" label="得分" width="80" align="center"></el-table-column>
          <el-table-column label="阅卷状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isGraded ? 'success' : 'warning'">
                {{ row.isGraded ? '已阅卷' : '待阅卷' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                    v-if="!row.isGraded"
                    type="warning"
                    size="small"
                    @click="startGrading(row.id)"
                >
                  <el-icon>
                    <Edit/>
                  </el-icon>
                  开始阅卷
                </el-button>
                <el-button
                    v-else
                    type="success"
                    size="small"
                    @click="viewGradingDetails(row.id)"
                >
                  <el-icon>
                    <View/>
                  </el-icon>
                  查看详情
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    @click="deleteSubmissions(row.id)"
                >
                  <el-icon>
                    <Delete/>
                  </el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 阅卷弹窗 -->
    <el-dialog
        v-model="gradingVisible"
        title="阅卷详情"
        width="90%"
        :before-close="closeGrading"
    >
      <div v-if="gradingDetails">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="学生姓名">{{ gradingDetails.studentName }}</el-descriptions-item>

          <el-descriptions-item label="考试标题">{{ gradingDetails.examTitle }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDateTime(gradingDetails.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="总分">{{ gradingDetails.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="当前得分">{{ gradingDetails.currentScore }}</el-descriptions-item>
          <el-descriptions-item label="阅卷状态">
            <el-tag :type="gradingDetails.isGraded ? 'success' : 'warning'">
              {{ gradingDetails.isGraded ? '已阅卷' : '待阅卷' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>题目详情</el-divider>

        <div v-for="(question, index) in gradingDetails.questions" :key="question.questionId" class="grading-question">
          <el-card class="grading-card">
            <div class="question-header">
              <h4>第{{ index + 1 }}题 (满分: {{ question.maxScore }}分)</h4>
              <div class="score-section">
                <div class="score-input">
                  <div class="score-input-container">
                    <el-input-number
                        v-model="question.currentScore"
                        :min="0"
                        :max="question.maxScore"
                        :precision="0"
                        size="small"
                        :disabled="gradingDetails.isGraded && !isEditing"
                        @change="updateQuestionScore(question.questionId || question.id, question.currentScore)"
                    />
                    <span class="score-label">分</span>
                  </div>
                </div>
                <div v-if="isSubjectiveQuestion(question.questionType)" class="ai-button-container">
                  <el-button
                      class="ai-grading-button"
                      type="primary"
                      size="small"
                      @click="openAiGradingDialog(question)"
                      :loading="question.aiLoading"
                  >
                    <el-icon>
                      <Service/>
                    </el-icon>
                    AI智能评分
                  </el-button>
                </div>
              </div>
            </div>

            <div class="question-content">
              <!-- 题目内容：选择题显示题目+选项，其他题型只显示题目 -->
              <div class="question-text">
                <p><strong>题目：</strong></p>
                <div class="question-detail">
                  <p>{{ question.questionContent }}</p>
                  <!-- 选择题直接在题目下方显示选项 -->
                  <div v-if="question.options && question.options.length > 0" class="inline-options">
                    <div v-for="(option, index) in question.options" :key="index" class="inline-option">
                      {{ String.fromCharCode(65 + index) }}. {{ option }}
                    </div>
                  </div>
                </div>
              </div>
              <p><strong>题目类型：</strong>{{ getQuestionTypeText(question.questionType) }}</p>

              <div class="answer-section">
                <div class="correct-answer">
                  <p><strong>标准答案：</strong></p>
                  <div class="answer-content">
                  <span v-if="question.options && question.options.length > 0">
                    {{ question.correctAnswer }} 
                    <span class="option-detail">({{ getOptionText(question.correctAnswer, question.options) }})</span>
                  </span>
                    <span v-else>{{ question.correctAnswer }}</span>
                  </div>
                </div>

                <div class="student-answer">
                  <p><strong>学生答案：</strong></p>
                  <div class="answer-content" :class="getAnswerClass(question.questionType)">
                  <span v-if="question.options && question.options.length > 0">
                    {{ question.studentAnswer }} 
                    <span class="option-detail">({{ getOptionText(question.studentAnswer, question.options) }})</span>
                  </span>
                    <span v-else>{{ question.studentAnswer }}</span>
                  </div>
                </div>
              </div>


            </div>
          </el-card>
        </div>

        <div class="grading-actions">
          <el-button v-if="!gradingDetails.isGraded" @click="completeGrading" type="primary">完成阅卷</el-button>
          <el-button v-if="gradingDetails.isGraded && !isEditing" @click="enableEditing" type="warning">编辑分数
          </el-button>
          <el-button v-if="isEditing" @click="saveEditing" type="success">保存修改</el-button>
          <el-button @click="closeGrading">关闭</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- AI辅助评分弹窗 -->
    <el-dialog
        v-model="aiGradingDialogVisible"
        title="AI辅助评分"
        width="60%"
        :before-close="closeAiGradingDialog"
    >
      <div v-if="currentAiQuestion">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="题目内容">{{ currentAiQuestion.questionContent }}</el-descriptions-item>
          <el-descriptions-item label="题目总分">{{ currentAiQuestion.maxScore }}分</el-descriptions-item>
          <el-descriptions-item label="标准答案" span="2">{{ currentAiQuestion.correctAnswer }}</el-descriptions-item>
          <el-descriptions-item label="学生答案" span="2">{{ currentAiQuestion.studentAnswer }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>AI评分结果</el-divider>

        <div class="ai-grading-result">
          <el-form :model="aiGradingForm" label-width="100px">
            <el-form-item label="参考分数">
              <el-input
                  v-model="aiGradingForm.suggestedScore"
                  placeholder="AI建议分数"
                  readonly
              >
                <template #append>分</template>
              </el-input>
            </el-form-item>

            <el-form-item label="评分理由">
              <el-input
                  v-model="aiGradingForm.gradingReason"
                  type="textarea"
                  :rows="4"
                  placeholder="AI评分理由"
                  readonly
              />
            </el-form-item>

            <!-- 流式显示区域 -->
            <div v-if="aiStreamingContent" class="streaming-content">
              <el-alert
                  title="AI正在评分中..."
                  type="info"
                  :closable="false"
                  show-icon
              >
                <div class="streaming-text">{{ aiStreamingContent }}</div>
              </el-alert>
            </div>
          </el-form>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAiGradingDialog">取消</el-button>
          <el-button
              type="primary"
              @click="startAiGrading"
              :loading="aiGradingLoading"
              :disabled="!currentAiQuestion"
          >
            开始AI评分
          </el-button>
          <el-button
              type="success"
              @click="applyAiScore"
              :disabled="!aiGradingForm.suggestedScore"
          >
            应用参考分数
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {onMounted, ref, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import api from "@/api/axios.js";
import {Delete, Edit, Files, Refresh, Service, View} from '@element-plus/icons-vue';

export default {
  name: 'ExamSubmission',
  components: {
    Delete,
    View,
    Edit,
    Refresh,
    Files,
    Service
  },

  setup() {
    const examSubmissions = ref([]);
    const searchKeyword = ref('');
    const filteredSubmissions = ref([]);
    
    // 筛选条件
    const filters = ref({
      gradingStatus: '', // 阅卷状态：graded(已阅卷), ungraded(待阅卷)
      examId: '', // 考试ID
      minScore: null, // 最低分
      maxScore: null // 最高分
    });
    
    // 唯一考试列表
    const uniqueExams = ref([]);

    // 试卷详情相关


    // 阅卷相关
    const gradingVisible = ref(false);
    const gradingDetails = ref(null);
    const isEditing = ref(false);
    const aiGradingLoading = ref(false);

    // AI辅助评分相关
    const aiGradingDialogVisible = ref(false);
    const currentAiQuestion = ref(null);
    const aiGradingForm = ref({
      suggestedScore: '',
      gradingReason: ''
    });
    const aiStreamingContent = ref('');


    // 查询所有考试提交记录
    const getAllSubmissions = async () => {
      try {
        const response = await api(`/exam-submissions/all`);
        if (response.status === 200) {
          examSubmissions.value = response.data;
          filteredSubmissions.value = examSubmissions.value;
          
          // 生成唯一考试列表
          generateUniqueExams();
          
          // 应用当前筛选条件
          applyFilters();
        }
      } catch (error) {
        console.error('Error fetching exam submissions:', error);
      }
    };
    
    /**
     * 生成唯一考试列表
     */
    const generateUniqueExams = () => {
      const examMap = new Map();
      examSubmissions.value.forEach(submission => {
        if (!examMap.has(submission.examId)) {
          examMap.set(submission.examId, {
            id: submission.examId,
            title: submission.examTitle
          });
        }
      });
      uniqueExams.value = Array.from(examMap.values());
    };

    // 根据关键词进行筛选
    const searchSubmission = () => {
      applyFilters();
    };
    
    /**
     * 应用所有筛选条件
     */
    const applyFilters = () => {
      let filtered = [...examSubmissions.value];
      
      // 关键词筛选
      if (searchKeyword.value.trim() !== '') {
        filtered = filtered.filter(submission => {
          return submission.userName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
              submission.examTitle.toLowerCase().includes(searchKeyword.value.toLowerCase())
        });
      }
      
      // 阅卷状态筛选
      if (filters.value.gradingStatus !== '') {
        filtered = filtered.filter(submission => {
          if (filters.value.gradingStatus === 'graded') {
            return submission.isGraded === true;
          } else if (filters.value.gradingStatus === 'ungraded') {
            return submission.isGraded === false;
          }
          return true;
        });
      }
      
      // 考试筛选
      if (filters.value.examId !== '') {
        filtered = filtered.filter(submission => {
          return submission.examId === filters.value.examId;
        });
      }
      
      // 得分区间筛选
      if (filters.value.minScore !== null || filters.value.maxScore !== null) {
        filtered = filtered.filter(submission => {
          const score = submission.submissionScore || 0;
          let inRange = true;
          
          if (filters.value.minScore !== null) {
            inRange = inRange && score >= filters.value.minScore;
          }
          
          if (filters.value.maxScore !== null) {
            inRange = inRange && score <= filters.value.maxScore;
          }
          
          return inRange;
        });
      }
      
      filteredSubmissions.value = filtered;
    };
    
    /**
     * 重置所有筛选条件
     */
    const resetFilters = () => {
      filters.value = {
        gradingStatus: '',
        examId: '',
        minScore: null,
        maxScore: null
      };
      searchKeyword.value = '';
      applyFilters();
    };

    /**
     * 删除某个考试提交记录
     * @param {number} submissionId - 提交记录ID
     */
    const deleteSubmissions = async (submissionId) => {
      try {
        await ElMessageBox.confirm(
            '确定要删除该提交记录吗？删除后无法恢复！',
            '删除确认',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        );
        await api.delete(`/exam-submissions/${submissionId}`);
        ElMessage.success('删除成功');
        await getAllSubmissions(); // 删除成功后重新获取考试列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败', error);
          ElMessage.error('删除失败');
        }
      }
    };


    // 开始阅卷或查看阅卷详情
    const startGrading = async (submissionId) => {
      try {
        const response = await api.get(`/grading/submission/${submissionId}`);
        console.log('API响应数据:', response.data); // 调试信息

        // 处理不同的响应结构
        const data = response.data.data || response.data;

        if (!data) {
          throw new Error('API响应数据为空');
        }

        console.log('处理后的数据:', data); // 调试信息

        gradingDetails.value = {
          submissionId: submissionId,
          studentName: data.studentName || '未知学生',
          examTitle: data.examTitle || '未知考试',
          submitTime: data.submitTime || '',
          totalScore: data.totalScore || 0,
          currentScore: data.currentScore || 0,
          isGraded: data.isGraded || false,
          questions: (data.questions || []).map(question => {
            // 解析选项字段，如果是JSON字符串则解析为对象
            let parsedOptions = null;
            if (question.options) {
              try {
                if (typeof question.options === 'string') {
                  const optionsObj = JSON.parse(question.options);
                  // 将对象转换为数组格式 [{"A": "选项1"} -> ["选项1", "选项2"]]
                  parsedOptions = Object.values(optionsObj);
                } else {
                  parsedOptions = question.options;
                }
              } catch (e) {
                console.error('解析选项失败:', e, question.options);
                parsedOptions = null;
              }
            }

            const processedQuestion = reactive({
              questionId: question.questionId,
              questionContent: question.questionContent || '',
              questionType: question.questionType || '',
              maxScore: question.maxScore || 0,
              currentScore: question.currentScore || 0,
              correctAnswer: question.correctAnswer || '',
              studentAnswer: question.studentAnswer || '',
              options: parsedOptions, // 解析后的选项数组
              aiGradingResult: null,
              showAiButton: false,
              aiLoading: false
            });

            // 自动比对客观题答案
            if (isObjectiveQuestion(processedQuestion.questionType)) {
              if (processedQuestion.studentAnswer && processedQuestion.correctAnswer) {
                const studentAnswer = processedQuestion.studentAnswer.toString().trim().toUpperCase();
                const correctAnswer = processedQuestion.correctAnswer.toString().trim().toUpperCase();

                if (studentAnswer === correctAnswer) {
                  processedQuestion.currentScore = processedQuestion.maxScore;
                }
              }
            }

            return processedQuestion;
          })
        };

        gradingVisible.value = true;
        isEditing.value = false;
      } catch (error) {
        console.error('获取阅卷详情失败:', error);
        ElMessage.error('获取阅卷详情失败');
      }
    };

    // 查看阅卷详情
    const viewGradingDetails = async (submissionId) => {
      await startGrading(submissionId);
    };

    // 关闭阅卷弹窗
    const closeGrading = () => {
      gradingVisible.value = false;
      gradingDetails.value = null;
      isEditing.value = false;
    };

    // 更新题目分数
    const updateQuestionScore = async (questionId, score) => {
      if (!gradingDetails.value) return;

      try {
        await api.put(`/grading/submission/${gradingDetails.value.submissionId}/score`, {
          questionId: questionId,
          score: score
        });

        // 更新当前总分
        gradingDetails.value.currentScore = gradingDetails.value.questions.reduce((total, q) => total + (q.currentScore || 0), 0);

        ElMessage.success('分数更新成功');
      } catch (error) {
        console.error('更新分数失败:', error);
        ElMessage.error('更新分数失败');
      }
    };

    // 完成阅卷
    const completeGrading = async () => {
      if (!gradingDetails.value) return;

      try {
        // 先保存所有题目分数
        console.log('完成阅卷前保存分数，提交ID:', gradingDetails.value.submissionId);

        for (const question of gradingDetails.value.questions) {
          const requestData = {
            questionId: question.questionId || question.id,
            score: question.currentScore || 0
          };
          console.log('保存题目分数:', requestData);

          await api.put(`/grading/submission/${gradingDetails.value.submissionId}/score`, requestData);
        }

        // 然后标记阅卷完成
        await api.put(`/grading/submission/${gradingDetails.value.submissionId}/complete`);
        gradingDetails.value.isGraded = true;
        ElMessage.success('阅卷完成');
        await getAllSubmissions(); // 刷新列表
      } catch (error) {
        console.error('完成阅卷失败:', error);
        ElMessage.error('完成阅卷失败: ' + (error.response?.data?.message || error.message));
      }
    };

    // 启用编辑模式
    const enableEditing = () => {
      isEditing.value = true;
    };

    // 保存编辑
    const saveEditing = async () => {
      if (!gradingDetails.value) return;

      try {
        console.log('开始保存分数，提交ID:', gradingDetails.value.submissionId);
        console.log('题目数据:', gradingDetails.value.questions);

        // 逐个更新每个题目分数
        for (const question of gradingDetails.value.questions) {
          const requestData = {
            questionId: question.questionId || question.id,
            score: question.currentScore || 0
          };
          console.log('发送分数更新请求:', requestData);

          await api.put(`/grading/submission/${gradingDetails.value.submissionId}/score`, requestData);
          console.log('题目分数更新成功:', question.questionId || question.id);
        }

        console.log('所有分数更新完成');

        // 更新当前总分
        gradingDetails.value.currentScore = gradingDetails.value.questions.reduce(
            (total, q) => total + (q.currentScore || 0), 0
        );

        isEditing.value = false;
        ElMessage.success('分数修改已保存');
      } catch (error) {
        console.error('保存分数失败:', error);
        ElMessage.error('保存分数失败: ' + (error.response?.data?.message || error.message));
      }
    };


    // 获取题目类型文本
    const getQuestionTypeText = (type) => {
      const typeMap = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'TRUE_FALSE': '判断题',
        'SHORT_ANSWER': '简答题',
        'ESSAY': '论述题',
        'single': '单选题',
        'multiple': '多选题',
        'true_false': '判断题',
        'short_answer': '简答题',
        'essay': '论述题',
        'single_choice': '单选题',
        'multiple_choice': '多选题'
      };
      return typeMap[type] || type;
    };

    // 获取答案样式类
    const getAnswerClass = (type) => {
      return {
        'objective-answer': ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'single', 'multiple', 'true_false', 'single_choice', 'multiple_choice'].includes(type),
        'subjective-answer': ['SHORT_ANSWER', 'ESSAY', 'short_answer', 'essay'].includes(type)
      };
    };

    // 判断是否为主观题
    const isSubjectiveQuestion = (type) => {
      return ['SHORT_ANSWER', 'ESSAY', 'short_answer', 'essay'].includes(type);
    };

    // 判断是否为客观题
    const isObjectiveQuestion = (type) => {
      return ['SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TRUE_FALSE', 'single', 'multiple', 'true_false', 'single_choice', 'multiple_choice'].includes(type);
    };

    // AI按钮现在直接显示，不需要悬浮逻辑

    // 打开AI辅助评分弹窗
    const openAiGradingDialog = (question) => {
      currentAiQuestion.value = question;
      aiGradingForm.value = {
        suggestedScore: '',
        gradingReason: ''
      };
      aiStreamingContent.value = '';
      aiGradingDialogVisible.value = true;
    };

    // 关闭AI辅助评分弹窗
    const closeAiGradingDialog = () => {
      aiGradingDialogVisible.value = false;
      currentAiQuestion.value = null;
      aiGradingForm.value = {
        suggestedScore: '',
        gradingReason: ''
      };
      aiStreamingContent.value = '';
    };

    // 开始AI评分（流式）
    const startAiGrading = async () => {
      if (!currentAiQuestion.value || !gradingDetails.value) return;

      aiGradingLoading.value = true;
      aiStreamingContent.value = '';
      aiGradingForm.value = {
        suggestedScore: '',
        gradingReason: ''
      };

      try {
        const requestData = {
          subject: gradingDetails.value.examTitle,
          question: currentAiQuestion.value.questionContent,
          referenceAnswer: currentAiQuestion.value.correctAnswer,
          totalScore: currentAiQuestion.value.maxScore,
          studentAnswer: currentAiQuestion.value.studentAnswer
        };

        // 使用fetch进行流式请求
        const response = await fetch('http://localhost:9999/api/ai-grading/grade/stream', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
          },
          body: JSON.stringify(requestData)
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        let accumulatedContent = ''; // 累积所有内容

        while (true) {
          const {done, value} = await reader.read();

          if (done) break;

          buffer += decoder.decode(value, {stream: true});
          const lines = buffer.split('\n');
          buffer = lines.pop() || ''; // 保留最后一个不完整的行

          for (const line of lines) {
            if (line.startsWith('data:')) {
              const data = line.slice(5); // 移除'data:'前缀
              if (data === '[DONE]') {
                break;
              }

              // 累积所有内容（包括空格、换行等）
              accumulatedContent += data;

              // 实时显示累积的内容
              aiStreamingContent.value = accumulatedContent;
            }
          }
        }

        // 流式传输完成后，解析最终的JSON
        try {
          // 移除markdown包裹
          let cleanData = accumulatedContent.replace(/```json|```/g, '').trim();

          // 尝试解析JSON
          if (cleanData.startsWith('{') && cleanData.endsWith('}')) {
            const parsed = JSON.parse(cleanData);

            // 解析AI返回的评分结果
            if (parsed.得分) {
              aiGradingForm.value.suggestedScore = parsed.得分.split('/')[0];
            }
            if (parsed.评分依据) {
              aiGradingForm.value.gradingReason = parsed.评分依据;
            }

            console.log('AI评分解析成功:', parsed);
          }
        } catch (e) {
          console.error('最终JSON解析失败:', e);
          console.log('累积的内容:', accumulatedContent);
        }

        // 流式传输完成后清空显示内容
        setTimeout(() => {
          aiStreamingContent.value = '';
        }, 2000);

      } catch (error) {
        console.error('AI流式评分失败:', error);
        ElMessage.error('AI评分失败: ' + error.message);
      } finally {
        aiGradingLoading.value = false;
      }
    };

    // 应用AI建议分数
    const applyAiScore = () => {
      if (currentAiQuestion.value && aiGradingForm.value.suggestedScore) {
        const score = parseInt(aiGradingForm.value.suggestedScore);
        if (!isNaN(score)) {
          currentAiQuestion.value.currentScore = Math.min(score, currentAiQuestion.value.maxScore);
          closeAiGradingDialog();
        }
      }
    };

    /**
     * 根据答案选项获取对应的选项文本
     * @param {string} answer - 答案（如 'A', 'B', 'C' 等）
     * @param {Array} options - 选项数组
     * @returns {string} 选项文本
     */
    const getOptionText = (answer, options) => {
      if (!answer || !options || options.length === 0) {
        return '';
      }

      // 处理单个字母答案（如 A, B, C, D）
      if (answer.length === 1 && /[A-Z]/i.test(answer)) {
        const index = answer.toUpperCase().charCodeAt(0) - 65;
        return options[index] || '';
      }

      // 处理多选答案（如 AB, AC, BCD）
      if (answer.length > 1 && /^[A-Z]+$/i.test(answer)) {
        const letters = answer.toUpperCase().split('');
        const texts = letters.map(letter => {
          const index = letter.charCodeAt(0) - 65;
          return options[index] || '';
        }).filter(text => text);
        return texts.join(', ');
      }

      // 如果答案本身就是选项文本，直接返回
      return answer;
    };

    /**
     * 格式化日期时间为中国北京时间
     * @param {string} dateTimeString - ISO格式的日期时间字符串
     * @returns {string} 格式化后的日期时间字符串 (YYYY-MM-DD HH:mm:ss)
     */
    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return '';

      try {
        const date = new Date(dateTimeString);
        // 转换为北京时间 (UTC+8)
        const beijingTime = new Date(date.getTime() + (8 * 60 * 60 * 1000));

        const year = beijingTime.getFullYear();
        const month = String(beijingTime.getMonth() + 1).padStart(2, '0');
        const day = String(beijingTime.getDate()).padStart(2, '0');
        const hours = String(beijingTime.getHours()).padStart(2, '0');
        const minutes = String(beijingTime.getMinutes()).padStart(2, '0');
        const seconds = String(beijingTime.getSeconds()).padStart(2, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      } catch (error) {
        console.error('日期格式化失败:', error);
        return dateTimeString;
      }
    };

    // 初始化时获取所有提交记录
    onMounted(() => {
      getAllSubmissions();
    });

    return {
      searchKeyword,
      filteredSubmissions,
      examSubmissions,
      deleteSubmissions,
      getAllSubmissions,
      searchSubmission,
      gradingVisible,
      gradingDetails,
      isEditing,
      aiGradingLoading,
      startGrading,
      viewGradingDetails,
      closeGrading,
      updateQuestionScore,
      completeGrading,
      enableEditing,
      saveEditing,
      getQuestionTypeText,
      getAnswerClass,
      isSubjectiveQuestion,
      isObjectiveQuestion,
      getOptionText,
      formatDateTime,
      // 筛选相关
      filters,
      uniqueExams,
      applyFilters,
      resetFilters,
      generateUniqueExams,
      // AI辅助评分相关
      aiGradingDialogVisible,
      currentAiQuestion,
      aiGradingForm,
      aiStreamingContent,
      openAiGradingDialog,
      closeAiGradingDialog,
      startAiGrading,
      applyAiScore
    };
  },
};
</script>

<style scoped>
/* 页面容器 */
.submission-manager-container {
  padding: 0;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.title-icon {
  font-size: 28px;
  color: #409EFF;
}

.page-description {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 24px;
}

.search-card {
  border: 1px solid #e9ecef;
  border-radius: 12px;
}

.search-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.search-left {
  display: flex;
  gap: 16px;
  flex: 1;
}

.search-input {
  width: 300px;
}

.search-right {
  display: flex;
  gap: 12px;
}

/* 筛选区域 */
.filter-section {
  border-top: 1px solid #e9ecef;
  padding-top: 20px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
  font-weight: 500;
}

.filter-select {
  width: 150px;
}

.score-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-input {
  width: 80px;
}

.score-separator {
  color: #909399;
  font-weight: 500;
}

/* 表格区域 */
.table-section {
  margin-bottom: 24px;
}

.table-card {
  border: 1px solid #e9ecef;
  border-radius: 12px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.submission-table {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.submission-table .el-table__row) {
  height: 56px;
}

:deep(.submission-table .el-table__cell) {
  padding: 12px 0;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

.action-buttons .el-button {
  min-width: 60px;
  height: 28px;
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-weight: 500;
}

.action-buttons .el-button .el-icon {
  margin-right: 4px;
  font-size: 12px;
}

/* 阅卷弹窗样式 */
.grading-question {
  margin-bottom: 20px;
}

.grading-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.score-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  padding-right: 56px;
}

.score-input {
  display: flex;
  align-items: center;
}

.score-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.score-label {
  margin-left: 8px;
  color: #606266;
  font-weight: 500;
}

.ai-button-container {
  display: flex;
  justify-content: flex-end;
  margin-right: -24px;
}

.ai-grading-button {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  transition: all 0.3s ease;
}

.ai-grading-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.ai-grading-button .el-icon {
  margin-right: 4px;
}

.question-content {
  padding: 8px 0;
}

.question-text {
  margin-bottom: 16px;
}

.question-detail {
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.inline-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.inline-option {
  padding: 4px 8px;
  background-color: #ecf5ff;
  border-radius: 4px;
  color: #409EFF;
}

.answer-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.correct-answer,
.student-answer {
  padding: 8px;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.answer-content {
  padding: 8px;
  border-radius: 4px;
  background-color: #fff;
  border: 1px solid #e9ecef;
}

.option-detail {
  color: #6c757d;
  font-size: 0.9em;
}

.grading-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

/* 弹窗样式 */
:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e9ecef;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-content {
    flex-direction: column;
    align-items: stretch;
  }

  .search-left {
    flex-direction: column;
    margin-bottom: 16px;
  }

  .search-input {
    width: 100%;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .filter-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .filter-select {
    width: 100%;
  }

  .score-range {
    width: 100%;
    justify-content: space-between;
  }

  .score-input {
    flex: 1;
    max-width: 120px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .question-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>


