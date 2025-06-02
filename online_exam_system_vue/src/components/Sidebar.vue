<template>
  <div class="sidebar-container">
    <!-- 侧边栏菜单 -->
    <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        mode="vertical"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
    >
      <!-- 管理员菜单 -->
      <template v-if="role === 'ADMIN'">
        <el-menu-item index="/dashboard/user" class="menu-item">
          <el-icon class="menu-icon"><User /></el-icon>
          <span class="menu-title">用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/dashboard/course" class="menu-item">
          <el-icon class="menu-icon"><Reading /></el-icon>
          <span class="menu-title">课程管理</span>
        </el-menu-item>
        
        <el-sub-menu index="exam" class="sub-menu">
          <template #title>
            <el-icon class="menu-icon"><Document /></el-icon>
            <span class="menu-title">考试管理</span>
          </template>
          <el-menu-item index="/dashboard/exam" class="sub-menu-item">
            <el-icon class="sub-menu-icon"><DocumentCopy /></el-icon>
            <span>试卷管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/exam-submission" class="sub-menu-item">
            <el-icon class="sub-menu-icon"><Finished /></el-icon>
            <span>交卷管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/dashboard/question" class="menu-item">
          <el-icon class="menu-icon"><EditPen /></el-icon>
          <span class="menu-title">题目管理</span>
        </el-menu-item>
      </template>

      <!-- 教师菜单 -->
      <template v-if="role === 'TEACHER'">
        <el-menu-item index="/dashboard/course" class="menu-item">
          <el-icon class="menu-icon"><Reading /></el-icon>
          <span class="menu-title">课程管理</span>
        </el-menu-item>
        
        <el-sub-menu index="exam" class="sub-menu">
          <template #title>
            <el-icon class="menu-icon"><Document /></el-icon>
            <span class="menu-title">考试管理</span>
          </template>
          <el-menu-item index="/dashboard/exam" class="sub-menu-item">
            <el-icon class="sub-menu-icon"><DocumentCopy /></el-icon>
            <span>试卷管理</span>
          </el-menu-item>
          <el-menu-item index="/dashboard/exam-submission" class="sub-menu-item">
            <el-icon class="sub-menu-icon"><Finished /></el-icon>
            <span>交卷管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/dashboard/question" class="menu-item">
          <el-icon class="menu-icon"><EditPen /></el-icon>
          <span class="menu-title">题目管理</span>
        </el-menu-item>
      </template>
    </el-menu>

    <!-- 侧边栏底部装饰 -->
    <div class="sidebar-footer">
      <div class="footer-decoration">
        <div class="decoration-line"></div>
        <div class="decoration-text">管理后台</div>
        <div class="decoration-line"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import {
  User,
  Reading,
  Document,
  DocumentCopy,
  Finished,
  EditPen
} from '@element-plus/icons-vue';

export default {
  name: 'Sidebar',
  components: {
    User,
    Reading,
    Document,
    DocumentCopy,
    Finished,
    EditPen
  },
  setup() {
    const route = useRoute();
    const role = ref(localStorage.getItem('role')?.toUpperCase() || 'STUDENT'); // 获取当前用户角色
    const activeMenu = ref(route.path); // 当前激活的菜单项

    // 侧边栏折叠状态 - 使用响应式ref
    const savedState = localStorage.getItem('sidebarCollapsed');
    const isCollapsed = ref(savedState === 'true');

    /**
     * 监听路由变化来更新激活的菜单项
     */
    const updateActiveMenu = () => {
      activeMenu.value = route.path;
      console.log('Active menu updated to:', route.path); // 调试日志
    };

    /**
     * 监听localStorage变化来更新侧边栏状态
     */
    const handleStorageChange = (event) => {
      if (event.key === 'sidebarCollapsed') {
        isCollapsed.value = event.newValue === 'true';
      }
    };

    // 组件挂载时更新激活菜单并监听存储变化
    onMounted(() => {
      updateActiveMenu();
      window.addEventListener('storage', handleStorageChange);
    });

    // 组件卸载时移除事件监听
    onUnmounted(() => {
      window.removeEventListener('storage', handleStorageChange);
    });

    // 监听路由变化，使用immediate选项确保立即执行
    watch(() => route.path, updateActiveMenu, { immediate: true });

    return {
      role,
      activeMenu,
      isCollapsed,
    };
  },
};
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  background: #304156;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* 侧边栏菜单样式 */
.sidebar-menu {
  flex: 1;
  border: none;
  width: 100%;
  transition: width 0.3s ease;
  background: #304156;
}

/* 菜单项样式 */
:deep(.menu-item) {
  height: 56px;
  line-height: 56px;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

:deep(.menu-item:hover) {
  background: rgba(64, 158, 255, 0.1) !important;
  transform: translateX(4px);
}

:deep(.menu-item.is-active) {
  background: linear-gradient(135deg, #409EFF 0%, #36a3f7 100%) !important;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

:deep(.menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: #fff;
  border-radius: 0 2px 2px 0;
}

/* 子菜单样式 */
:deep(.sub-menu) {
  margin: 4px 12px;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.sub-menu .el-sub-menu__title) {
  height: 56px;
  line-height: 56px;
  border-radius: 8px;
  transition: all 0.3s ease;
  color: #bfcbd9 !important;
  background: transparent !important;
}

:deep(.sub-menu .el-sub-menu__title:hover) {
  background: rgba(64, 158, 255, 0.1) !important;
  transform: translateX(4px);
}

:deep(.sub-menu.is-opened .el-sub-menu__title) {
  background: rgba(64, 158, 255, 0.1) !important;
  color: #409EFF !important;
}

/* 子菜单项样式 */
:deep(.sub-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.sub-menu-item:hover) {
  background: rgba(64, 158, 255, 0.15) !important;
  transform: translateX(8px);
}

:deep(.sub-menu-item.is-active) {
  background: linear-gradient(135deg, #409EFF 0%, #36a3f7 100%) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

/* 图标样式 */
.menu-icon {
  font-size: 18px;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.sub-menu-icon {
  font-size: 16px;
  margin-right: 8px;
  opacity: 0.8;
}

/* 菜单标题样式 */
.menu-title {
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

/* 侧边栏底部装饰 */
.sidebar-footer {
  padding: 20px;
  background: rgba(0, 0, 0, 0.1);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.decoration-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
}

.decoration-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 500;
  white-space: nowrap;
}

/* 滚动条样式 */
.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar-container {
    width: 200px;
  }
  
  .sidebar-menu {
    width: 200px;
  }
  
  :deep(.menu-item) {
    height: 48px;
    line-height: 48px;
    margin: 2px 8px;
  }
  
  .menu-icon {
    font-size: 16px;
    margin-right: 8px;
  }
  
  .menu-title {
    font-size: 13px;
  }
}

/* 折叠状态下的样式优化 */
:deep(.el-menu--collapse .menu-item) {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 !important;
}

:deep(.el-menu--collapse .menu-icon) {
  margin-right: 0 !important;
  font-size: 20px;
}

:deep(.el-menu--collapse .sub-menu .el-sub-menu__title) {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 !important;
}

:deep(.el-menu--collapse .sub-menu .el-sub-menu__title .menu-icon) {
  margin-right: 0 !important;
  font-size: 20px;
}

/* 折叠状态下隐藏箭头图标 */
:deep(.el-menu--collapse .el-sub-menu__icon-arrow) {
  display: none;
}

/* 动画效果 */
:deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
  transition: transform 0.3s ease;
}

:deep(.sub-menu.is-opened .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transform: rotateZ(180deg);
}
</style>
