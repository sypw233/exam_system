<template>
  <el-container>
    <!-- 页头 -->
    <el-header style="display: flex; align-items: center; justify-content: space-between; padding: 10px 0;">
      <div>
        <el-button type="primary" @click="CreateExamDialogFormVisible = true">手动组建试卷</el-button>
        <el-button type="success" @click="randomExamDialogVisible = true">随机组建试卷</el-button>
      </div>
      
      <div style="display: flex; align-items: center;">
        <!-- 按科目筛选 -->
        <el-select 
          v-model="selectedCourseId" 
          placeholder="按科目筛选" 
          clearable 
          @change="filterExamsByCourse"
          style="margin-right: 15px; width: 180px;"
        >
          <el-option v-for="course in courseList" :key="course.id" :label="course.courseName" :value="course.id"></el-option>
        </el-select>
        
        <el-input
          v-model="examSearchKeyword"
          placeholder="请输入考试名称或描述进行筛选"
          clearable
          @input="filterExamList"
          style="width: 300px"
        />
      </div>
      
      <!-- 手动组建试卷弹窗 -->
      <el-dialog v-model="CreateExamDialogFormVisible" title="手动组建试卷" width="800px">
        <el-form :model="examForm" ref="form" label-width="120px">
          <!-- 考试名称 -->
          <el-form-item label="考试名称" :rules="[{ required: true, message: '请输入考试名称', trigger: 'blur' }]">
            <el-input v-model="examForm.title" autocomplete="off"/>
          </el-form-item>
          <!-- 课程选择 -->
          <el-form-item label="选择课程" :rules="[{ required: true, message: '请选择课程', trigger: 'change' }]">
            <el-select v-model="examForm.courseId" placeholder="请选择课程" style="width: 100%">
              <el-option v-for="course in courseList" :key="course.id" :label="course.courseName"
                         :value="course.id"></el-option>
            </el-select>
          </el-form-item>
          <!-- 考试描述 -->
          <el-form-item label="考试描述">
            <el-input v-model="examForm.description" type="textarea" autocomplete="off"/>
          </el-form-item>

          <!-- 开始时间 -->
          <el-form-item label="开始时间" :rules="[{ required: true, message: '请选择开始时间', trigger: 'change' }]">
            <el-date-picker
                v-model="examForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%;"
            />
          </el-form-item>

          <!-- 结束时间 -->
          <el-form-item label="结束时间" :rules="[{ required: true, message: '请选择结束时间', trigger: 'change' }]">
            <el-date-picker
                v-model="examForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%;"
            />
          </el-form-item>

          <!-- 选择题库题目 -->
          <el-form-item label="选择题目">
            <!-- 高级筛选区域 -->
            <div style="margin-bottom: 15px; display: flex; gap: 10px;">
              <el-input
                  v-model="searchKeyword"
                  placeholder="请输入题目关键词"
                  clearable
                  @input="searchQuestions"
                  style="width: 200px;"
              />
              <el-select v-model="filterCategory" placeholder="按分类筛选" clearable @change="applyFilters" style="width: 150px;">
                <el-option 
                  v-for="category in categoryList" 
                  :key="category" 
                  :label="category" 
                  :value="category"
                ></el-option>
              </el-select>
              <el-select v-model="filterDifficulty" placeholder="按难度筛选" clearable @change="applyFilters" style="width: 150px;">
                <el-option label="简单" value="easy"></el-option>
                <el-option label="中等" value="medium"></el-option>
                <el-option label="困难" value="hard"></el-option>
              </el-select>
              <el-select v-model="filterType" placeholder="按题型筛选" clearable @change="applyFilters" style="width: 150px;">
                <el-option label="单选题" value="single"></el-option>
                <el-option label="多选题" value="multiple"></el-option>
                <el-option label="判断题" value="true_false"></el-option>
                <el-option label="填空题" value="fill_blank"></el-option>
                <el-option label="简答题" value="short_answer"></el-option>
              </el-select>
            </div>
            
            <el-table
                :data="filteredQuestions"
                style="width: 100%; height: 400px;"
                :row-key="row => row.id"
                :highlight-current-row="true"
                @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55"></el-table-column>
              <el-table-column prop="category" label="分类" width="150"></el-table-column>
              <el-table-column prop="content" label="题目内容" width="300"></el-table-column>
              <el-table-column prop="type" label="题型" width="150">
                <template #default="scope">
                  <span>{{ formatQuestionType(scope.row.type) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="difficulty" label="难度" width="100">
                <template #default="scope">
                  <span>{{ formatDifficulty(scope.row.difficulty) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>

          <!-- 添加到试卷按钮 -->
          <el-button type="primary" @click="addToExam">添加到试卷</el-button>

          <!-- 显示已选择的题目 -->
          <el-form-item label="已选择题目">
            <el-table
                :data="selectedQuestions"
                style="width: 100%; height: 400px;"
                :row-key="row => row.id"
            >
              <el-table-column prop="category" label="分类" width="150"></el-table-column>
              <el-table-column prop="content" label="题目内容" width="300"></el-table-column>
              <el-table-column prop="type" label="题型" width="150">
                <template #default="scope">
                  <span>{{ formatQuestionType(scope.row.type) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="difficulty" label="难度" width="100">
                <template #default="scope">
                  <span>{{ formatDifficulty(scope.row.difficulty) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button size="small" @click="removeFromExam(scope.row)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-form>

        <!-- 弹窗底部按钮 -->
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="CreateExamDialogFormVisible = false">取消</el-button>
            <el-button type="primary" @click="submitExam">提交</el-button>
          </div>
        </template>
      </el-dialog>
      
      <!-- 随机组卷弹窗 -->
      <el-dialog v-model="randomExamDialogVisible" title="随机组建试卷" width="800px">
        <RandomExamForm ref="randomExamFormRef" :courseList="courseList" />
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="randomExamDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitRandomExam">提交</el-button>
          </div>
        </template>
      </el-dialog>
    </el-header>
    
    <!-- 查看考试详情弹窗 -->
    <el-dialog
        v-model="viewExamDialogVisible"
        title="查看考试详情"
        width="800px"
    >
      <el-form :model="viewExamForm" label-width="120px">
        <el-form-item label="考试名称">
          <el-input v-model="viewExamForm.title" readonly/>
        </el-form-item>

        <el-form-item label="考试描述">
          <el-input v-model="viewExamForm.description" readonly type="textarea"/>
        </el-form-item>

        <el-form-item label="开始时间">
          <el-input v-model="viewExamForm.startTime" readonly/>
        </el-form-item>

        <el-form-item label="结束时间">
          <el-input v-model="viewExamForm.endTime" readonly/>
        </el-form-item>

        <!-- 显示考试题目 -->
        <el-form-item label="考试题目">
          <el-table :data="viewExamForm.questions" style="width: 100%">
            <el-table-column prop="category" label="分类" width="150"></el-table-column>
            <el-table-column prop="content" label="题目内容" width="300"></el-table-column>
            <el-table-column prop="type" label="题型" width="150">
              <template #default="scope">
                <span>{{ formatQuestionType(scope.row.type) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="difficulty" label="难度" width="100">
              <template #default="scope">
                <span>{{ formatDifficulty(scope.row.difficulty) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        
        <!-- 导出成绩按钮 -->
        <el-form-item>
          <el-button type="primary" @click="exportResults(viewExamForm.id)">导出成绩</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    
    <!-- 主体部分 -->
    <el-main>
      <el-table :data="filteredExamList" style="width: 100%">
        <el-table-column prop="title" label="考试名称"></el-table-column>
        <el-table-column prop="description" label="考试介绍"></el-table-column>
        <el-table-column label="课程">
          <template #default="scope">
            <!-- 使用getCourseNameById来显示课程名 -->
            {{ getCourseNameById(scope.row.courseId) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间"></el-table-column>
        <el-table-column prop="endTime" label="结束时间"></el-table-column>
        <el-table-column label="操作">
          <template v-slot="scope">
            <el-button @click="viewExam(scope.row)" size="small">查看</el-button>
            <el-button @click="deleteExam(scope.row.id)" type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 考试列表分页 -->
      <!--      <el-pagination-->
      <!--          @size-change="handleExamSizeChange"-->
      <!--          @current-change="handleExamCurrentChange"-->
      <!--          :current-page="examPagination.current"-->
      <!--          :page-sizes="[10, 20, 40]"-->
      <!--          :page-size="examPagination.size"-->
      <!--          layout="total, sizes, prev, pager, next, jumper"-->
      <!--          :total="examPagination.total"-->
      <!--      />-->
    </el-main>
  </el-container>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import api from '@/api/axios';
import RandomExamForm from '@/components/RandomExamForm.vue';

export default {
  name: 'ExamManager',
  components: {
    RandomExamForm
  },
  setup() {
    const createExamDialogVisible = ref(false);
    const randomExamDialogVisible = ref(false);
    const randomExamFormRef = ref(null);
    const examForm = reactive({
      title: '',
      description: '',
      course: '',
      startTime: '',
      endTime: '',
      courseId: '',
    });
    const viewExamDialogVisible = ref(false); // Dialog for viewing exam details
    const viewExamForm = reactive({
      id: null,
      title: '',
      description: '',
      course: '',
      courseId: '',
      startTime: '',
      endTime: '',
      questions: [],
    });
    const courseList = ref([]);
    const examSearchKeyword = ref('');
    const selectedCourseId = ref(null);
    const questions = ref([]);
    const filteredQuestions = ref([]);
    const categoryList = ref([]);
    const selectedQuestions = ref([]);
    const examList = ref([]);
    const filteredExamList = ref([]);
    const searchKeyword = ref('');
    const selectedRows = ref([]);
    
    // 筛选条件
    const filterCategory = ref('');
    const filterDifficulty = ref('');
    const filterType = ref('');

    // 考试列表分页状态
    const examPagination = ref({
      current: 1,
      total: 0,
      size: 10,
    });
    
    // 格式化题目类型
    const formatQuestionType = (type) => {
      const types = {
        'single': '单选题',
        'multiple': '多选题',
        'true_false': '判断题',
        'fill_blank': '填空题',
        'short_answer': '简答题'
      };
      return types[type] || type;
    };
    
    // 格式化难度
    const formatDifficulty = (difficulty) => {
      const difficulties = {
        'easy': '简单',
        'medium': '中等',
        'hard': '困难'
      };
      return difficulties[difficulty] || difficulty;
    };
    
    // 获取课程列表
    const getCourses = async () => {
      try {
        const res = await api('/courses');  // 假设这个接口返回课程列表
        courseList.value = res.data;  // 将课程数据存储在 courseList 中
        console.log("courseList")
        console.log(courseList.value)
      } catch (error) {
        console.error('获取课程列表失败', error);
      }
    };

    // 根据id获取课程名称的方法
    const getCourseNameById = (courseId) => {
      const course = courseList.value.find(item => item.id === courseId);
      return course ? course.courseName : null;  // 如果找到对应课程，返回courseName，否则返回null
    };
    
    // 获取所有分类
    const getCategories = async () => {
      try {
        const res = await api.get('/questions/categories');
        if (res.data && res.data.data) {
          categoryList.value = res.data.data;
        }
      } catch (error) {
        console.error('获取分类列表失败', error);
      }
    };

    // 获取题库中的题目
    const getQuestions = async () => {
      try {
        const res = await api('/questions'); // 不需要分页
        questions.value = res.data.data;
        filteredQuestions.value = questions.value; // 默认显示所有题目
      } catch (error) {
        console.error('获取题目失败', error);
      }
    };

    // 获取考试列表
    const getExamInfo = async () => {
      try {
        const res = await api(`/exams/all`);
        examList.value = res.data.data;
        filteredExamList.value = examList.value;
        console.log(examList.value)
      } catch (error) {
        console.error('获取考试列表失败', error);
      }
    };
    
    // 按科目筛选考试
    const filterExamsByCourse = async () => {
      if (!selectedCourseId.value) {
        // 如果没有选择科目，显示所有考试
        filteredExamList.value = examList.value;
        return;
      }
      
      try {
        // 从后端获取指定科目的考试
        const res = await api.get(`/exams/course/${selectedCourseId.value}/1/100`);
        if (res.data && res.data.data && res.data.data.records) {
          filteredExamList.value = res.data.data.records;
        }
      } catch (error) {
        console.error('按科目筛选考试失败', error);
      }
    };
    
    // 删除考试
    const deleteExam = async (examId) => {
      try {
        await api.delete(`/exams/${examId}`);
        alert('考试删除成功');
        await getExamInfo(); // 删除成功后重新获取考试列表
      } catch (error) {
        console.error('删除考试失败', error);
      }
    };
    
    // 导出成绩Excel
    const exportResults = async (examId) => {
      try {
        const response = await api.get(`/exam-submissions/export/exam/${examId}`, {
          responseType: 'blob' // 设置响应类型为blob
        });
        
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        
        // 创建下载链接并自动点击
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `exam_${examId}_results.xlsx`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
      } catch (error) {
        console.error('导出成绩失败', error);
        alert('导出成绩失败');
      }
    };

    const viewExam = async (exam) => {
      try {
        console.log("click" + exam.id)
        const res = await api(`/exams/${exam.id}/details`);
        const examDetails = res.data;
        console.log(examDetails)
        viewExamForm.id = examDetails.id;
        viewExamForm.title = examDetails.title;
        viewExamForm.description = examDetails.description;
        viewExamForm.startTime = examDetails.startTime;
        viewExamForm.endTime = examDetails.endTime;
        viewExamForm.questions = examDetails.questions;
        viewExamDialogVisible.value = true;
      } catch (error) {
        alert("获取数据失败" + error)
        console.error('获取考试详情失败', error);
      }
    };
    
    const filterExamList = () => {
      if (examSearchKeyword.value.trim() === '') {
        filteredExamList.value = examList.value;
      } else {
        filteredExamList.value = examList.value.filter(exam =>
            exam.title.toLowerCase().includes(examSearchKeyword.value.toLowerCase()) ||
            exam.description.toLowerCase().includes(examSearchKeyword.value.toLowerCase())
        );
      }
    };
    
    // 应用高级筛选
    const applyFilters = async () => {
      // 根据筛选条件从服务器获取数据
      try {
        let url = '/questions';
        
        // 如果有筛选条件，使用相应的API
        if (filterCategory.value && filterDifficulty.value) {
          url = `/questions/category/${filterCategory.value}/difficulty/${filterDifficulty.value}`;
        } else if (filterCategory.value) {
          url = `/questions/category/${filterCategory.value}`;
        } else if (filterDifficulty.value) {
          url = `/questions/difficulty/${filterDifficulty.value}`;
        }
        
        const res = await api.get(url);
        let filteredData = res.data.data;
        
        // 进一步根据题型筛选（前端筛选）
        if (filterType.value) {
          filteredData = filteredData.filter(q => q.type === filterType.value);
        }
        
        // 进一步根据搜索关键词筛选（前端筛选）
        if (searchKeyword.value.trim() !== '') {
          filteredData = filteredData.filter(q =>
            q.content.toLowerCase().includes(searchKeyword.value.toLowerCase())
          );
        }
        
        filteredQuestions.value = filteredData;
      } catch (error) {
        console.error('筛选题目失败', error);
      }
    };
    
    // 搜索题目
    const searchQuestions = () => {
      // 如果已经应用了筛选条件，则调用applyFilters
      if (filterCategory.value || filterDifficulty.value || filterType.value) {
        applyFilters();
        return;
      }
      
      // 否则直接在本地数据中筛选
      if (searchKeyword.value.trim() === '') {
        filteredQuestions.value = questions.value;
      } else {
        filteredQuestions.value = questions.value.filter(q =>
            q.content.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
            q.category.toLowerCase().includes(searchKeyword.value.toLowerCase())
        );
      }
    };

    // 添加选中的题目到试卷
    const addToExam = () => {
      selectedRows.value.forEach((selectedQuestion) => {
        // 检查是否已经添加了该题目，防止重复添加
        if (!selectedQuestions.value.some(q => q.id === selectedQuestion.id)) {
          selectedQuestions.value.push(selectedQuestion);
        }
      });
    };

    // 从试卷中移除题目
    const removeFromExam = (question) => {
      selectedQuestions.value = selectedQuestions.value.filter(q => q.id !== question.id);
    };

    // 提交考试
    const submitExam = async () => {
      try {
        const examData = {
          title: examForm.title,
          description: examForm.description,
          startTime: examForm.startTime,
          endTime: examForm.endTime,
          courseId: examForm.courseId,
          totalScore: 100,
          creatorId: localStorage.getItem("id"),
          questions: selectedQuestions.value.map(q => q.id),
        };
        console.log(examData)
        await api.post('/exams', examData);
        createExamDialogVisible.value = false;
        alert('考试创建成功');
        selectedQuestions.value = [];
        await getExamInfo();
      } catch (error) {
        console.error('创建考试失败', error);
      }
    };
    
    // 提交随机组卷
    const submitRandomExam = async () => {
      try {
        const formData = await randomExamFormRef.value.validate();
        if (!formData) {
          return;
        }
        
        const response = await api.post('/exams/random', formData);
        if (response.status === 200) {
          alert('随机组卷成功');
          randomExamDialogVisible.value = false;
          await getExamInfo(); // 重新获取考试列表
        }
      } catch (error) {
        console.error('随机组卷失败', error);
        alert('随机组卷失败: ' + (error.response?.data?.message || error.message));
      }
    };

    // 处理表格选择变化
    const handleSelectionChange = (selection) => {
      selectedRows.value = selection;
    };

    // 初始化
    onMounted(() => {
      getQuestions();
      getExamInfo();
      getCourses();
      getCategories();
    });

    return {
      CreateExamDialogFormVisible: createExamDialogVisible,
      randomExamDialogVisible,
      randomExamFormRef,
      examForm,
      examList,
      examPagination,
      filteredQuestions,
      selectedQuestions,
      searchKeyword,
      selectedRows,
      viewExamDialogVisible,
      viewExamForm,
      examSearchKeyword,
      filteredExamList,
      courseList,
      categoryList,
      selectedCourseId,
      filterCategory,
      filterDifficulty, 
      filterType,
      getCourseNameById,
      getCourses,
      filterExamList,
      filterExamsByCourse,
      viewExam,
      deleteExam,
      exportResults,
      addToExam,
      removeFromExam,
      submitExam,
      submitRandomExam,
      getExamInfo,
      searchQuestions,
      applyFilters,
      handleSelectionChange,
      formatQuestionType,
      formatDifficulty
    };
  },
};
</script>

<style>
.el-header {
  padding: 0;
  margin-bottom: 20px;
}

.el-button {
  border-radius: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>