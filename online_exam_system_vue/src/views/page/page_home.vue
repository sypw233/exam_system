<template>
  <div class="course-manager">
    <el-row>
      <el-col :span="24">
        <el-button type="primary" @click="openCreateDialog">添加课程</el-button>
      </el-col>
    </el-row>

    <!-- 课程列表 -->
    <el-table :data="courses" style="width: 100%" border>
      <el-table-column prop="courseName" label="课程名称" width="180"></el-table-column>
      <el-table-column label="授课教师" width="180">
        <template #default="scope">
          {{ getTeacherName(scope.row.teacherId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteCourse(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建课程对话框 -->
    <el-dialog v-model="createDialogVisible" title="添加课程" @close="resetForm">
      <el-form :model="form" ref="formData" label-width="120px">
        <el-form-item label="课程名称" prop="courseName"
                      :rules="[{ required: true, message: '请输入课程名称', trigger: 'blur' }]">
          <el-input v-model="form.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId"
                      :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
          <el-select v-model="form.teacherId" placeholder="请选择授课教师">
            <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.username"
                       :value="teacher.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
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
        <el-form-item label="授课教师" prop="teacherId"
                      :rules="[{ required: true, message: '请选择授课教师', trigger: 'change' }]">
          <el-select v-model="form.teacherId" placeholder="请选择授课教师">
            <el-option v-for="teacher in teachers" :key="teacher.id" :label="teacher.username"
                       :value="teacher.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <el-divider></el-divider>
      <div class="student-management">
        <h3>选课学生</h3>
        <el-table :data="enrolledStudents" style="width: 100%" border>
          <el-table-column prop="username" label="学生姓名" width="180"></el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="danger" @click="removeStudent(scope.row.studentId)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加学生 -->
        <el-select v-model="selectedStudentId" placeholder="选择学生" @change="addStudent">
          <el-option v-for="student in students" :key="student.id" :label="student.username"
                     :value="student.id"></el-option>
        </el-select>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateCourse">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {ref, onMounted} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import api from "@/api/axios.js";

export default {
  name: "CourseManager",
  setup() {
    // 数据
    const courses = ref([]);
    const teachers = ref([]);
    const students = ref([]);
    const enrolledStudents = ref([]);
    const form = ref({
      courseName: "",
      teacherId: "",
    });
    const selectedStudentId = ref(null);

    const createDialogVisible = ref(false);
    const editDialogVisible = ref(false);
    const currentCourseId = ref(null);

    // 获取课程列表
    const getCourses = async () => {
      try {
        const response = await api.get("/courses");
        // courses.value.id  = response.data.courseId;
        // courses.value.studentId  = response.data.studentId;
        courses.value = response.data;
        console.log(courses.value)
      } catch (error) {
        ElMessage.error("获取课程列表失败");
      }
    };

    // 获取教师列表
    const getTeachers = async () => {
      try {
        const response = await api.get("/users/teachers");
        teachers.value = response.data;
      } catch (error) {
        ElMessage.error("获取教师列表失败");
      }
    };

    // 获取学生列表
    const getStudents = async () => {
      try {
        const response = await api.get("/users/students");
        students.value = response.data;
      } catch (error) {
        ElMessage.error("获取学生列表失败");
      }
    };

    // 获取已选学生列表
    const getEnrolledStudents = async (courseId) => {
      try {
        const response = await api.get(`/course-selections/course/${courseId}`);
        // 将已选学生的 studentId 替换为学生的用户名
        enrolledStudents.value = response.data.map(item => {
          const student = students.value.find(s => s.id === item.studentId);
          return {...item, username: student ? student.username : "未知"};
        });
      } catch (error) {
        ElMessage.error("获取选课学生失败");
      }
    };

    // 打开编辑课程对话框
    const openEditDialog = async (course) => {
      form.value = {...course};
      currentCourseId.value = course.id;
      editDialogVisible.value = true;

      // 获取已选学生列表
      await getEnrolledStudents(course.id);
    };

    // 打开添加课程对话框
    const openCreateDialog = () => {
      form.value = {courseName: "", teacherId: ""}; // 重置表单
      createDialogVisible.value = true;
    };

    // 创建课程
    const createCourse = async () => {
      if (!form.value.courseName || !form.value.teacherId) {
        ElMessage.error("请填写所有字段");
        return;
      }
      try {
        await api.post("/courses", form.value);
        ElMessage.success("课程创建成功");
        createDialogVisible.value = false;
        await getCourses();
      } catch (error) {
        ElMessage.error("创建课程失败");
      }
    };

    // 更新课程
    const updateCourse = async () => {
      if (!form.value.courseName || !form.value.teacherId) {
        ElMessage.error("请填写所有字段");
        return;
      }
      try {
        await api.put(`/courses/${currentCourseId.value}`, form.value);
        ElMessage.success("课程更新成功");
        editDialogVisible.value = false;
        await getCourses();
      } catch (error) {
        ElMessage.error("更新课程失败");
      }
    };

    // 删除课程
    const deleteCourse = async (courseId) => {
      try {
        await ElMessageBox.confirm("确认删除此课程?", "警告", {
          type: "warning",
        });
        await api.delete(`/courses/${courseId}`);
        ElMessage.success("课程删除成功");
        await getCourses();
      } catch (error) {
        ElMessage.error("删除课程失败");
      }
    };

    // 添加学生
    const addStudent = async () => {
      if (!selectedStudentId.value) {
        ElMessage.error("请选择学生");
        return;
      }
      try {
        await api.post(`/course-selections`, {
          courseId: currentCourseId.value,
          studentId: selectedStudentId.value,
        });
        ElMessage.success("学生添加成功");
        await getEnrolledStudents(currentCourseId.value);  // 更新选课学生列表
      } catch (error) {
        ElMessage.error("添加学生失败");
      }
    };

    // 移除学生
    const removeStudent = async (studentId) => {
      try {
        console.log("delete"+currentCourseId.value+",,,"+studentId)
        await api.delete(`/course-selections/${currentCourseId.value}/students/${studentId}`);
        ElMessage.success("学生移除成功");
        await getEnrolledStudents(currentCourseId.value);  // 更新选课学生列表
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

    // 初始化
    onMounted(() => {
      getCourses();
      getTeachers();
      getStudents();
    });

    return {
      courses,
      teachers,
      students,
      enrolledStudents,
      form,
      selectedStudentId,
      createDialogVisible,
      editDialogVisible,
      currentCourseId,
      resetForm,
      getCourses,
      openCreateDialog,
      createCourse,
      openEditDialog,
      updateCourse,
      deleteCourse,
      addStudent,
      removeStudent,
      getTeacherName,
    };
  },
};
</script>

<style scoped>
.course-manager {
  padding: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
