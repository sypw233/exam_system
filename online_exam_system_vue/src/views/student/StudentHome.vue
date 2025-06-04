<template>
  <el-container id="home">
    <!-- 导航栏 -->
    <el-header>
      <el-menu
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          :ellipsis="false"
      >
        <!-- 首页 -->
        <el-menu-item index="0" @click="showPage('StudentExams')">
          <h1>在线考试系统</h1>
        </el-menu-item>

        <!-- 考试记录 -->
        <el-menu-item index="1" @click="showPage('StudentExamSubmission')">考试记录</el-menu-item>

        <!-- 我的资料和退出 -->
        <el-sub-menu index="2">
          <template #title>{{ username || "我的" }}</template>
          <el-menu-item @click="logout">退出</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-header>


<el-main class="el-main1">
  <component :is="currentPageComponent" />
  <el-backtop target=".el-main1" :visibility-height="100" :right="100" :bottom="100" />
</el-main>

  </el-container>
</template>

<script>
import {ref} from 'vue';
import StudentExamSubmission from "@/views/student/StudentExamSubmission.vue";
import StudentExams from "@/views/student/StudentExams.vue";

export default {
  name: 'StudentHome',
  components: {StudentExams, StudentExamSubmission},
  setup() {
    const activeIndex = ref('0');
    return {
      activeIndex,

    };
  },
  data() {
    return {
      // 默认加载考试系统页面
      currentPage: 'StudentExams',
      username: localStorage.getItem('username') || '',
    };
  },
  computed: {
    currentPageComponent() {
      // 根据 currentPage 的值返回对应的组件
      return this.currentPage === 'StudentExams' ? StudentExams : StudentExamSubmission;
    },
  },
  methods: {
    showPage(page) {
      this.currentPage = page;
    },
    logout() {
      if (confirm('Logging out?')) {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        this.$router.push('/');
      }
    },
  },
};
</script>

<style scoped>
/* 主容器样式 */
#home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 头部导航栏样式 */
.el-header {
  padding: 0;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border-radius: 0 0 16px 16px;
  margin-bottom: 24px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* 导航菜单样式 */
:deep(.el-menu-demo) {
  border-bottom: none;
  background: white;
  border-radius: 0 0 16px 16px;
  padding: 0 32px;
  height: 70px;
  line-height: 70px;
}

/* 导航菜单项样式 */
:deep(.el-menu--horizontal > .el-menu-item) {
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
  height: 70px;
  line-height: 70px;
  font-weight: 500;
  color: #606266;
}

:deep(.el-menu--horizontal > .el-menu-item:nth-child(1)) {
  margin-right: auto;
}

:deep(.el-menu--horizontal > .el-menu-item:hover) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(102, 177, 255, 0.1));
  border-bottom-color: #409eff;
  color: #409eff;
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15), rgba(102, 177, 255, 0.15));
  border-bottom-color: #409eff;
  color: #409eff;
}

/* 系统标题样式 */
:deep(.el-menu-item h1) {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover h1) {
  transform: scale(1.02);
}

/* 子菜单样式 */
:deep(.el-sub-menu) {
  height: 70px;
  line-height: 70px;
}

:deep(.el-sub-menu__title) {
  height: 70px;
  line-height: 70px;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
  font-weight: 500;
  color: #606266;
}

:deep(.el-sub-menu__title:hover) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(102, 177, 255, 0.1));
  border-bottom-color: #409eff;
  color: #409eff;
}

:deep(.el-sub-menu.is-active .el-sub-menu__title) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15), rgba(102, 177, 255, 0.15));
  border-bottom-color: #409eff;
  color: #409eff;
}

/* 下拉菜单样式 */
:deep(.el-menu--popup) {
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.05);
  padding: 8px 0;
}

:deep(.el-menu--popup .el-menu-item) {
  margin: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-menu--popup .el-menu-item:hover) {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
}

/* 主内容区域样式 */
.el-main1 {
  min-height: calc(100vh - 94px);
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: transparent;
}

/* 内容包装器 */
:deep(.el-main1 > *) {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  transition: all 0.3s ease;
}

:deep(.el-main1 > *:hover) {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 返回顶部按钮样式 */
:deep(.el-backtop) {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-radius: 50%;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

:deep(.el-backtop:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .el-main1 {
    padding: 20px;
  }
  
  :deep(.el-main1 > *) {
    padding: 24px;
  }
}

@media (max-width: 768px) {
  :deep(.el-menu-demo) {
    padding: 0 16px;
    height: 60px;
    line-height: 60px;
  }
  
  :deep(.el-menu--horizontal > .el-menu-item) {
    height: 60px;
    line-height: 60px;
    font-size: 14px;
  }
  
  :deep(.el-sub-menu) {
    height: 60px;
    line-height: 60px;
  }
  
  :deep(.el-sub-menu__title) {
    height: 60px;
    line-height: 60px;
  }
  
  :deep(.el-menu-item h1) {
    font-size: 20px;
  }
  
  .el-main1 {
    padding: 16px;
    min-height: calc(100vh - 84px);
  }
  
  :deep(.el-main1 > *) {
    padding: 20px;
    border-radius: 12px;
  }
}

@media (max-width: 480px) {
  :deep(.el-menu-demo) {
    padding: 0 12px;
  }
  
  :deep(.el-menu-item h1) {
    font-size: 18px;
  }
  
  .el-main1 {
    padding: 12px;
  }
  
  :deep(.el-main1 > *) {
    padding: 16px;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  #home {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d3748 100%);
  }
  
  .el-header,
  :deep(.el-menu-demo) {
    background: #2d3748;
  }
  
  :deep(.el-main1 > *) {
    background: #2d3748;
    color: #e2e8f0;
  }
}
</style>
