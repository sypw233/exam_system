<!-- src/App.vue -->
<template>
  <div id="app">
    <router-view></router-view>  <!-- 这里是显示路由匹配页面的地方 -->
  </div>
</template>

<script>
export default {
  name: 'OnlineExamSystem',
}

</script>
<style>
/* 强制修改所有 el-input 的圆角为 10px */
.el-input__inner {
  border-radius: 10px !important;
}
</style>