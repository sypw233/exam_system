<template>
  <div class="exams-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">试卷列表</h1>
      <div class="search-section">
        <el-input
            v-model="keyword"
            placeholder="输入试卷名称进行搜索"
            prefix-icon="Search"
            @input="search"
            clearable
            class="search-input"
        />
        <el-button type="warning" @click="reset" class="reset-btn">重置</el-button>
      </div>
    </div>

    <!-- 试卷列表 -->
    <div class="exams-grid">
      <el-row class="paper" type="flex" justify="start" :gutter="24">
        <el-col v-for="(item, index) in pagination.records" :key="index" :span="12" class="exam-col">
          <el-card :class="['item', { expired: isExpired(item.endTime) }]" :hoverable="!isExpired(item.endTime)">
            <h4 @click="!isExpired(item.endTime) && toExamMsg(item.id)"
                :class="{ 'disabled-link': isExpired(item.endTime) }">{{ item.title }}</h4>
            <div class="info">
              <p class="exam-info">{{ item.description }}</p>
              <div class="exam-time">
                <i class="el-icon-time"></i>
                <span>{{ formatDate(item.startTime) }} - {{ formatDate(item.endTime) }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[6, 10, 20, 40]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
      />
    </div>

    <!-- 空状态 -->
    <div v-if="pagination.records.length === 0" class="empty-state">
      <div class="empty-icon">
        <i class="el-icon-document-remove"></i>
      </div>
      <p class="empty-text">暂无试卷</p>
      <p class="empty-desc">试卷发布后将在这里显示</p>
    </div>
  </div>
</template>

<script>
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
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

    /**
     * 检查用户是否已经提交过该考试
     * @param {number} examId - 考试ID
     * @returns {boolean} 是否已提交
     */
    const checkExamSubmission = async (examId) => {
      try {
        const userId = localStorage.getItem('id');
        const response = await api(`/exam-submissions/user/${userId}`);
        if (response.status === 200) {
          // 检查是否存在该考试的提交记录
          return response.data.some(submission => submission.examId === examId);
        }
        return false;
      } catch (error) {
        console.error('检查考试提交状态失败:', error);
        return false;
      }
    };

    /**
     * 跳转到试卷详情页
     * @param {number} examId - 考试ID
     */
    const toExamMsg = async (examId) => {
      try {
        // 先检查是否已经提交过该考试
        const hasSubmitted = await checkExamSubmission(examId);
        if (hasSubmitted) {
          ElMessage.warning('您已经提交过该考试，无法重复参加！');
          return;
        }
        
        // 如果没有提交过，则跳转到答题页面
        router.push({
          path: '/answer',
          query: {examId}
        });
      } catch (error) {
        console.error('跳转考试页面失败:', error);
        ElMessage.error('跳转失败，请稍后重试');
      }
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
/* 主容器样式 */
.exams-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 24px;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 页面头部样式 */
.page-header {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.page-header:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 页面标题样式 */
.page-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 搜索区域样式 */
.search-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 300px;
}

:deep(.search-input .el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
}

:deep(.search-input .el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  border-color: #409eff;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  border-color: #409eff;
}

/* 重置按钮样式 */
.reset-btn {
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
  transition: all 0.3s ease;
}

:deep(.reset-btn.el-button--warning) {
  background: linear-gradient(135deg, #e6a23c, #f0a020);
  border: none;
}

:deep(.reset-btn.el-button--warning:hover) {
  background: linear-gradient(135deg, #f0a020, #f7ba2a);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(230, 162, 60, 0.4);
}

/* 试卷网格容器样式 */
.exams-grid {
  margin-bottom: 24px;
  min-height: 600px; /* 设置最小高度，确保页面不会因为内容少而缩小 */
}

/* 试卷卡片样式 */
:deep(.el-card) {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  background: white;
  margin-bottom: 24px;
}

.paper .item {
  height: 220px;
  width: 100%; /* 固定卡片宽度为100% */
  max-width: 500px; /* 设置最大宽度，防止卡片过宽 */
  position: relative;
}

.paper .item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

/* 试卷标题样式 */
.paper h4 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.paper h4:hover {
  color: #409eff;
  transform: translateX(4px);
}

/* 禁用链接的样式 */
.paper h4.disabled-link {
  cursor: not-allowed;
  color: #c0c4cc;
}

.paper h4.disabled-link:hover {
  color: #c0c4cc;
  transform: none;
}

/* 试卷信息区域样式 */
.item .info {
  position: absolute;
  bottom: 20px;
  left: 20px;
  right: 20px;
}

.item .exam-info {
  font-size: 14px;
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.exam-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.exam-time i {
  color: #409eff;
  font-size: 14px;
}

/* 过期考试的样式 */
.paper .item.expired {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  position: relative;
}

.paper .item.expired::before {
  content: '已过期';
  position: absolute;
  top: 16px;
  right: 16px;
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  z-index: 10;
}

.paper .item.expired h4 {
  color: #c0c4cc;
}

.paper .item.expired .info {
  color: #c0c4cc;
}

.paper .item.expired .exam-info {
  color: #c0c4cc;
}

.paper .item.expired .exam-time {
  color: #c0c4cc;
}

.paper .item.expired .exam-time i {
  color: #c0c4cc;
}

.paper .item.expired:hover {
  transform: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 分页器容器样式 */
.pagination-container {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.pagination-container:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 分页器样式 */
:deep(.el-pagination) {
  display: flex;
  justify-content: center;
  align-items: center;
}

:deep(.el-pagination .el-pager li) {
  border-radius: 8px;
  margin: 0 4px;
  transition: all 0.3s ease;
}

:deep(.el-pagination .el-pager li:hover) {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
}

:deep(.el-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-pagination .btn-prev:hover),
:deep(.el-pagination .btn-next:hover) {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
}

/* 空状态样式 */
.empty-state {
  background: white;
  border-radius: 16px;
  padding: 60px 24px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 64px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 18px;
  color: #606266;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.empty-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .exams-container {
    padding: 20px;
  }
  
  .page-header,
  .pagination-container {
    padding: 20px;
  }
  
  .paper .item {
    height: 200px;
  }
}

@media (max-width: 768px) {
  .exams-container {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 16px;
  }
  
  .page-title {
    font-size: 24px;
    text-align: center;
  }
  
  .search-section {
    flex-direction: column;
    gap: 12px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .reset-btn {
    width: 100%;
  }
  
  .pagination-container {
    padding: 16px;
  }
  
  .paper .item {
    height: 180px;
  }
  
  .paper h4 {
    font-size: 16px;
  }
  
  :deep(.el-col) {
    margin-bottom: 16px;
  }
  
  .paper .item {
    max-width: none; /* 移动端移除最大宽度限制 */
  }
}

@media (max-width: 480px) {
  .page-header,
  .pagination-container {
    padding: 12px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .paper .item {
    height: 160px;
  }
  
  .paper h4 {
    font-size: 15px;
  }
  
  .item .exam-info {
    font-size: 13px;
  }
  
  .empty-state {
    padding: 40px 16px;
  }
  
  .empty-icon {
    font-size: 48px;
  }
  
  .empty-text {
    font-size: 16px;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .exams-container {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d3748 100%);
  }
  
  .page-header,
  .pagination-container,
  .empty-state,
  :deep(.el-card) {
    background: #2d3748;
    color: #e2e8f0;
  }
  
  .paper h4 {
    color: #e2e8f0;
  }
  
  .item .exam-info {
    color: #a0aec0;
  }
  
  .exam-time {
    color: #718096;
  }
  
  .empty-icon {
    color: #718096;
  }
  
  .empty-text {
    color: #a0aec0;
  }
  
  .empty-desc {
    color: #718096;
  }
}
</style>
