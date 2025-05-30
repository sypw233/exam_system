<template>
  <el-container>
    <el-header>
      <!-- 角色筛选下拉框 -->
      <el-select v-model="selectedRole" placeholder="选择角色" @change="filterUsers"
                 style="width: 200px; margin-right: 10px;">
        <el-option label="全部" value=""></el-option>
        <el-option label="管理员" value="admin"></el-option>
        <el-option label="教师" value="teacher"></el-option>
        <el-option label="学生" value="student"></el-option>
      </el-select>

      <!-- 用户筛选输入框 -->
      <el-input
          v-model="filterText"
          placeholder="搜索用户名或邮箱"
          clearable
          suffix-icon="el-icon-search"
          style="width: 300px; margin-right: 10px;"
          @input="filterUsers"
      ></el-input>
    </el-header>

    <el-main>
      <el-table :data="filteredUsers" style="width: 100%">
        <el-table-column label="ID" prop="id"></el-table-column>
        <el-table-column label="用户名" prop="username"></el-table-column>
        <el-table-column label="角色" prop="role" :formatter="roleFormatter"></el-table-column>
        <el-table-column label="邮箱" prop="email"></el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" @click="editUser(row)">编辑</el-button>
            <el-button type="danger" @click="deleteUser(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>

    <!-- 更新用户对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑用户">
      <el-form :model="userForm" ref="userFormData" label-width="120px">
        <el-form-item label="ID" :rules="[{ required: false, trigger: 'blur' }]">
          <el-input disabled v-model="userForm.id"></el-input>
        </el-form-item>
        <el-form-item label="用户名" :rules="[{ required: true, message: '用户名是必填项', trigger: 'blur' }]">
          <el-input v-model="userForm.username"></el-input>
        </el-form-item>
        <el-form-item label="角色" :rules="[{ required: true, message: '角色是必填项', trigger: 'blur' }]">
          <el-input v-model="userForm.role"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" :rules="[{ required: true, message: '邮箱是必填项', trigger: 'blur' }]">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import {ref, onMounted} from 'vue';
import api from "@/api/axios.js";

export default {
  name: 'UserManager',
  setup() {
    const users = ref([]);
    const filteredUsers = ref([]);
    const filterText = ref('');
    const selectedRole = ref('');
    const dialogVisible = ref(false);
    const userForm = ref({
      id: null,
      username: '',
      role: '',
      email: ''
    });

    // 角色映射
    const roleMap = {
      admin: '管理员',
      teacher: '教师',
      student: '学生'
    };

    // 组件挂载时获取所有用户
    onMounted(() => {
      fetchUsers();
    });

    // 获取所有用户
    const fetchUsers = async () => {
      try {
        const response = await api('/users');
        users.value = response.data;
        filteredUsers.value = users.value;
      } catch (error) {
        console.error('获取用户失败:', error);
      }
    };

    // 格式化角色
    const roleFormatter = (row, column, cellValue) => {
      return roleMap[cellValue] || cellValue;
    };

    // 筛选用户
    const filterUsers = () => {
      filteredUsers.value = users.value.filter(user => {
        const matchesRole = !selectedRole.value || user.role === selectedRole.value;
        const matchesText = !filterText.value || user.username.toLowerCase().includes(filterText.value.toLowerCase()) || user.email.toLowerCase().includes(filterText.value.toLowerCase());
        return matchesRole && matchesText;
      });
    };

    // 编辑用户
    const editUser = (user) => {
      console.log(user)
      // 填充表单
      userForm.value.id = user.id;
      userForm.value.username = user.username;
      userForm.value.role = user.role;
      userForm.value.email = user.email;
      console.log(userForm.value)

      dialogVisible.value = true;  // 显示对话框
    };

    // 提交表单
    const submitForm = async () => {
      // console.log(userForm.value)
      try {
        if (userForm.value.id) {
          // 更新用户信息
          await api.put(`/users/${userForm.value.id}`, userForm.value);
        }
        dialogVisible.value = false;
        await fetchUsers();
      } catch (error) {
        alert('更新用户失败:'+error);
      }
    };

    // 删除用户
    const deleteUser = async (id) => {
      try {
        await api.delete(`/users/${id}`);
        await fetchUsers();
      } catch (error) {
        console.error('删除用户失败:', error);
      }
    };

    return {
      users,
      filteredUsers,
      filterText,
      selectedRole,
      dialogVisible,
      userForm,
      fetchUsers,
      roleFormatter,
      filterUsers,
      editUser,
      submitForm,
      deleteUser
    };
  }
};
</script>

<style scoped>
.el-button {
  border-radius: 10px;
}
</style>
