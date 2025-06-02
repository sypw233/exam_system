<template>
  <div class="user-manager-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <el-icon class="title-icon"><User /></el-icon>
          用户管理
        </h2>
        <p class="page-description">管理系统中的所有用户信息</p>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <el-card class="search-card" shadow="never">
        <div class="search-content">
          <div class="search-left">
            <el-input
                v-model="filterText"
                placeholder="搜索用户名或邮箱"
                clearable
                class="search-input"
                @input="filterUsers"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            
            <el-select 
              v-model="selectedRole" 
              placeholder="选择角色" 
              clearable
              @change="filterUsers"
              class="role-select"
            >
              <el-option label="全部角色" value=""></el-option>
              <el-option label="管理员" value="admin">
                <el-icon><Star /></el-icon>
                <span style="margin-left: 8px;">管理员</span>
              </el-option>
              <el-option label="教师" value="teacher">
                <el-icon><UserFilled /></el-icon>
                <span style="margin-left: 8px;">教师</span>
              </el-option>
              <el-option label="学生" value="student">
                <el-icon><User /></el-icon>
                <span style="margin-left: 8px;">学生</span>
              </el-option>
            </el-select>
          </div>
          
          <div class="search-right">
            <el-button type="primary" @click="fetchUsers" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新数据
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 用户列表 -->
    <div class="table-section">
      <el-card class="table-card" shadow="never">
        <template #header>
          <div class="table-header">
            <span class="table-title">用户列表</span>
            <el-tag type="info" size="small">共 {{ filteredUsers.length }} 个用户</el-tag>
          </div>
        </template>
        
        <el-table 
          :data="filteredUsers" 
          v-loading="loading"
          class="user-table"
          stripe
          :header-cell-style="{ background: '#f8f9fa', color: '#495057' }"
        >
          <el-table-column label="ID" prop="id" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small" type="info">{{ row.id }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="用户信息" min-width="200">
            <template #default="{ row }">
              <div class="user-info-cell">
                <div class="user-avatar">
                  <el-icon size="20"><Avatar /></el-icon>
                </div>
                <div class="user-details">
                  <div class="username">{{ row.username }}</div>
                  <div class="email">{{ row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="角色" prop="role" width="120" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getRoleTagType(row.role)" 
                size="small" 
                effect="light"
                class="role-tag"
              >
                {{ roleFormatter(row, null, row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="editUser(row)"
                  :icon="Edit"
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="deleteUser(row.id)"
                  :icon="Delete"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        

      </el-card>
    </div>

    <!-- 编辑用户对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="编辑用户信息" 
      width="500px"
      :close-on-click-modal="false"
      class="edit-dialog"
    >
      <el-form 
        :model="userForm" 
        ref="userFormRef" 
        label-width="100px"
        :rules="formRules"
        class="edit-form"
      >
        <el-form-item label="用户ID">
          <el-input v-model="userForm.id" disabled class="disabled-input">
            <template #prefix>
              <el-icon><Key /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin">
              <el-icon><Star /></el-icon>
              <span style="margin-left: 8px;">管理员</span>
            </el-option>
            <el-option label="教师" value="teacher">
              <el-icon><UserFilled /></el-icon>
              <span style="margin-left: 8px;">教师</span>
            </el-option>
            <el-option label="学生" value="student">
              <el-icon><User /></el-icon>
              <span style="margin-left: 8px;">学生</span>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱地址">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" :icon="Close">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting" :icon="Check">
            {{ submitting ? '保存中...' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, onActivated, watch, h } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRoute } from 'vue-router';
import api from "@/api/axios.js";
import {
  User,
  Search,
  Refresh,
  Edit,
  Delete,
  Avatar,
  Star,
  UserFilled,
  Key,
  Message,
  Close,
  Check
} from '@element-plus/icons-vue';

export default {
  name: 'UserManager',
  components: {
    User,
    Search,
    Refresh,
    Edit,
    Delete,
    Avatar,
    Star,
    UserFilled,
    Key,
    Message,
    Close,
    Check
  },
  setup() {
    const route = useRoute();
    const users = ref([]);
    const filteredUsers = ref([]);
    const filterText = ref('');
    

    const selectedRole = ref('');
    const dialogVisible = ref(false);
    const loading = ref(false);
    const submitting = ref(false);
    const userFormRef = ref(null);
    
    const userForm = ref({
      id: null,
      username: '',
      role: '',
      email: ''
    });

    // 表单验证规则
    const formRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      role: [
        { required: true, message: '请选择用户角色', trigger: 'change' }
      ],
      email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ]
    };

    // 角色映射
    const roleMap = {
      admin: '管理员',
      teacher: '教师',
      student: '学生'
    };

    /**
     * 组件挂载时获取所有用户
     */
    onMounted(() => {
      fetchUsers();
    });

    /**
     * 组件激活时重新获取数据（解决路由切换缓存问题）
     */
    onActivated(() => {
      fetchUsers();
    });

    /**
     * 监听路由变化，确保切换到此页面时重新加载数据
     */
    watch(() => route.path, (newPath) => {
      if (newPath === '/dashboard/user') {
        fetchUsers();
      }
    });

    /**
     * 获取所有用户
     */
    const fetchUsers = async () => {
      loading.value = true;
      try {
        const response = await api.get('/users');
        
        // 优先处理直接数组格式
        if (Array.isArray(response.data)) {
          users.value = response.data;
        } else if (response.data && response.data.content) {
          // 兼容分页响应格式
          users.value = response.data.content;
        } else {
          users.value = [];
        }
        
        filteredUsers.value = users.value;
        console.log('用户数据加载成功:', users.value.length, '个用户');
        ElMessage.success(`用户数据加载成功，共 ${users.value.length} 个用户`);
      } catch (error) {
        console.error('获取用户失败:', error);
        ElMessage.error('获取用户数据失败');
      } finally {
        loading.value = false;
      }
    };

    /**
     * 格式化角色显示
     */
    const roleFormatter = (row, column, cellValue) => {
      return roleMap[cellValue] || cellValue;
    };

    /**
     * 获取角色标签类型
     */
    const getRoleTagType = (role) => {
      const typeMap = {
        admin: 'danger',
        teacher: 'success',
        student: 'info'
      };
      return typeMap[role] || 'info';
    };

    /**
     * 获取角色图标
     */
    const getRoleIcon = (role) => {
      const iconMap = {
        admin: h(Star),
        teacher: h(UserFilled),
        student: h(User)
      };
      return iconMap[role] || h(User);
    };

    /**
     * 筛选用户
     */
    const filterUsers = () => {
      filteredUsers.value = users.value.filter(user => {
        const matchesRole = !selectedRole.value || user.role === selectedRole.value;
        const matchesText = !filterText.value || 
          user.username.toLowerCase().includes(filterText.value.toLowerCase()) || 
          user.email.toLowerCase().includes(filterText.value.toLowerCase());
        return matchesRole && matchesText;
      });
    };

    /**
     * 编辑用户
     */
    const editUser = (user) => {
      userForm.value = {
        id: user.id,
        username: user.username,
        role: user.role,
        email: user.email
      };
      dialogVisible.value = true;
    };

    /**
     * 提交表单
     */
    const submitForm = async () => {
      if (!userFormRef.value) return;
      
      try {
        await userFormRef.value.validate();
        submitting.value = true;
        
        await api.put(`/users/${userForm.value.id}`, userForm.value);
        
        ElMessage.success('用户信息更新成功');
        dialogVisible.value = false;
        await fetchUsers();
      } catch (error) {
        if (error.errors) {
          // 表单验证错误
          return;
        }
        console.error('更新用户失败:', error.message || error);
        ElMessage.error('更新用户信息失败');
      } finally {
        submitting.value = false;
      }
    };

    /**
     * 删除用户
     */
    const deleteUser = async (id) => {
      try {
        await ElMessageBox.confirm(
          '此操作将永久删除该用户，是否继续？',
          '删除确认',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning',
            customClass: 'delete-confirm-dialog'
          }
        );
        
        await api.delete(`/users/${id}`);
        ElMessage.success('用户删除成功');
        await fetchUsers();
      } catch (error) {
        if (error === 'cancel') {
          return;
        }
        console.error('删除用户失败:', error.message || error);
        ElMessage.error('删除用户失败');
      }
    };



    return {
      users,
      filteredUsers,
      filterText,
      selectedRole,
      dialogVisible,
      loading,
      submitting,
      userForm,
      userFormRef,
      formRules,
      fetchUsers,
      roleFormatter,
      getRoleTagType,
      getRoleIcon,
      filterUsers,
      editUser,
      submitForm,
      deleteUser
    };
  },
};
</script>

<style scoped>
.user-manager-container {
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

.role-select {
  width: 200px;
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

.user-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.email {
  color: #6c757d;
  font-size: 12px;
}

/* 角色标签 */
.role-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.role-icon {
  font-size: 12px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 编辑对话框 */
:deep(.edit-dialog) {
  border-radius: 12px;
}

:deep(.edit-dialog .el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #e9ecef;
}

:deep(.edit-dialog .el-dialog__body) {
  padding: 20px;
}

.edit-form {
  padding: 0;
}

.disabled-input {
  background: #f8f9fa;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 20px 0;
  border-top: 1px solid #e9ecef;
}



/* 删除确认对话框 */
:deep(.delete-confirm-dialog) {
  border-radius: 12px;
}

:deep(.delete-confirm-dialog .el-message-box__header) {
  padding: 20px 20px 10px;
}

:deep(.delete-confirm-dialog .el-message-box__content) {
  padding: 10px 20px 20px;
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
  
  .search-input,
  .role-select {
    width: 100%;
  }
  
  .user-info-cell {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>
