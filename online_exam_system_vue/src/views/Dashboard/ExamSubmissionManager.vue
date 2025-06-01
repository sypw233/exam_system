<template>
  <el-header>
    <el-input
        v-model="searchKeyword"
        placeholder="输入关键词进行搜索"
        @input="searchSubmission"
        style="width: 200px; margin-right: 10px;"
    />
  </el-header>
  <el-main>
    <el-table
        :data="filteredSubmissions"
        style="width: 100%"
    >
      <el-table-column prop="userName" label="用户名"></el-table-column>
      <el-table-column prop="examId" label="考试ID"></el-table-column>
      <el-table-column prop="examTitle" label="考试"></el-table-column>
      <el-table-column prop="examTotalScore" label="总分"></el-table-column>
      <el-table-column prop="submissionScore" label="得分"></el-table-column>
      <el-table-column label="阅卷状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isGraded ? 'success' : 'warning'">
            {{ row.isGraded ? '已阅卷' : '待阅卷' }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button 
            v-if="!row.isGraded" 
            type="warning" 
            size="small" 
            @click="startGrading(row.id)"
          >
            开始阅卷
          </el-button>
          <el-button 
            v-else 
            type="success" 
            size="small" 
            @click="viewGradingDetails(row.id)"
          >
            查看阅卷详情
          </el-button>
          <el-button type="danger" size="small" @click="deleteSubmissions(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-main>



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
            <div class="score-input">
              <div class="score-input-container" @mouseenter="showAiButton(question)" @mouseleave="hideAiButton(question)">
                <el-input-number
                  v-model="question.currentScore"
                  :min="0"
                  :max="question.maxScore"
                  :precision="0"
                  size="small"
                  :disabled="gradingDetails.isGraded && !isEditing"
                />
                <span class="score-label">分</span>
                <!-- AI辅助评分悬浮按钮 -->
                <el-button
                  v-if="isSubjectiveQuestion(question.questionType) && question.showAiButton"
                  class="ai-float-button"
                  type="primary"
                  size="small"
                  circle
                  @click="openAiGradingDialog(question)"
                  :loading="question.aiLoading"
                >
                  <el-icon><Service /></el-icon>
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
        <el-button v-if="gradingDetails.isGraded && !isEditing" @click="enableEditing" type="warning">编辑分数</el-button>
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
</template>

<script>
import {onMounted, ref, reactive} from 'vue';
import api from "@/api/axios.js";
import { Service } from '@element-plus/icons-vue';
import axios from "@/api/axios.js";

export default {
  name: 'ExamSubmission',
  components: {
    Service
  },

  setup() {
    const examSubmissions = ref([]);
    const searchKeyword = ref('');
    const filteredSubmissions = ref([]);

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
        }
      } catch (error) {
        console.error('Error fetching exam submissions:', error);
      }
    };

    // 根据关键词进行筛选
    const searchSubmission = () => {
      // console.log("筛选keyword:" + searchKeyword.value.trim())
      if (searchKeyword.value.trim() === '') {
        filteredSubmissions.value = examSubmissions.value;
        // console.log("筛选内容为空")
      } else {
        filteredSubmissions.value = examSubmissions.value.filter(submission => {
          return submission.userName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
              submission.examTitle.toLowerCase().includes(searchKeyword.value.toLowerCase())
        });
      }
    };

    // 删除某个考试提交记录
    const deleteSubmissions = async (submissionId) => {
      try {
        await api.delete(`/exam-submissions/${submissionId}`);
        alert('删除成功');
        await getAllSubmissions(); // 删除成功后重新获取考试列表
      } catch (error) {
        console.error('删除失败', error);
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
          alert('获取阅卷详情失败');
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
          
          alert('分数更新成功');
        } catch (error) {
          console.error('更新分数失败:', error);
          alert('更新分数失败');
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
          alert('阅卷完成');
          await getAllSubmissions(); // 刷新列表
        } catch (error) {
          console.error('完成阅卷失败:', error);
          alert('完成阅卷失败: ' + (error.response?.data?.message || error.message));
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
          alert('分数修改已保存');
        } catch (error) {
          console.error('保存分数失败:', error);
          alert('保存分数失败: ' + (error.response?.data?.message || error.message));
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
    
    // 显示AI按钮
    const showAiButton = (question) => {
      if (isSubjectiveQuestion(question.questionType)) {
        question.showAiButton = true;
      }
    };
    
    // 隐藏AI按钮（延迟0.5秒）
    const hideAiButton = (question) => {
      setTimeout(() => {
        question.showAiButton = false;
      }, 500);
    };
    
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
          const { done, value } = await reader.read();
          
          if (done) break;
          
          buffer += decoder.decode(value, { stream: true });
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
        alert('AI评分失败: ' + error.message);
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
      // AI辅助评分相关
      aiGradingDialogVisible,
      currentAiQuestion,
      aiGradingForm,
      aiStreamingContent,
      showAiButton,
      hideAiButton,
      openAiGradingDialog,
      closeAiGradingDialog,
      startAiGrading,
      applyAiScore
    };
  },
};
</script>

