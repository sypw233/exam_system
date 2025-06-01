<template>
  <el-header>
    <!-- 添加课程按钮 -->
    <el-button type="primary" @click="openCreateDialog">添加课程</el-button>

    <!-- 课程搜索框，支持搜索课程或授课教师 -->
    <el-input
        v-model="filterText"
        placeholder="搜索课程或者授课教师"
        clearable
        suffix-icon="el-icon-search"
        style="width: 300px; margin-left: 20px;"
        @input="filterCourses"
    ></el-input>
  </el-header>

  <el-main>
    <!-- 课程表格 -->
    <el-table :data="filteredCourses">
      <!-- 显示课程名称 -->
      <el-table-column prop="courseName" label="课程名称"></el-table-column>

      <!-- 显示授课教师 -->
      <el-table-column label="授课教师" width="180">
        <template #default="scope">
          {{ getTeacherName(scope.row.teacherId) }}
        </template>
      </el-table-column>

      <!-- 操作列，编辑和删除课程 -->
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteCourse(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-main>

  <!-- 创建课程对话框 -->
  <el-dialog v-model="createDialogVisible" title="添加课程" @close="resetForm">
    <el-form :model="form" ref="formData" label-width="120px">
      <el-form-item label="课程名称" prop="courseName"
                    :rules="[{ required: true, message: '请输入课程名称', trigger: 'blur' }]">
        <el-input v-model="form.courseName" placeholder="请输入课程名称"></el-input>
      </el-form-item>

      <!-- 授课教师选择 -->
      <el-form-item label="授课教师" prop="teacherId"
                    :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
        <el-select v-model="form.teacherId" placeholder="请选择授课教师" :disabled="isTeacherRole">
          <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.username"
                     :value="teacher.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <!-- 对话框底部按钮 -->
    <div slot="footer" class="dialog-footer">
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="createCourse">确定</el-button>
    </div>
  </el-dialog>

  <!-- 编辑课程对话框 -->
  <el-dialog v-model="editDialogVisible" title="编辑课程" @close="resetForm">
    <el-form :model="form" ref="formData" label-width="120px">
      <el-form-item label="课程名称" prop="courseName"
                    :rules="[{ required: true, message: '请输入课程名称', trigger: 'blur' }]">
        <el-input v-model="form.courseName" placeholder="请输入课程名称"></el-input>
      </el-form-item>

      <!-- 授课教师选择 -->
      <el-form-item label="授课教师" prop="teacherId"
                    :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
        <el-select v-model="form.teacherId" placeholder="请选择授课教师" :disabled="isTeacherRole">
          <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.username"
                     :value="teacher.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <el-divider></el-divider>

    <!-- 学生管理 -->
    <el-form label-width="120px">
      <!-- 学生筛选框 -->
      <el-form-item label="搜索学生">
        <el-input
            v-model="studentFilterText"
            placeholder="按学生姓名搜索"
            clearable
            suffix-icon="el-icon-search"
            @input="filterStudents"
            style="width: 300px; margin-bottom: 10px;"
        />
      </el-form-item>

      <!-- 总学生列表 -->
      <el-form-item label="总学生列表">
        <el-table :data="filteredStudents" style="width: 100%" border max-height="300px">
          <el-table-column prop="username" label="学生姓名" width="180"></el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button @click="addStudent(scope.row)">添加</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <!-- 当前课程学生列表 -->
      <el-form-item label="课程学生列表">
        <el-table :data="enrolledStudents" style="width: 100%" border max-height="300px">
          <el-table-column prop="username" label="学生姓名" width="180"></el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="danger" @click="removeStudent(scope.row.studentId)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

    </el-form>

    <!-- 对话框底部按钮 -->
    <div slot="footer" class="dialog-footer">
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="updateCourse">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import {ref, onMounted, computed} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import api from "@/api/axios.js";

export default {
  name: "CourseManager",
  setup() {
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

    // 获取已选学生
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
        alert("该学生已在课程中，不能重复添加");
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

    return {
      courses,
      teachers,
      students,
      filterText,
      studentFilterText,
      filteredCourses,
      filteredStudents,
      createDialogVisible,
      editDialogVisible,
      enrolledStudents,
      form,
      selectedStudentId,
      filterStudents,
      resetForm,
      getCourses,
      filterCourses,
      openCreateDialog,
      createCourse,
      openEditDialog,
      updateCourse,
      deleteCourse,
      addStudent,
      removeStudent,
      getTeacherName,
      isTeacherRole,  // 新增 computed 属性
    };
  },
};
</script>

<style scoped>
.el-button {
  border-radius: 10px;
}
</style>
