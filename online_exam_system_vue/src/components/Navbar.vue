<template>
  <div class="navbar-container">
    <!-- 左侧品牌区域 -->
    <div class="navbar-brand">
      <!-- 侧边栏切换按钮 -->
      <div class="sidebar-toggle" @click="toggleSidebar">
        <el-icon size="20">
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
      <div class="brand-icon">
        <el-icon size="24"><Document /></el-icon>
      </div>
      <h1 class="brand-title">在线考试系统</h1>
    </div>

    <!-- 右侧用户信息区域 -->
    <div class="navbar-user">
      <!-- 用户角色标签 -->
      <el-tag 
        :type="tagType" 
        effect="light" 
        round 
        size="large" 
        class="user-role-tag"
      >
        <el-icon class="tag-icon"><User /></el-icon>
        {{ role }}
      </el-tag>

      <!-- 用户下拉菜单 -->
      <el-dropdown trigger="click" class="user-dropdown">
        <div class="user-info">
          <div class="user-avatar">
            <el-icon size="20"><Avatar /></el-icon>
          </div>
          <span class="username">{{ username || "用户" }}</span>
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="user-dropdown-menu">
            <el-dropdown-item class="dropdown-item" @click="logout">
              <el-icon class="item-icon"><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import router from "@/router/index.js";
import { 
  Document, 
  User, 
  Avatar, 
  ArrowDown, 
  SwitchButton,
  Expand,
  Fold
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

export default {
  name: 'Navbar',
  components: {
    Document,
    User,
    Avatar,
    ArrowDown,
    SwitchButton,
    Expand,
    Fold
  },
  setup(props, { emit }) {
    const activeIndex = ref('0');
    
    // 侧边栏折叠状态 - 使用响应式ref
    const savedState = localStorage.getItem('sidebarCollapsed');
    const isCollapsed = ref(savedState === 'true');

    /**
     * 切换侧边栏显示/隐藏
     */
    const toggleSidebar = () => {
      // 直接更新本地状态
      isCollapsed.value = !isCollapsed.value;
      // 保存状态到本地存储
      localStorage.setItem('sidebarCollapsed', isCollapsed.value.toString());
      // 触发存储事件，通知其他组件状态变化
      window.dispatchEvent(new StorageEvent('storage', {
        key: 'sidebarCollapsed',
        newValue: isCollapsed.value.toString()
      }));
      // 同时触发父组件事件（保持兼容性）
      emit('toggle-sidebar');
    };

    /**
     * 监听localStorage变化来更新侧边栏状态
     */
    const handleStorageChange = (event) => {
      if (event.key === 'sidebarCollapsed') {
        isCollapsed.value = event.newValue === 'true';
      }
    };

    // 组件挂载时监听存储变化
    onMounted(() => {
      window.addEventListener('storage', handleStorageChange);
    });

    // 组件卸载时移除事件监听
    onUnmounted(() => {
      window.removeEventListener('storage', handleStorageChange);
    });

    /**
     * 用户退出登录功能
     */
    const logout = async () => {
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '退出确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            customClass: 'logout-confirm-dialog'
          }
        );
        
        // 清除本地存储的用户信息
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        
        ElMessage.success('退出成功');
        
        // 跳转到登录页面
        router.push('/');
      } catch {
        // 用户取消退出
      }
    };

    return {
      activeIndex,
      logout,
      toggleSidebar,
      isCollapsed
    };
  },
  data() {
    return {
      username: localStorage.getItem('username') || '',
      role: localStorage.getItem('role') === 'TEACHER' ? '教师' :
          localStorage.getItem('role') === 'ADMIN' ? '管理员' : '学生',
    };
  },
  computed: {
    /**
     * 根据用户角色返回对应的标签类型
     */
    tagType() {
      const roleMap = {
        '教师': 'success',
        '管理员': 'danger',
        '学生': 'info'
      };
      return roleMap[this.role] || 'info';
    }
  }
};
</script>

<style scoped>
.navbar-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e8eaec;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 左侧品牌区域 */
.navbar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 侧边栏切换按钮 */
.sidebar-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
  margin-right: 4px;
}

.sidebar-toggle:hover {
  background-color: #f0f2f5;
  color: #409EFF;
}

.brand-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.brand-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 右侧用户信息区域 */
.navbar-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 用户角色标签 */
.user-role-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  font-weight: 500;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tag-icon {
  font-size: 14px;
}

/* 用户下拉菜单 */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.user-info:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-arrow {
  font-size: 12px;
  color: #8c939d;
  transition: transform 0.3s ease;
}

.user-info:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* 下拉菜单样式 */
:deep(.user-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: 1px solid #e9ecef;
  padding: 8px 0;
}

:deep(.dropdown-item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-size: 14px;
  color: #2c3e50;
  transition: all 0.3s ease;
}

:deep(.dropdown-item:hover) {
  background: #f8f9fa;
  color: #e74c3c;
}

.item-icon {
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 16px;
  }
  
  .brand-title {
    font-size: 18px;
  }
  
  .user-role-tag {
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .username {
    max-width: 80px;
  }
}

/* 退出确认对话框样式 */
:deep(.logout-confirm-dialog) {
  border-radius: 12px;
}

:deep(.logout-confirm-dialog .el-message-box__header) {
  padding: 20px 20px 10px;
}

:deep(.logout-confirm-dialog .el-message-box__content) {
  padding: 10px 20px 20px;
}
</style>
