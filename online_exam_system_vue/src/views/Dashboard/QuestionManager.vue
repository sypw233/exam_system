<template>

  <!-- 页头 -->
  <el-header>
    <el-button type="primary" @click="openAddDialog">添加题目</el-button>
    <!-- 添加题目弹窗 -->
    <el-dialog v-model="dialogFormVisible" title="添加题目" width="600">
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
            <div v-for="(option, index) in questionOptions" :key="index" style="margin-bottom: 10px; display: flex; align-items: center;">
              <span style="margin-right: 10px; font-weight: bold;">{{ String.fromCharCode(65 + index) }}.</span>
              <el-input 
                v-model="option.text" 
                placeholder="请输入选项内容" 
                style="flex: 1; margin-right: 10px;"
              />
              <el-checkbox 
                v-model="option.isCorrect" 
                :disabled="newQuestion.type === 'single' && option.isCorrect === false && hasCorrectAnswer"
                @change="onCorrectAnswerChange(index)"
                style="margin-right: 10px;"
              >
                正确答案
              </el-checkbox>
              <el-button 
                type="danger" 
                size="small" 
                @click="removeOption(index)"
                :disabled="questionOptions.length <= 2"
              >
                删除
              </el-button>
            </div>
            <el-button type="primary" @click="addOption" style="margin-top: 10px;">添加选项</el-button>
          </el-form-item>
        </div>
        
        <!-- 判断题选项 -->
        <div v-if="newQuestion.type === 'true_false'">
          <el-form-item label="选项">
            <div style="margin-bottom: 10px;">
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

    <!-- 添加搜索框 -->
    <el-input v-model="keyword" placeholder="输入题目ID进行搜索" @keyup.enter="search"
              style="width: 200px; margin-left: 20px;"></el-input>
    <el-button type="primary" @click="search" style="margin-left: 10px;">搜索</el-button>
    <el-button @click="reset" style="margin-left: 10px;">重置</el-button>
  </el-header>

  <!-- 主体部分 -->
  <el-main>
    <el-table :data="pagination.records" style="width: 100%">
      <el-table-column prop="category" label="题目考点"></el-table-column>
      <el-table-column prop="content" label="内容"></el-table-column>
      <el-table-column label="题型">
        <template #default="scope">
          {{ formatQuestionType(scope.row.type) }}
        </template>
      </el-table-column>
      <el-table-column label="难度">
        <template #default="scope">
          {{ formatDifficulty(scope.row.difficulty) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button @click="deleteQuestion(scope.row.id)" type="danger" size="small">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 40]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
    />
  </el-main>

</template>

<script>
import {onMounted, reactive, ref, computed} from 'vue';
import api from '@/api/axios';
import { ElMessage, ElMessageBox } from "element-plus"; // 引入配置好的 axios 实例

export default {
  name: 'QuestionManager',
  setup() {
    const keyword = ref('');
    const dialogFormVisible = ref(false);
    const pagination = ref({
      current: 1,
      total: null,
      size: 10,
      records: [],
    });

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
     * 获取题目信息
     */
    const getQuestions = async () => {
      try {
        const res = await api.get(`/questions/${pagination.value.current}/${pagination.value.size}`);
        pagination.value.records = res.data.data.records;
        pagination.value.total = res.data.data.total;
        pagination.value.current = res.data.data.current;
        pagination.value.size = res.data.data.size;
      } catch (error) {
        console.error('获取题目失败', error);
      }
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

    // 改变当前页码
    const handleCurrentChange = (val) => {
      pagination.value.current = val;
      getQuestions();
    };

    // 改变每页显示记录数
    const handleSizeChange = (val) => {
      pagination.value.size = val;
      getQuestions();
    };

    /**
     * 搜索题目
     */
    const search = async () => {
      try {
        const res = await api.get(`/questions/${keyword.value}`);
        pagination.value.records = [res.data.data.records];
      } catch (error) {
        ElMessage.error('没找到该题目');
        console.error('搜索题目失败', error);
      }
    };

    // 重置搜索
    const reset = () => {
      keyword.value = '';
      getQuestions();
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
        await getQuestions(); // 刷新题目列表
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
            await getQuestions(); // 删除后刷新题目列表
          })
    };
    


    // 初始化时获取题目信息
    onMounted(() => {
      getQuestions();
    });

    return {
        keyword,
        pagination,
        dialogFormVisible,
        newQuestion,
        questionOptions,
        trueFalseAnswer,
        hasCorrectAnswer,
        handleSizeChange,
        handleCurrentChange,
        search,
        reset,
        openAddDialog,
        onTypeChange,
        addOption,
        removeOption,
        onCorrectAnswerChange,
        addQuestion,
        deleteQuestion,
        formatQuestionType,
        formatDifficulty,
      };
  },
};
</script>

<style scoped>
.el-table {
  margin-bottom: 20px;
}

.el-button {
  border-radius: 10px;
}
</style>
