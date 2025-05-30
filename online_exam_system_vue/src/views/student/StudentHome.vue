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
#home {
  width: 1000px;
  margin: 0 auto;
}
.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;
}
.el-main1{
  height: 90vh;
}
</style>
