<template>
  <div class="question-manager-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <el-icon class="title-icon"><QuestionFilled /></el-icon>
          题目管理
        </h2>
        <p class="page-description">管理系统中的所有题目，包括添加、搜索和删除题目</p>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="hover">
        <div class="search-content">
          <div class="search-left">
            <el-input 
              v-model="keyword" 
              placeholder="请输入关键词搜索题目" 
              @keyup.enter="applyFilters"
              class="search-input"
              clearable
            >
              <template #prefix>
                <i class="el-icon-search"></i>
              </template>
            </el-input>
          </div>
          <div class="search-right">
            <el-button type="primary" @click="applyFilters">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
            <el-button type="success" @click="openAddDialog">添加题目</el-button>
          </div>
        </div>
        
        <!-- 筛选区域 -->
        <div class="filter-section">
          <div class="filter-row">
            <div class="filter-item">
              <label class="filter-label">题目考点：</label>
              <el-select v-model="filters.category" placeholder="请选择考点" clearable class="filter-select">
                <el-option
                  v-for="category in uniqueCategories"
                  :key="category"
                  :label="category"
                  :value="category"
                />
              </el-select>
            </div>
            
            <div class="filter-item">
              <label class="filter-label">题型：</label>
              <el-select v-model="filters.type" placeholder="请选择题型" clearable class="filter-select">
                <el-option label="单选题" value="single" />
                <el-option label="多选题" value="multiple" />
                <el-option label="判断题" value="true_false" />
                <el-option label="填空题" value="fill_blank" />
                <el-option label="简答题" value="short_answer" />
                <el-option label="论述题" value="essay" />
              </el-select>
            </div>
            
            <div class="filter-item">
              <label class="filter-label">难度：</label>
              <el-select v-model="filters.difficulty" placeholder="请选择难度" clearable class="filter-select">
                <el-option label="简单" value="easy" />
                <el-option label="中等" value="medium" />
                <el-option label="困难" value="hard" />
              </el-select>
            </div>
            
            <div class="filter-item">
              <el-button type="info" @click="resetFilters">重置筛选</el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <el-card class="table-card" shadow="hover">
        <div class="table-header">
          <h2 class="table-title">题目列表 (共 {{ filteredQuestions.length }} 题)</h2>
        </div>
        <el-table 
          :data="filteredQuestions" 
          class="question-table"
          border
          stripe
          style="width: 100%"
        >
          <el-table-column prop="category" label="题目考点" min-width="120"></el-table-column>
          <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column label="题型" min-width="100">
            <template #default="scope">
              <el-tag :type="getQuestionTypeTag(scope.row.type)" effect="light">
                {{ formatQuestionType(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="难度" min-width="100">
            <template #default="scope">
              <el-tag :type="getDifficultyTag(scope.row.difficulty)" effect="light">
                {{ formatDifficulty(scope.row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-button @click="deleteQuestion(scope.row.id)" type="danger" size="small">
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 添加题目弹窗 -->
    <el-dialog v-model="dialogFormVisible" title="添加题目" width="600" class="question-dialog">
      <el-form :model="newQuestion" ref="form" label-width="120px">
        <!-- 题目考点 -->
        <el-form-item label="题目考点" :rules="[{ required: true, message: '请输入题目考点', trigger: 'blur' }]">
          <el-input v-model="newQuestion.category" autocomplete="off"/>
        </el-form-item>

        <!-- 题目内容 -->
        <el-form-item label="题目内容" :rules="[{ required: true, message: '请输入题目内容', trigger: 'blur' }]">
          <el-input v-model="newQuestion.content" type="textarea" autocomplete="off"/>
        </el-form-item>

        <!-- 题型 -->
        <el-form-item label="题型" :rules="[{ required: true, message: '请选择题型', trigger: 'blur' }]">
          <el-select v-model="newQuestion.type" placeholder="请选择题型" @change="onTypeChange">
            <el-option label="单选题" value="single"></el-option>
            <el-option label="判断题" value="true_false"></el-option>
            <el-option label="多选题" value="multiple"></el-option>
            <el-option label="填空题" value="fill_blank"></el-option>
            <el-option label="简答题" value="short_answer"></el-option>
            <el-option label="论述题" value="essay"></el-option>
          </el-select>
        </el-form-item>

        <!-- 难度 -->
        <el-form-item label="难度" :rules="[{ required: true, message: '请选择难度', trigger: 'blur' }]">
          <el-select v-model="newQuestion.difficulty" placeholder="请选择难度">
            <el-option label="简单" value="easy"></el-option>
            <el-option label="中等" value="medium"></el-option>
            <el-option label="困难" value="hard"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 单选题/多选题选项管理 -->
        <div v-if="['single', 'multiple'].includes(newQuestion.type)">
          <el-form-item label="选项管理">
            <div v-for="(option, index) in questionOptions" :key="index" class="option-item">
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <el-input 
                v-model="option.text" 
                placeholder="请输入选项内容" 
                class="option-input"
              />
              <el-checkbox 
                v-model="option.isCorrect" 
                :disabled="newQuestion.type === 'single' && option.isCorrect === false && hasCorrectAnswer"
                @change="onCorrectAnswerChange(index)"
                class="option-checkbox"
              >
                正确答案
              </el-checkbox>
              <el-button 
                type="danger" 
                size="small" 
                @click="removeOption(index)"
                :disabled="questionOptions.length <= 2"
                class="option-delete"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" @click="addOption" class="add-option-btn">添加选项</el-button>
          </el-form-item>
        </div>
        
        <!-- 判断题选项 -->
        <div v-if="newQuestion.type === 'true_false'">
          <el-form-item label="选项">
            <div class="true-false-options">
              <el-radio-group v-model="trueFalseAnswer">
                <el-radio label="A">A. 正确</el-radio>
                <el-radio label="B">B. 错误</el-radio>
              </el-radio-group>
            </div>
          </el-form-item>
        </div>
        
        <!-- 填空题参考答案 -->
        <el-form-item v-if="newQuestion.type === 'fill_blank'" label="参考答案" :rules="[{ required: true, message: '请输入参考答案', trigger: 'blur' }]">
          <el-input 
            v-model="newQuestion.answer" 
            type="textarea"
            :rows="2"
            placeholder="请输入填空题参考答案，多个空用分号分隔"
            autocomplete="off"
          />
        </el-form-item>
        
        <!-- 简答题/论述题参考答案 -->
        <el-form-item v-if="['short_answer', 'essay'].includes(newQuestion.type)" label="参考答案" :rules="[{ required: true, message: '请输入参考答案', trigger: 'blur' }]">
          <el-input 
            v-model="newQuestion.answer" 
            type="textarea"
            :rows="4"
            :placeholder="newQuestion.type === 'short_answer' ? '请输入简答题参考答案' : '请输入论述题参考答案'"
            autocomplete="off"
          />
        </el-form-item>
      </el-form>

      <!-- 弹窗底部按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="addQuestion">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {onMounted, reactive, ref, computed} from 'vue';
import api from '@/api/axios';
import { ElMessage, ElMessageBox } from "element-plus";
import {QuestionFilled} from "@element-plus/icons-vue"; // 引入配置好的 axios 实例

export default {
  name: 'QuestionManager',
  components: {QuestionFilled},
  setup() {
    const keyword = ref('');
    const dialogFormVisible = ref(false);
    const allQuestions = ref([]);
    const filteredQuestions = ref([]);
    
    // 筛选条件
    const filters = ref({
      category: '',
      type: '',
      difficulty: ''
    });
    
    // 唯一的考点列表
    const uniqueCategories = ref([]);

    const newQuestion = reactive({
      category: '',
      content: '',
      type: '',
      answer: '',
      difficulty: '',
      options: '',
      creatorId: localStorage.getItem("id")
    });
    
    // 选择题选项管理
    const questionOptions = ref([
      { text: '', isCorrect: false },
      { text: '', isCorrect: false }
    ]);
    
    // 判断题答案
    const trueFalseAnswer = ref('');
    
    // 计算是否已有正确答案（用于单选题限制）
    const hasCorrectAnswer = computed(() => {
      return questionOptions.value.some(option => option.isCorrect);
    });
    
    /**
     * 根据题目类型返回对应的标签类型
     * @param {string} type - 题目类型
     * @returns {string} 对应的标签类型
     */
    const getQuestionTypeTag = (type) => {
      const typeMap = {
        'single': 'primary',
        'multiple': 'success',
        'true_false': 'info',
        'fill_blank': 'warning',
        'short_answer': 'danger',
        'essay': ''
      };
      return typeMap[type] || '';
    };
    
    /**
     * 根据难度返回对应的标签类型
     * @param {string} difficulty - 难度
     * @returns {string} 对应的标签类型
     */
    const getDifficultyTag = (difficulty) => {
      const difficultyMap = {
        'easy': 'success',
        'medium': 'warning',
        'hard': 'danger'
      };
      return difficultyMap[difficulty] || '';  
    };

    /**
     * 获取所有题目信息
     */
    const getAllQuestions = async () => {
      try {
        const res = await api.get('/questions');
        allQuestions.value = res.data.data || [];
        generateUniqueCategories();
        applyFilters();
      } catch (error) {
        console.error('获取题目失败', error);
        ElMessage.error('获取题目列表失败');
      }
    };
    
    /**
     * 生成唯一的考点列表
     */
    const generateUniqueCategories = () => {
      const categories = new Set();
      allQuestions.value.forEach(question => {
        if (question.category && question.category.trim()) {
          categories.add(question.category.trim());
        }
      });
      uniqueCategories.value = Array.from(categories).sort();
    };
    
    /**
     * 打开添加题目对话框
     */
    const openAddDialog = () => {
      // 重置表单数据
      resetForm();
      dialogFormVisible.value = true;
    };
    
    /**
     * 重置表单数据
     */
    const resetForm = () => {
      Object.keys(newQuestion).forEach(key => {
        if (key !== 'creatorId') {
          newQuestion[key] = '';
        }
      });
      
      // 重置选项数据
      questionOptions.value = [
        { text: '', isCorrect: false },
        { text: '', isCorrect: false }
      ];
      
      // 重置判断题答案
      trueFalseAnswer.value = '';
    };
    
    /**
     * 题型变化处理
     */
    const onTypeChange = () => {
      // 清空答案
      newQuestion.answer = '';
      
      // 根据题型重置相关数据
      if (['single', 'multiple'].includes(newQuestion.type)) {
        // 重置选项
        questionOptions.value = [
          { text: '', isCorrect: false },
          { text: '', isCorrect: false }
        ];
      } else if (newQuestion.type === 'true_false') {
        // 重置判断题答案
        trueFalseAnswer.value = '';
      }
    };
    
    /**
     * 添加选项
     */
    const addOption = () => {
      if (questionOptions.value.length < 6) { // 最多6个选项
        questionOptions.value.push({ text: '', isCorrect: false });
      }
    };
    
    /**
     * 删除选项
     */
    const removeOption = (index) => {
      if (questionOptions.value.length > 2) {
        questionOptions.value.splice(index, 1);
      }
    };
    
    /**
     * 正确答案变化处理
     */
    const onCorrectAnswerChange = (index) => {
      if (newQuestion.type === 'single') {
        // 单选题只能有一个正确答案
        questionOptions.value.forEach((option, i) => {
          if (i !== index) {
            option.isCorrect = false;
          }
        });
      }
    };
    
    /**
     * 格式化题目类型为中文
     * @param {string} type - 题目类型英文标识
     * @returns {string} 中文题目类型
     */
    const formatQuestionType = (type) => {
      const types = {
        'single': '单选题',
        'multiple': '多选题',
        'true_false': '判断题',
        'fill_blank': '填空题',
        'short_answer': '简答题',
        'essay': '论述题'
      };
      return types[type] || type;
    };
    
    /**
     * 格式化难度为中文
     * @param {string} difficulty - 难度英文标识
     * @returns {string} 中文难度
     */
    const formatDifficulty = (difficulty) => {
      const difficulties = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难'
      };
      return difficulties[difficulty] || difficulty;
    };

    /**
     * 应用筛选条件
     */
    const applyFilters = () => {
      let filtered = allQuestions.value;
      
      // 关键词筛选
      if (keyword.value && keyword.value.trim()) {
        const searchTerm = keyword.value.trim().toLowerCase();
        filtered = filtered.filter(question => 
          question.content.toLowerCase().includes(searchTerm) ||
          question.category.toLowerCase().includes(searchTerm)
        );
      }
      
      // 考点筛选
      if (filters.value.category) {
        filtered = filtered.filter(question => 
          question.category === filters.value.category
        );
      }
      
      // 题型筛选
      if (filters.value.type) {
        filtered = filtered.filter(question => 
          question.type === filters.value.type
        );
      }
      
      // 难度筛选
      if (filters.value.difficulty) {
        filtered = filtered.filter(question => 
          question.difficulty === filters.value.difficulty
        );
      }
      
      filteredQuestions.value = filtered;
    };
    
    /**
     * 重置筛选条件
     */
    const resetFilters = () => {
      keyword.value = '';
      filters.value = {
        category: '',
        type: '',
        difficulty: ''
      };
      applyFilters();
    };


    /**
     * 添加题目
     */
    const addQuestion = async () => {
      try {
        // 根据题型处理选项和答案
        if (['single', 'multiple'].includes(newQuestion.type)) {
          // 单选题和多选题：构建选项JSON数组
          const optionsArray = questionOptions.value.map((option, index) => ({
            key: String.fromCharCode(65 + index),
            value: option.text
          }));
          newQuestion.options = JSON.stringify(optionsArray);
          
          // 构建答案字符串
          const correctAnswers = questionOptions.value
            .map((option, index) => option.isCorrect ? String.fromCharCode(65 + index) : null)
            .filter(answer => answer !== null);
          newQuestion.answer = correctAnswers.join(',');
          
          // 验证是否有正确答案
          if (correctAnswers.length === 0) {
            ElMessage.warning('请至少选择一个正确答案');
            return;
          }
          
          // 验证选项内容是否填写完整
          if (questionOptions.value.some(option => !option.text.trim())) {
            ElMessage.warning('请填写完整的选项内容');
            return;
          }
        } else if (newQuestion.type === 'true_false') {
          // 判断题：构建选项JSON数组
          const trueFalseOptions = [
            { key: 'A', value: '正确' },
            { key: 'B', value: '错误' }
          ];
          newQuestion.options = JSON.stringify(trueFalseOptions);
          newQuestion.answer = trueFalseAnswer.value;
          
          if (!trueFalseAnswer.value) {
            ElMessage.warning('请选择判断题的正确答案');
            return;
          }
        } else if (['fill_blank', 'short_answer', 'essay'].includes(newQuestion.type)) {
          // 填空题、简答题、论述题：设置空的JSON数组
          newQuestion.options = JSON.stringify([]);
          
          if (!newQuestion.answer.trim()) {
            ElMessage.warning('请填写参考答案');
            return;
          }
        }
        
        console.log('提交的题目数据:', newQuestion);
        await api.post('/questions', newQuestion);
        dialogFormVisible.value = false; // 关闭弹窗
        await getAllQuestions(); // 刷新题目列表
        ElMessage.success('题目添加成功');
        
        // 重置表单
        resetForm();
      } catch (error) {
        console.error('添加题目失败', error);
        ElMessage.error(`添加题目失败: ${error.response?.data?.message || error.message}`);
      }
    };

    // 删除题目
    const deleteQuestion = async (id) => {
      ElMessageBox.confirm('确定删除?')
          .then(async () => {
            await api.delete(`/questions/${id}`);
            await getAllQuestions(); // 删除后刷新题目列表
            ElMessage.success('题目删除成功');
          })
          .catch(() => {
            // 用户取消删除
          });
    };
    


    // 初始化时获取题目信息
    onMounted(() => {
      getAllQuestions();
    });

    return {
        keyword,
        allQuestions,
        filteredQuestions,
        filters,
        uniqueCategories,
        dialogFormVisible,
        newQuestion,
        questionOptions,
        trueFalseAnswer,
        hasCorrectAnswer,
        getAllQuestions,
        generateUniqueCategories,
        applyFilters,
        resetFilters,
        openAddDialog,
        onTypeChange,
        addOption,
        removeOption,
        onCorrectAnswerChange,
        addQuestion,
        deleteQuestion,
        formatQuestionType,
        formatDifficulty,
        getQuestionTypeTag,
        getDifficultyTag,
      };
  },
};
</script>

<style scoped>
/* 页面容器 */
.question-manager-container {
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
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
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

.question-table {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.question-table .el-table__row) {
  height: 56px;
}

:deep(.question-table .el-table__cell) {
  padding: 12px 0;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  text-align: right;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

.action-buttons .el-button {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

/* 选项管理 */
.option-item {
  margin-bottom: 10px; 
  display: flex; 
  align-items: center;
}

.option-label {
  margin-right: 10px; 
  font-weight: bold;
}

.option-input {
  flex: 1; 
  margin-right: 10px;
}

.option-checkbox {
  margin-right: 10px;
}

.add-option-btn {
  margin-top: 10px;
}

.true-false-options {
  margin-bottom: 10px;
}

/* 弹窗样式 */
.question-dialog :deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e9ecef;
}

.question-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.el-button {
  border-radius: 10px;
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
  
  .search-right {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .filter-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .option-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .option-input {
    width: 100%;
    margin-right: 0;
  }
}
</style>
