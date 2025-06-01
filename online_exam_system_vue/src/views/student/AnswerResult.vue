<template>
  <el-row class="full-height" type="flex" justify="center" align="middle">
    <el-col :span="12">
      <el-card :body-style="{ padding: '30px' }" class="result-card">
        <h1 class="title">考试成绩</h1>
        <el-row>
          <el-col :span="24" class="info-item">
            <p><strong>总分:</strong> {{ totalScore }}</p>
          </el-col>
          <el-col :span="24" class="info-item">
            <p><strong>提交时间:</strong> {{ formattedSubmitTime }}</p>
          </el-col>
        </el-row>
        <el-button type="primary" class="action-button" @click="goHome">返回首页</el-button>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import dayjs from 'dayjs';

export default {
  name: 'ResultPage',
  setup() {
    const route = useRoute();
    const router = useRouter();

    // 从路由的 query 参数获取数据
    const totalScore = ref(route.query.totalScore);
    const submitTime = ref(route.query.submitTime);

    /**
     * 格式化提交时间为中国北京时间
     */
    const formattedSubmitTime = computed(() => {
      if (!submitTime.value) return '';
      
      try {
        const date = new Date(submitTime.value);
        // 转换为北京时间 (UTC+8)
        const beijingTime = new Date(date.getTime() + (8 * 60 * 60 * 1000));
        
        const year = beijingTime.getFullYear();
        const month = String(beijingTime.getMonth() + 1).padStart(2, '0');
        const day = String(beijingTime.getDate()).padStart(2, '0');
        const hours = String(beijingTime.getHours()).padStart(2, '0');
        const minutes = String(beijingTime.getMinutes()).padStart(2, '0');
        const seconds = String(beijingTime.getSeconds()).padStart(2, '0');
        
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      } catch (error) {
        console.error('日期格式化失败:', error);
        return submitTime.value;
      }
    });

    // 返回首页的方法
    const goHome = () => {
      router.push('/');
    };

    return {
      totalScore,
      submitTime,
      formattedSubmitTime,
      goHome,
    };
  },
};
</script>

<style scoped>
/* 页面背景渐变 */
.full-height {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 卡片样式 */
.result-card {
  text-align: center;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  background: #fff;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.result-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

/* 标题样式 */
.title {
  margin-bottom: 20px;
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
}

/* 信息项样式 */
.info-item {
  margin-bottom: 15px;
  font-size: 16px;
  color: #636e72;
}



</style>
