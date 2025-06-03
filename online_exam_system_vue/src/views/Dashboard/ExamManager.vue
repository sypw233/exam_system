<template>
  <div class="exam-manager-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <el-icon class="title-icon">
            <Files/>
          </el-icon>
          试卷管理
        </h2>
        <p class="page-description">管理系统中的所有考试试卷信息</p>
      </div>
    </div>
    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="never">
        <div class="search-content">
          <div class="search-left">
            <el-input
                v-model="searchKeyword"
                placeholder="输入考试名称进行搜索"
                @input="filterExams"
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
            <el-button type="primary" @click="openManualExamDialog">
              <el-icon>
                <Plus/>
              </el-icon>
              手动组卷
            </el-button>
            <el-button type="success" @click="openRandomExamDialog">
              <el-icon>
                <Refresh/>
              </el-icon>
              随机组卷
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    <!-- 主体部分 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="table-header">
            <span class="table-title">试卷列表</span>
            <el-tag type="info" size="small">共 {{ filteredExamList.length }} 份试卷</el-tag>
          </div>
        </template>

        <el-table
            :data="filteredExamList"
            class="exam-table"
            stripe
            :header-cell-style="{ background: '#f8f9fa', color: '#495057' }"
        >
          <el-table-column label="ID" prop="id" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.id }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="title" label="考试名称" min-width="150"></el-table-column>
          <el-table-column prop="description" label="考试介绍" min-width="200"></el-table-column>
          <el-table-column label="课程" width="100">
            <template #default="scope">
              <!-- 使用getCourseNameById来显示课程名 -->
              {{ getCourseNameById(scope.row.courseId) }}
            </template>
          </el-table-column>

          <el-table-column label="开始时间" width="160" align="center">
            <template #default="scope">
              <div class="time-cell">
                <span>{{ formatDateTime(scope.row.startTime) }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="结束时间" width="160" align="center">
            <template #default="scope">
              <div class="time-cell">
                <span>{{ formatDateTime(scope.row.endTime) }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="220" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button
                    type="primary"
                    size="small"
                    @click="viewExam(scope.row)"
                >
                  <el-icon>
                    <View/>
                  </el-icon>
                  查看
                </el-button>
                <el-button
                    type="success"
                    size="small"
                    @click="exportExamSubmissions(scope.row.id)"
                >
                  <el-icon>
                    <Download/>
                  </el-icon>
                  导出
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    @click="deleteExam(scope.row.id)"
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
            <el-select v-model="filterCategory" placeholder="按分类筛选" clearable @change="applyFilters"
                       style="width: 150px;">
              <el-option
                  v-for="category in categoryList"
                  :key="category"
                  :label="category"
                  :value="category"
              ></el-option>
            </el-select>
            <el-select v-model="filterDifficulty" placeholder="按难度筛选" clearable @change="applyFilters"
                       style="width: 150px;">
              <el-option label="简单" value="easy"></el-option>
              <el-option label="中等" value="medium"></el-option>
              <el-option label="困难" value="hard"></el-option>
            </el-select>
            <el-select v-model="filterType" placeholder="按题型筛选" clearable @change="applyFilters"
                       style="width: 150px;">
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
      <RandomExamForm ref="randomExamFormRef" :courseList="courseList"/>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="randomExamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRandomExam">提交</el-button>
        </div>
      </template>
    </el-dialog>
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
          <el-input :value="formatDateTime(viewExamForm.startTime)" readonly/>
        </el-form-item>

        <el-form-item label="结束时间">
          <el-input :value="formatDateTime(viewExamForm.endTime)" readonly/>
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
  </div>
</template>

<style scoped>

</style>

<script>
import {ref, reactive, onMounted} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import api from '@/api/axios';
import RandomExamForm from '@/components/RandomExamForm.vue';
import {Calendar, Delete, Download, Files, Plus, Refresh, View} from "@element-plus/icons-vue";

export default {
  name: 'ExamManager',
  components: {
    Delete,
    Download,
    Calendar,
    Refresh,
    Plus,
    View,
    Files,
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

    /**
     * 格式化日期时间为中国北京时间
     * @param {string} dateTimeString - ISO格式的日期时间字符串
     * @returns {string} 格式化后的中国北京时间字符串
     */
    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return '';

      try {
        const date = new Date(dateTimeString);
        // 转换为中国北京时间 (UTC+8)
        const beijingTime = new Date(date.getTime() + (8 * 60 * 60 * 1000));

        const year = beijingTime.getFullYear();
        const month = String(beijingTime.getMonth() + 1).padStart(2, '0');
        const day = String(beijingTime.getDate()).padStart(2, '0');
        const hours = String(beijingTime.getHours()).padStart(2, '0');
        const minutes = String(beijingTime.getMinutes()).padStart(2, '0');
        const seconds = String(beijingTime.getSeconds()).padStart(2, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      } catch (error) {
        console.error('时间格式化错误:', error);
        return dateTimeString;
      }
    };

    /**
     * 获取课程列表
     */
    const getCourses = async () => {
      try {
        const res = await api.get('/courses');  // 获取课程列表
        courseList.value = res.data.data || res.data;  // 将课程数据存储在 courseList 中
        console.log('课程列表:', courseList.value);
      } catch (error) {
        console.error('获取课程列表失败', error);
        // 如果获取失败，提供一些默认课程
        courseList.value = [];
      }
    };

    // 根据id获取课程名称的方法
    const getCourseNameById = (courseId) => {
      const course = courseList.value.find(item => item.id === courseId);
      return course ? course.courseName : null;  // 如果找到对应课程，返回courseName，否则返回null
    };

    /**
     * 获取所有分类
     */
    const getCategories = async () => {
      try {
        // 先获取所有题目，然后提取分类
        const res = await api.get('/questions');
        if (res.data && res.data.data) {
          // 从题目数据中提取唯一的分类
          const categories = [...new Set(res.data.data.map(q => q.category).filter(c => c))];
          categoryList.value = categories;
        }
      } catch (error) {
        console.error('获取分类列表失败', error);
        // 如果获取失败，提供一些默认分类
        categoryList.value = ['数学', '语文', '英语', '物理', '化学', '生物', '历史', '地理', '政治'];
      }
    };

    /**
     * 获取题库中的题目
     */
    const getQuestions = async () => {
      try {
        const res = await api.get('/questions'); // 获取所有题目
        questions.value = res.data.data || res.data;
        filteredQuestions.value = questions.value; // 默认显示所有题目
      } catch (error) {
        console.error('获取题目失败', error);
        questions.value = [];
        filteredQuestions.value = [];
      }
    };

    /**
     * 获取考试列表
     */
    const getExamInfo = async () => {
      try {
        const res = await api.get('/exams/all');
        examList.value = res.data.data || res.data;
        filteredExamList.value = examList.value;
        console.log('考试列表:', examList.value);
      } catch (error) {
        console.error('获取考试列表失败', error);
        examList.value = [];
        filteredExamList.value = [];
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

    /**
     * 删除考试
     * @param {number} examId - 考试ID
     */
    const deleteExam = async (examId) => {
      try {
        await ElMessageBox.confirm(
            '确定要删除该考试吗？删除后无法恢复！',
            '删除确认',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            }
        );
        await api.delete(`/exams/${examId}`);
        ElMessage.success('考试删除成功');
        await getExamInfo(); // 删除成功后重新获取考试列表
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除考试失败', error);
          ElMessage.error('删除考试失败');
        }
      }
    };

    // 导出成绩Excel
    const exportResults = async (examId) => {
      try {
        const response = await api.get(`/exam-submissions/export/exam/${examId}`, {
          responseType: 'blob' // 设置响应类型为blob
        });

        // 创建Blob对象
        const blob = new Blob([response.data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});

        // 创建下载链接并自动点击
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `exam_${examId}_results.xlsx`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

      } catch (error) {
        console.error('导出成绩失败', error);
        ElMessage.error('导出成绩失败');
      }
    };

    /**
     * 导出试卷提交记录为Excel
     * @param {number} examId - 考试ID
     */
    const exportExamSubmissions = async (examId) => {
      try {
        const response = await api.get(`/exam-submissions/exam/${examId}/export`, {
          responseType: 'blob'
        });

        // 获取文件名，优先从响应头获取
        let filename = `exam_${examId}_submissions.xlsx`;
        const contentDisposition = response.headers['content-disposition'];
        if (contentDisposition) {
          const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
          if (filenameMatch) {
            filename = filenameMatch[1];
          }
        }

        // 创建下载链接
        const url = window.URL.createObjectURL(new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        ElMessage.success('Excel文件导出成功！');
      } catch (error) {
        console.error('导出Excel失败', error);
        ElMessage.error('导出失败: ' + (error.response?.data?.message || error.message));
      }
    };

    /**
     * 查看考试详情
     * @param {Object} exam - 考试对象
     */
    const viewExam = async (exam) => {
      try {
        console.log("查看考试详情，ID:" + exam.id);
        const res = await api.get(`/exams/${exam.id}/details`);

        // 检查响应数据结构
        const examDetails = res.data.data || res.data;
        console.log('考试详情:', examDetails);

        viewExamForm.id = examDetails.id;
        viewExamForm.title = examDetails.title;
        viewExamForm.description = examDetails.description;
        viewExamForm.startTime = examDetails.startTime;
        viewExamForm.endTime = examDetails.endTime;
        viewExamForm.questions = examDetails.questions || [];
        viewExamDialogVisible.value = true;
      } catch (error) {
        console.error('获取考试详情失败', error);
        if (error.response && error.response.status === 403) {
          ElMessage.error('权限不足，无法查看考试详情');
        } else {
          ElMessage.error('获取考试详情失败: ' + (error.response?.data?.message || error.message));
        }
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
        ElMessage.success('考试创建成功');
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
          ElMessage.success('随机组卷成功');
          randomExamDialogVisible.value = false;
          await getExamInfo(); // 重新获取考试列表
        }
      } catch (error) {
        console.error('随机组卷失败', error);
        ElMessage.error('随机组卷失败: ' + (error.response?.data?.message || error.message));
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
      formatDifficulty,
      formatDateTime,
      exportExamSubmissions
    };
  },
};
</script>

<style scoped>

.exam-manager-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

.exam-table {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.exam-table .el-table__row) {
  height: 56px;
}

:deep(.exam-table .el-table__cell) {
  padding: 12px 0;
}
/* 时间单元格 */
.time-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
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

/* 弹窗样式 */
:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e9ecef;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 20px 0;
  border-top: 1px solid #e9ecef;
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

  .search-input,
  .course-select {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>