<style scoped>
.el-button {
  border-radius: 10px;
}

.search-container {
  margin-bottom: 20px;
}

/* 试卷详情样式 */
.question-item {
  margin-bottom: 16px;
}

.question-card {
  margin-bottom: 12px;
}

.question-card h4 {
  color: #409eff;
  margin-bottom: 12px;
}

.question-card p {
  margin: 8px 0;
  line-height: 1.6;
}

.question-card ul {
  margin: 8px 0;
  padding-left: 20px;
}

.question-card li {
  margin: 4px 0;
}

/* 阅卷界面样式 */
.grading-question {
  margin-bottom: 20px;
}

.grading-card {
  margin-bottom: 16px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.question-header h4 {
  color: #409eff;
  margin: 0;
}

.score-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-input-container {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
}

.score-label {
  font-size: 14px;
  color: #606266;
}

.ai-float-button {
  position: absolute;
  left: 50%;
  top: 100%;
  transform: translateX(-50%);
  margin-top: 8px;
  width: 28px;
  height: 28px;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.ai-float-button:hover {
  transform: translateX(-50%) scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 选项显示样式 */
.question-options {
  margin: 12px 0;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.options-list {
  margin: 8px 0 0 0;
  padding-left: 0;
  list-style: none;
}

.option-item {
  margin: 6px 0;
  padding: 4px 8px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-size: 14px;
}

.option-detail {
  color: #909399;
  font-size: 12px;
  margin-left: 4px;
}

.question-content {
  padding: 0 16px;
}

.question-content p {
  margin: 12px 0;
  line-height: 1.6;
}

/* 题目文本样式 */
.question-text {
  margin-bottom: 16px;
}

.question-detail {
  margin-left: 16px;
}

/* 内联选项样式 */
.inline-options {
  margin-top: 12px;
  padding-left: 16px;
}

.inline-option {
  margin: 8px 0;
  padding: 6px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #e4e7ed;
  font-size: 14px;
  line-height: 1.5;
}

.inline-option:hover {
  background-color: #f0f2f5;
  border-left-color: #409eff;
}

.answer-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin: 16px 0;
}

.correct-answer,
.student-answer {
  padding: 12px;
  border-radius: 6px;
  background-color: #f8f9fa;
}

.correct-answer {
  border-left: 4px solid #67c23a;
}

.student-answer {
  border-left: 4px solid #409eff;
}

.answer-content {
  margin-top: 8px;
  padding: 8px;
  background-color: white;
  border-radius: 4px;
  min-height: 40px;
  word-wrap: break-word;
}

.objective-answer {
  font-weight: 500;
}

.subjective-answer {
  line-height: 1.8;
  white-space: pre-wrap;
}

.ai-grading {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.ai-result {
  margin-top: 12px;
}

.grading-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  text-align: right;
}

.grading-actions .el-button {
  margin-left: 12px;
}

/* 答题详情样式 */
.student-answer {
  background-color: #e1f3ff;
  border-left: 3px solid #409eff;
  padding-left: 8px;
}

.correct-answer {
  background-color: #f0f9ff;
  border-left: 3px solid #67c23a;
  padding-left: 8px;
}

.correct-text {
  color: #67c23a;
  font-weight: 500;
}

.wrong-text {
  color: #f56c6c;
  font-weight: 500;
}

/* AI辅助评分弹窗样式 */
.ai-grading-result {
  margin-top: 16px;
}

.streaming-content {
  margin-top: 16px;
}

.streaming-text {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .answer-section {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .question-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .grading-actions {
    text-align: center;
  }
  
  .grading-actions .el-button {
    margin: 4px;
  }
  
  .ai-float-button {
    left: 50%;
    transform: translateX(-50%);
    margin-top: 6px;
    width: 24px;
    height: 24px;
  }
  
  .dialog-footer {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
