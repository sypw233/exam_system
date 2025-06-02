<template>
  <!-- 管理面板的外层容器 -->
  <el-container class="admin-dashboard-container">
    <!-- 头部区域，包含导航栏 -->
    <el-header class="admin-dashboard-header">
      <Navbar @toggle-sidebar="toggleSidebar" /> <!-- 导航栏组件 -->
    </el-header>

    <!-- 页面主体区域 -->
    <el-container class="dashboard-body">
      <!-- 侧边栏区域 -->
      <el-aside class="sidebar" :class="{ 'is-collapsed': isCollapsed }" :width="sidebarWidth">
        <Sidebar /> <!-- 侧边栏组件 -->
      </el-aside>

      <!-- 主内容区域，显示当前页面的内容 -->
      <el-main class="main-content">
        <router-view/> <!-- 路由视图，动态加载不同页面组件 -->
      </el-main>

      <!-- 返回顶部按钮，target 指定返回顶部的目标区域 -->
      <el-backtop target=".main-content" :visibility-height="100" :right="30" :bottom="30">
        <div class="backtop-button">
          <el-icon><ArrowUp /></el-icon>
        </div>
      </el-backtop>
    </el-container>
  </el-container>
</template>

<script>
import Navbar from '@/components/Navbar.vue';
import Sidebar from '@/components/Sidebar.vue';
import { ArrowUp } from '@element-plus/icons-vue';
import { ref, computed, onMounted, onUnmounted } from 'vue';

export default {
  name: 'AdminDashboard',
  components: {
    Navbar,  // 导航栏组件
    Sidebar, // 侧边栏组件
    ArrowUp, // 返回顶部图标
  },
  setup() {
    // 侧边栏折叠状态 - 从localStorage初始化
    const savedState = localStorage.getItem('sidebarCollapsed');
    const isCollapsed = ref(savedState === 'true');
    
    // 计算侧边栏宽度
    const sidebarWidth = computed(() => {
      return isCollapsed.value ? '64px' : '220px';
    });
    
    // 监听localStorage变化来更新侧边栏状态
    const handleStorageChange = (event) => {
      if (event.key === 'sidebarCollapsed') {
        isCollapsed.value = event.newValue === 'true';
      }
    };
    
    // 切换侧边栏折叠状态（现在主要由Navbar组件处理）
    const toggleSidebar = () => {
      // 这个方法现在主要用于兼容性，实际逻辑在Navbar组件中
      // 如果需要，可以在这里添加额外的逻辑
    };
    
    // 组件挂载时监听存储变化
    onMounted(() => {
      window.addEventListener('storage', handleStorageChange);
    });

    // 组件卸载时移除事件监听
    onUnmounted(() => {
      window.removeEventListener('storage', handleStorageChange);
    });
    
    return {
      isCollapsed,
      sidebarWidth,
      toggleSidebar
    };
  },
};
</script>

<style scoped>
/* 全局样式重置 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 管理面板的外容器 */
.admin-dashboard-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

/* 头部样式 */
.admin-dashboard-header {
  padding: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 1000;
  background: #fff;
}

/* 主体容器 */
.dashboard-body {
  height: calc(100vh - 60px);
}

/* 侧边栏样式 */
.sidebar {
  background: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  transition: all 0.3s ease;
  overflow: hidden;
}

/* 折叠状态的侧边栏 */
.sidebar.is-collapsed {
  width: 64px !important;
}

/* 折叠状态下隐藏文本 */
.sidebar.is-collapsed :deep(.el-menu) {
  width: 64px !important;
}

.sidebar.is-collapsed :deep(.el-menu-item span),
.sidebar.is-collapsed :deep(.el-sub-menu__title span) {
  display: none;
}

/* 折叠状态下调整图标位置 */
.sidebar.is-collapsed :deep(.el-menu-item),
.sidebar.is-collapsed :deep(.el-sub-menu__title) {
  padding: 0 !important;
  text-align: center;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 24px;
  height: calc(100vh - 60px);
  overflow-y: auto;
  background: #f0f2f5;
  position: relative;
}

/* 内容包装器 */
.content-wrapper {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  min-height: calc(100vh - 148px);
  transition: all 0.3s ease;
}

.content-wrapper:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

/* 返回顶部按钮样式 */
.backtop-button {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.backtop-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.6);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .content-wrapper {
    padding: 16px;
    border-radius: 6px;
  }
}

/* 路由过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滚动条样式 */
.main-content::-webkit-scrollbar {
  width: 6px;
}

.main-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
  transition: background 0.3s ease;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
