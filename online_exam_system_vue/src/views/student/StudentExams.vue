<template>
  <el-row class="top" type="flex" justify="space-between" align="middle">
    <el-col :span="4">
      <h1 class="order">试卷列表</h1>
    </el-col>
    <el-col :span="10">
      <el-input
          v-model="keyword"
          placeholder="输入试卷名称进行搜索"
          suffix-icon="el-icon-search"
          @input="search"
          clearable
      />
    </el-col>
    <el-col :span="6">
      <el-button type="warning" @click="reset">重置</el-button>
    </el-col>
  </el-row>

  <el-row class="paper" type="flex" justify="start" :gutter="12">
    <el-col v-for="(item, index) in pagination.records" :key="index" :span="12">
      <el-card :class="['item', { expired: isExpired(item.endTime) }]" :hoverable="!isExpired(item.endTime)">
        <h4 @click="!isExpired(item.endTime) && toExamMsg(item.id)"
            :class="{ 'disabled-link': isExpired(item.endTime) }">{{ item.title }}</h4>
        <div class="info">
          <p class="exam-info">{{ item.description }}</p>
          <span>{{ formatDate(item.startTime) }} - {{ formatDate(item.endTime) }}</span>
          <i class="iconfont icon-time"></i>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.current"
      :page-sizes="[6, 10, 20, 40]"
      :page-size="pagination.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
  />
</template>

<script>
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import api from '@/api/axios';
import Navbar from "@/components/Navbar.vue";

export default {
  name: 'StudentExams',
  components: {Navbar},
  setup() {
    const router = useRouter();
    const keyword = ref('');
    const pagination = ref({
      current: 1,
      total: null,
      size: 6,
      records: [],
    });

    /**
     * 格式化日期时间为中国北京时间
     * @param {string} dateTimeString - ISO格式的日期时间字符串
     * @returns {string} 格式化后的日期时间字符串 (YYYY-MM-DD HH:mm:ss)
     */
    const formatDate = (dateTimeString) => {
      if (!dateTimeString) return '';
      
      try {
        const date = new Date(dateTimeString);
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
        return dateTimeString;
      }
    };

    // 判断考试是否过期
    const isExpired = (endTime) => {
      const currentTime = new Date().getTime();
      const endTimeStamp = new Date(endTime).getTime();
      return currentTime > endTimeStamp;
    };

    // 获取考试信息
    const getExamInfo = async () => {
      try {
        const res = await api(`/exams/${pagination.value.current}/${pagination.value.size}`);
        if (res.data.code === 200) {
          pagination.value.records = res.data.data.records;
          pagination.value.size = res.data.data.size;
          pagination.value.current = res.data.data.current;
          pagination.value.total = res.data.data.total;
        } else {
          console.error('请求失败，错误信息：', res.data.message);
        }
      } catch (error) {
        console.error(error);
      }
    };

    // 改变当前记录条数
    const handleSizeChange = (val) => {
      pagination.value.size = val;
      getExamInfo();
    };

    // 改变当前页码，重新发送请求
    const handleCurrentChange = (val) => {
      pagination.value.current = val;
      getExamInfo();
    };

    // 搜索试卷
    const search = async () => {
      try {
        if(keyword.value===''){
          await getExamInfo();
          return;
        }
        const res = await api(`/exams/search?key=${keyword.value}&page=${pagination.value.current}&size=${pagination.value.size}`);
        if (res.data.code === 200) {
          pagination.value.records = res.data.data.records;
          pagination.value.size = res.data.data.size;
          pagination.value.current = res.data.data.current;
        }
      } catch (error) {
        console.error('获取试卷失败', error);
      }
    };

    // 重置搜索
    const reset = async () => {
      try {
        await getExamInfo();
        keyword.value = '';
      } catch (error) {
        console.error('获取试卷失败', error);
      }
    };

    // 跳转到试卷详情页
    const toExamMsg = (examId) => {
      router.push({
        path: '/answer',
        query: {examId}
      });
    };

    // 初始化时获取考试信息
    onMounted(() => {
      getExamInfo();
    });

    return {
      keyword,
      pagination,
      getExamInfo,
      handleSizeChange,
      handleCurrentChange,
      reset,
      search,
      toExamMsg,
      formatDate,
      isExpired,
    };
  },
};
</script>

<style scoped>
/* 保持原有悬浮效果 */
.paper h4 {
  cursor: pointer;
}

/* 禁用链接的样式 */
.paper h4.disabled-link {
  cursor: not-allowed;
  color: #bbb;
}

.item .info i {
  margin-right: 5px;
  color: #0195ff;
}

.item .info span {
  margin-right: 14px;
}

/* 固定卡片大小 */
.paper .item {
  height: 200px; /* 固定高度 */
  border-radius: 20px;
  padding: 20px 10px;
  border: 1px solid #eee;

  box-shadow: 0 0 4px 2px rgba(217, 222, 234, 0.3);
  transition: all 0.6s ease;
}

/* 保留悬浮效果 */
.paper .item:hover {
  box-shadow: 0 0 4px 2px rgba(140, 193, 248, 0.45);
  transform: scale(1.03);
}

/* 过期考试的样式 */
.paper .item.expired {
  background-color: #f5f5f5;
  border-color: #dcdfe6;
  color: #bbb;
}

.paper .item.expired .info {
  color: #bbb;
}

.paper .item.expired:hover {
  box-shadow: 0 0 4px 2px rgba(217, 222, 234, 0.3);
  transform: scale(1);
}

.paper .item .info {
  font-size: 14px;
  color: #88949b;
}

.paper .item .exam-info {
  font-size: 14px;
  color: #88949b;
}

.paper * {
  margin: 10px 0;
}

.top li {
  display: flex;
  align-items: center;
}

.wrapper .top li {
  margin: 20px;
}

#myExam {
  width: 980px;
  margin: 0 auto;
}
</style>
