<!-- src/components/Sidebar.vue -->
<template>
  <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      theme="dark"
      router
      default-active=""
      mode="vertical"
  >

    <template v-if="role === 'ADMIN'">
      <el-menu-item index="/dashboard/user">
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="/dashboard">
        <span>课程管理</span>
      </el-menu-item>
      <el-sub-menu>
        <template #title><span>考试管理</span></template>
        <el-menu-item index="/dashboard/exam">试卷管理</el-menu-item>
        <el-menu-item index="/dashboard/exam-submission">交卷管理</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/dashboard/question">
        <span>题目管理</span>
      </el-menu-item>
    </template>

    <template v-if="role === 'TEACHER'">
      <el-menu-item index="/dashboard">
        <span>课程管理</span>
      </el-menu-item>
      <el-sub-menu>
        <template #title><span>考试管理</span></template>
        <el-menu-item index="/dashboard/exam">试卷管理</el-menu-item>
        <el-menu-item index="/dashboard/exam-submission">交卷管理</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/dashboard/question">
        <span>题目管理</span>
      </el-menu-item>
    </template>

  </el-menu>
</template>

<script>
import {ref, onMounted} from 'vue';
import {useRoute} from 'vue-router';
import {Location} from "@element-plus/icons-vue";

export default {
  name: 'Sidebar',
  components: {Location},
  setup() {
    const route = useRoute();
    const role = ref(localStorage.getItem('role').toLocaleUpperCase()); // 获取当前用户角色
    const activeMenu = ref(route.path); // 当前激活的菜单项

    // 监听路由变化来更新激活的菜单项
    onMounted(() => {
      activeMenu.value = route.path;
    });

    return {
      role,
      activeMenu,
    };
  },
};
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
  width: 200px;
}
</style>
