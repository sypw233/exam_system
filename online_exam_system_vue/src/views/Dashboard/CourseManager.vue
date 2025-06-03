<template>
  <div class="course-manager-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <el-icon class="title-icon"><Reading /></el-icon>
          课程管理
        </h2>
        <p class="page-description">管理系统中的所有课程信息</p>
      </div>
    </div>

    <!-- 搜索和操作区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="never">
        <div class="search-content">
          <div class="search-left">
            <el-input
                v-model="filterText"
                placeholder="搜索课程或者授课教师"
                clearable
                class="search-input"
                @input="filterCourses"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          
          <div class="search-right">
            <el-button type="primary" @click="openCreateDialog">
              <el-icon><Plus /></el-icon>
              添加课程
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 课程列表 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="table-header">
            <span class="table-title">课程列表</span>
            <el-tag type="info" size="small">共 {{ filteredCourses.length }} 个课程</el-tag>
          </div>
        </template>
        
        <!-- 课程表格 -->
        <el-table 
          :data="filteredCourses"
          class="course-table"
          stripe
          :header-cell-style="{ background: '#f8f9fa', color: '#495057' }"
        >
          <!-- 显示课程名称 -->
          <el-table-column prop="courseName" label="课程名称" min-width="200">
            <template #default="scope">
              <div class="course-name">
                <el-icon><Reading /></el-icon>
                <span>{{ scope.row.courseName }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 显示授课教师 -->
          <el-table-column label="授课教师" width="180">
            <template #default="scope">
              <div class="teacher-info">
                <el-icon><UserFilled /></el-icon>
                <span>{{ getTeacherName(scope.row.teacherId) }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 操作列，编辑和删除课程 -->
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <div class="action-buttons">
                <el-button type="primary" size="small" @click="openEditDialog(scope.row)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="deleteCourse(scope.row.id)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>

  <!-- 创建课程对话框 -->
  <el-dialog v-model="createDialogVisible" title="添加课程" @close="resetForm" class="course-dialog">
    <el-form :model="form" ref="formData" label-width="120px">
      <el-form-item label="课程名称" prop="courseName"
                    :rules="[{ required: true, message: '请输入课程名称', trigger: 'blur' }]">
        <el-input v-model="form.courseName" placeholder="请输入课程名称">
          <template #prefix>
            <el-icon><Reading /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <!-- 授课教师选择 -->
      <el-form-item label="授课教师" prop="teacherId"
                    :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
        <el-select v-model="form.teacherId" placeholder="请选择授课教师" :disabled="isTeacherRole" style="width: 100%">
          <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.username"
                     :value="teacher.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <!-- 对话框底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createCourse">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 编辑课程对话框 -->
  <el-dialog v-model="editDialogVisible" title="编辑课程" @close="resetForm" class="course-dialog">
    <el-form :model="form" ref="formData" label-width="120px">
      <el-form-item label="课程名称" prop="courseName"
                    :rules="[{ required: true, message: '请输入课程名称', trigger: 'blur' }]">
        <el-input v-model="form.courseName" placeholder="请输入课程名称">
          <template #prefix>
            <el-icon><Reading /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <!-- 授课教师选择 -->
      <el-form-item label="授课教师" prop="teacherId"
                    :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
        <el-select v-model="form.teacherId" placeholder="请选择授课教师" :disabled="isTeacherRole" style="width: 100%">
          <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.username"
                     :value="teacher.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <el-divider content-position="left">学生管理</el-divider>

    <!-- 学生管理 -->
    <el-form label-width="120px">
      <!-- 学生筛选框 -->
      <el-form-item label="搜索学生">
        <el-input
            v-model="studentFilterText"
            placeholder="按学生姓名搜索"
            clearable
            @input="filterStudents"
            style="width: 100%"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <!-- 总学生列表 -->
      <el-form-item label="总学生列表">
        <el-table :data="filteredStudents" style="width: 100%" border max-height="300px" stripe>
          <el-table-column prop="username" label="学生姓名" width="180">
            <template #default="scope">
              <div class="student-info">
                <el-icon><User /></el-icon>
                <span>{{ scope.row.username }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="addStudent(scope.row)">
                <el-icon><Plus /></el-icon>
                添加
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <!-- 当前课程学生列表 -->
      <el-form-item label="课程学生列表">
        <el-table :data="enrolledStudents" style="width: 100%" border max-height="300px" stripe>
          <el-table-column prop="username" label="学生姓名" width="180"></el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button type="danger" size="small" @click="removeStudent(scope.row.studentId)">
                <el-icon><Delete /></el-icon>
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

    </el-form>

    <!-- 对话框底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCourse">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import {ref, onMounted, onActivated, watch, computed} from "vue";
import {useRoute} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import api from "@/api/axios.js";
import { Reading, UserFilled, User, Search, Edit, Delete, Plus } from '@element-plus/icons-vue';

export default {
  name: "CourseManager",
  components: {
    Reading,
    UserFilled,
    User,
    Search,
    Edit,
    Delete,
    Plus
  },
  setup() {
    const route = useRoute();
    // 课程数据和学生、教师数据
    const courses = ref([]);
    const teachers = ref([]);
    const students = ref([]);
    const filterText = ref("");
    const studentFilterText = ref(""); // 学生筛选文本
    const filteredCourses = ref([]);
    const filteredStudents = ref([]);
    const studentMap = computed(() => {
      const map = new Map();
      students.value.forEach((student) => map.set(student.id, student.username));
      return map;
    });
    const selectedStudentId = ref(null);
    const createDialogVisible = ref(false); // 创建课程对话框的显示状态
    const editDialogVisible = ref(false); // 编辑课程对话框的显示状态
    const currentCourseId = ref(null); // 当前编辑的课程ID
    const enrolledStudents = ref([]); // 已选学生列表
    const form = ref({
      courseName: "",
      teacherId: "",
    });

    // 检查用户角色，判断是否为教师
    const isTeacherRole = computed(() => localStorage.getItem("role") === "TEACHER");

    // 课程过滤函数
    const filterCourses = () => {
      filteredCourses.value = courses.value.filter((course) => {
        const teacher = teachers.value.find((t) => t.id === course.teacherId);
        const teacherName = teacher ? teacher.username : "未知";
        return (
            course.courseName.toLowerCase().includes(filterText.value.toLowerCase()) ||
            teacherName.toLowerCase().includes(filterText.value.toLowerCase())
        );
      });
    };

    // 学生过滤函数
    const filterStudents = () => {
      filteredStudents.value = students.value.filter((student) =>
          student.username.toLowerCase().includes(studentFilterText.value.toLowerCase())
      );
    };

    // 获取课程数据
    const getCourses = async () => {
      try {
        const response = ref('');
        const userRole = localStorage.getItem("role");
        if (userRole === "TEACHER") {
          const userId = localStorage.getItem("id");
          response.value = await api.get(`/courses/teacher/${userId}`);
        } else {
          response.value = await api.get("/courses");
        }
        courses.value = response.value.data;
        console.log(courses.value)
        filterCourses();
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "获取课程列表失败");
      }
    };

    // 获取教师数据
    const getTeachers = async () => {
      try {
        const response = await api.get("/users/teachers");
        teachers.value = response.data;
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "获取教师列表失败");
      }
    };

    // 获取学生数据
    const getStudents = async () => {
      try {
        const response = await api.get("/users/students");
        students.value = response.data;
        filteredStudents.value = students.value;
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "获取学生列表失败");
      }
    };

    // 获取已选课学生
    const getEnrolledStudents = async (courseId) => {
      try {
        const response = await api.get(`/course-selections/course/${courseId}`);
        enrolledStudents.value = response.data.map((item) => ({
          ...item,
          username: studentMap.value.get(item.studentId) || "未知",
        }));
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "获取选课学生失败");
      }
    };

    // 打开创建课程对话框
    const openCreateDialog = () => {
      form.value = {courseName: "", teacherId: localStorage.getItem("id")};
      createDialogVisible.value = true;
    };

    // 创建课程
    const createCourse = async () => {
      try {
        await api.post("/courses", form.value);
        ElMessage.success("课程创建成功");
        createDialogVisible.value = false;
        await getCourses();
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "创建课程失败");
      }
    };

    // 打开编辑课程对话框
    const openEditDialog = async (course) => {
      form.value = {...course};
      currentCourseId.value = course.id;
      editDialogVisible.value = true;
      await getEnrolledStudents(course.id);
    };

    // 更新课程
    const updateCourse = async () => {
      try {
        await api.put(`/courses/${currentCourseId.value}`, form.value);
        ElMessage.success("课程更新成功");
        editDialogVisible.value = false;
        await getCourses();
      } catch (error) {
        ElMessage.error(error.response?.data?.message || "更新课程失败");
      }
    };

    // 删除课程
    const deleteCourse = async (courseId) => {
      try {
        await ElMessageBox.confirm("确认删除此课程?", "警告", {type: "warning"});
        await api.delete(`/courses/${courseId}`);
        ElMessage.success("课程删除成功");
        await getCourses();
      } catch (error) {
        ElMessage.error("删除课程失败");
      }
    };

    // 添加学生到课程
    const addStudent = async (student) => {
      const isAlreadyEnrolled = enrolledStudents.value.some(
          (enrolledStudent) => enrolledStudent.studentId === student.id
      );

      if (isAlreadyEnrolled) {
        ElMessage.warning("该学生已在课程中，不能重复添加");
        return;
      }

      try {
        await api.post(`/course-selections`, {
          courseId: currentCourseId.value,
          studentId: student.id,
        });
        ElMessage.success("学生添加成功");
        await getEnrolledStudents(currentCourseId.value);
      } catch (error) {
        ElMessage.error("添加学生失败");
      }
    };

    // 从课程中移除学生
    const removeStudent = async (studentId) => {
      try {
        await api.delete(`/course-selections/${currentCourseId.value}/students/${studentId}`);
        ElMessage.success("学生移除成功");
        await getEnrolledStudents(currentCourseId.value);
      } catch (error) {
        ElMessage.error("移除学生失败");
      }
    };

    // 获取教师名称
    const getTeacherName = (teacherId) => {
      const teacher = teachers.value.find((t) => t.id === teacherId);
      return teacher ? teacher.username : "未知";
    };

    // 重置表单
    const resetForm = () => {
      form.value = {courseName: "", teacherId: ""};
    };

    // 页面加载时获取数据
    onMounted(() => {
      getCourses();
      getTeachers();
      getStudents();
    });

    /**
     * 组件激活时重新获取数据（解决路由切换缓存问题）
     */
    onActivated(() => {
      getCourses();
      getTeachers();
      getStudents();
    });

    /**
     * 监听路由变化，确保切换到此页面时重新加载数据
     */
    watch(() => route.path, (newPath) => {
      if (newPath === '/dashboard/course') {
        getCourses();
        getTeachers();
        getStudents();
      }
    });

    return {
      courses,
      teachers,
      students,
      filterText,
      studentFilterText,
      filteredCourses,
      filteredStudents,
      selectedStudentId,
      createDialogVisible,
      editDialogVisible,
      currentCourseId,
      enrolledStudents,
      form,
      isTeacherRole,
      filterCourses,
      filterStudents,
      getTeacherName,
      openCreateDialog,
      openEditDialog,
      resetForm,
      createCourse,
      updateCourse,
      deleteCourse,
      addStudent,
      removeStudent,
    };
  },
};
</script>

<style scoped>
.course-manager-container {
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

.course-table {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.course-table .el-table__row) {
  height: 56px;
}

:deep(.course-table .el-table__cell) {
  padding: 12px 0;
}

/* 课程名称样式 */
.course-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 教师信息样式 */
.teacher-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 学生信息样式 */
.student-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-buttons .el-button {
  min-width: 72px;
  height: 32px;
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
  font-weight: 500;
}

.action-buttons .el-button .el-icon {
  margin-right: 4px;
  font-size: 12px;
}

/* 对话框样式 */
:deep(.course-dialog .el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e9ecef;
}

:deep(.course-dialog .el-dialog__body) {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-left {
    flex-direction: column;
  }
  
  .search-input {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
