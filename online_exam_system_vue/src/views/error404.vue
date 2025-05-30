<template>
  <div class="result-container">
    <el-card class="box-card">
      <el-result
          icon="error"
          title="404"
          sub-title="没有找到页面"
      >
        <template #extra>
          <el-button type="primary" @click="goHome">回到首页</el-button>
          <el-button type="danger" @click="downloadGenshin">下载原神</el-button>

        </template>
      </el-result>
    </el-card>

  </div>
</template>

<script>

export default {
  name: "error404",
  methods: {
    goHome() {
      this.$router.push("/");
    },
    downloadGenshin() {
      window.location.href = ("https://lililili.icu/gi");
    }
  }
};
</script>

<style scoped>
.result-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 高度占满整个视窗 */
  width: 100%;
}

.box-card {
  width: 30%;
}
</style>