<template>
  <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      :ellipsis="false"
      style="display: flex; align-items: center;"
  >
    <el-menu-item index="0">
      <h1>在线考试系统</h1>
    </el-menu-item>
    <el-tag :type="tagType" effect="dark" round size="large">{{ role }}</el-tag>
    <el-sub-menu index="2">
      <template #title>{{ username || "我的" }}</template>
      <el-menu-item  @click="logout">退出</el-menu-item>
    </el-sub-menu>

  </el-menu>
</template>

<script>
import {ref} from 'vue';
import router from "@/router/index.js";

export default {
  name: 'Navbar',
  setup() {
    const activeIndex = ref('0');


    return {
      activeIndex,
    };
  },
  data() {
    return {
      username: localStorage.getItem('username') || '',
      role: localStorage.getItem('role') === 'TEACHER' ? '教师' :
          localStorage.getItem('role') === 'ADMIN' ? '管理员' : '',
    };
  },
  computed: {
    // 根据 role 的值动态返回 tag 的 type
    tagType() {
      if(this.role === 'TEACHER') {
        return "info"
      }else{
        return "warning"
      }
    }
  },
  methods: {
    logout() {
      if (confirm('Logging out?')) {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        router.push('/');
      }
    },
  },
};
</script>

<style scoped>
.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;
}

</style>
