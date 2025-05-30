<template>

  <!-- 页头 -->
  <el-header>
    <el-button type="primary" @click="dialogFormVisible =true ">添加题目</el-button>
    <!-- 添加题目弹窗 -->
    <el-dialog v-model="dialogFormVisible" title="添加题目" width="500">
      <el-form :model="newQuestion" ref="form" label-width="120px">
        <!-- 课程 -->
        <el-form-item label="题目考点" :rules="[{ required: true, message: '请输入题目考点', trigger: 'blur' }]">
          <el-input v-model="newQuestion.category" autocomplete="off"/>
        </el-form-item>

        <!-- 题目内容 -->
        <el-form-item label="题目内容" :rules="[{ required: true, message: '请输入题目内容', trigger: 'blur' }]">
          <el-input v-model="newQuestion.content" type="textarea" autocomplete="off"/>
        </el-form-item>

        <!-- 题型 -->
        <el-form-item label="题型" :rules="[{ required: true, message: '请选择题型', trigger: 'blur' }]">
          <el-select v-model="newQuestion.type" placeholder="请选择题型">
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
            <!-- 添加更多难度选项 -->
          </el-select>
        </el-form-item>
        <!-- 选项 (仅客观题显示) -->
        <el-form-item label="选项" v-if="['single', 'multiple', 'true_false'].includes(newQuestion.type)" :rules="[{ required: true, message: '请输入选项', trigger: 'blur' }]">
          <el-input v-model="newQuestion.options" type="textarea" :rows="3" placeholder="单选/多选题格式：A. 选项1;B. 选项2;C. 选项3&#10;判断题格式：A. 正确;B. 错误" autocomplete="off"/>
        </el-form-item>
        
        <!-- 填空题选项 -->
        <el-form-item label="选项" v-if="newQuestion.type === 'fill_blank'" :rules="[{ required: true, message: '请输入选项', trigger: 'blur' }]">
          <el-input v-model="newQuestion.options" type="textarea" :rows="2" placeholder="填空题无需填写选项，可留空" autocomplete="off"/>
        </el-form-item>
        
        <!-- 答案 -->
        <el-form-item label="答案" :rules="[{ required: true, message: '请输入答案', trigger: 'blur' }]">
          <el-input 
            v-model="newQuestion.answer" 
            :type="['short_answer', 'essay'].includes(newQuestion.type) ? 'textarea' : 'text'"
            :rows="['short_answer', 'essay'].includes(newQuestion.type) ? 3 : 1"
            :placeholder="getAnswerPlaceholder()"
            autocomplete="off"/>
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
      <el-table-column prop="type" label="题型"></el-table-column>
      <el-table-column prop="difficulty" label="难度"></el-table-column>

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
import {onMounted, reactive, ref} from 'vue';
import api from '@/api/axios';
import {ElMessageBox} from "element-plus"; // 引入配置好的 axios 实例

export default {
  name: 'QuestionManager',
  setup() {
    const keyword = ref('');
    const dialogFormVisible = ref(false)
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

    // 获取题目信息
    const getQuestions = async () => {
      try {
        const res = await api(`/questions/${pagination.value.current}/${pagination.value.size}`);
        pagination.value.records = res.data.data.records;
        pagination.value.total = res.data.data.total;
        pagination.value.current = res.data.data.current;
        pagination.value.size = res.data.data.size;
      } catch (error) {
        console.error('获取题目失败', error);
      }
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

    // 搜索题目
    const search = async () => {
      try {
        const res = await api(`/questions/${keyword.value}`);
        pagination.value.records = [res.data.data.records];
      } catch (error) {
        alert('没找到该题目');
        console.error('搜索题目失败', error);
      }
    };

    // 重置搜索
    const reset = () => {
      keyword.value = '';
      getQuestions();
    };


    // 添加题目
    const addQuestion = async () => {
      try {
        // 对于主观题类型，清空options字段
        if (['short_answer', 'essay'].includes(newQuestion.type)) {
          newQuestion.options = '';
        }
        
        // 如果是填空题且没有填写选项，设置为空字符串
        if (newQuestion.type === 'fill_blank' && !newQuestion.options.trim()) {
          newQuestion.options = '';
        }
        
        console.log('提交的题目数据:', newQuestion);
        await api.post('/questions', newQuestion);
        dialogFormVisible.value = false; // 关闭弹窗
        await getQuestions(); // 刷新题目列表
        alert('题目添加成功');
        
        // 重置表单
        Object.keys(newQuestion).forEach(key => {
          if (key !== 'creatorId') {
            newQuestion[key] = '';
          }
        });
      } catch (error) {
        console.error('添加题目失败', error);
        alert(`添加题目失败: ${error.response?.data?.message || error.message}`);
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
    
    // 根据题型获取答案输入框的提示文本
    const getAnswerPlaceholder = () => {
      switch(newQuestion.type) {
        case 'single':
          return '请输入正确选项的字母，如：A';
        case 'multiple':
          return '请输入正确选项的字母，多个选项用逗号分隔，如：A,B,C';
        case 'true_false':
          return '请输入正确选项的字母，如：A';
        case 'fill_blank':
          return '请输入填空题答案，多个空用分号分隔';
        case 'short_answer':
          return '请输入简答题参考答案';
        case 'essay':
          return '请输入论述题参考答案';
        default:
          return '请输入答案';
      }
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
      handleSizeChange,
      handleCurrentChange,
      search,
      reset,
      addQuestion,
      deleteQuestion,
      getAnswerPlaceholder,
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
