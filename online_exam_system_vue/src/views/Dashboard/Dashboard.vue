<template>
  <!-- 管理面板的外层容器 -->
  <el-container class="admin-dashboard-container">
    <!-- 头部区域，包含导航栏 -->
    <el-header class="admin-dashboard-header">
      <Navbar/> <!-- 导航栏组件 -->
    </el-header>

    <!-- 页面主体区域 -->
    <el-container>
      <!-- 侧边栏区域 -->
      <el-aside class="sidebar">
        <Sidebar/> <!-- 侧边栏组件 -->
      </el-aside>

      <!-- 主内容区域，显示当前页面的内容 -->
      <el-main class="main-content">
        <router-view/> <!-- 路由视图，动态加载不同页面组件 -->
      </el-main>

      <!-- 返回顶部按钮，target 指定返回顶部的目标区域 -->
      <el-backtop target=".main-content" :visibility-height="100"/>
    </el-container>
  </el-container>
</template>

<script>
import Navbar from '@/components/Navbar.vue';
import Sidebar from '@/components/Sidebar.vue';

export default {
  name: 'AdminDashboard',
  components: {
    Navbar,  // 导航栏组件
    Sidebar, // 侧边栏组件
  },
  setup() {
    return {};
  },
};
</script>

<style scoped>
/* html 和 body 样式设置，确保页面充满全屏 */
html {
  overflow: hidden; /* 禁止滚动条出现 */
  height: 100%;     /* 确保页面占满整个视口高度 */
}

body {
  margin: 0;         /* 去除默认的 body margin */
  padding: 0;        /* 去除默认的 body padding */
  height: 100%;      /* 确保 body 高度充满视口 */
  overflow: hidden;  /* 禁止 body 滚动条出现 */
}

/* 管理面板的外容器，确保不出现滚动条 */
.admin-dashboard-container {
  overflow: hidden;  /* 禁止外层容器出现滚动条 */
}

/* 侧边栏样式，设置宽度为200px */
.sidebar {
  width: 200px;  /* 侧边栏宽度设为200px */
}

/* 主内容区域，设置高度和滚动 */
.main-content {
  flex: 1;                  /* 主内容区域占据剩余空间 */
  padding: 20px;            /* 内边距为20px */
  height: calc(100vh - 64px); /* 调整高度，减去头部区域的高度（假设头部高度为64px） */
  overflow-y: auto;         /* 如果内容超过区域高度，则显示纵向滚动条 */
}
.admin-dashboard-header{
  padding: 0;
}
</style>
