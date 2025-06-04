<template>
  <div class="submission-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">考试记录</h1>
      <div class="search-section">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索考试或用户名"
            @input="searchSubmission"
            prefix-icon="Search"
            clearable
            class="search-input"
        />
      </div>
    </div>

    <!-- 考试记录表格 -->
    <div class="table-container">
      <el-table
          :data="filteredSubmissions"
          class="submission-table"
          empty-text="暂无考试记录"
          stripe
      >
        <el-table-column prop="userName" label="用户名" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ row.userName.charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="examId" label="考试ID" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">#{{ row.examId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examTitle" label="考试名称" min-width="200">
          <template #default="{ row }">
            <div class="exam-title">
              <i class="el-icon-document"></i>
              <span>{{ row.examTitle }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="examTotalScore" label="总分" width="100" align="center">
          <template #default="{ row }">
            <span class="total-score">{{ row.examTotalScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="submissionScore" label="得分" width="120" align="center">
          <template #default="{ row }">
            <div v-if="row.isGraded" class="score-info">
              <span class="score" :class="getScoreClass(row.submissionScore, row.examTotalScore)">
                {{ row.submissionScore }}
              </span>
              <div class="score-percentage">
                {{ getScorePercentage(row.submissionScore, row.examTotalScore) }}%
              </div>
            </div>
            <div v-else class="ungraded-info">
              <el-tag type="warning" size="small">未阅卷</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isGraded" :type="getGradeType(row.submissionScore, row.examTotalScore)" size="small">
              {{ getGrade(row.submissionScore, row.examTotalScore) }}
            </el-tag>
            <el-tag v-else type="info" size="small">
              待阅卷
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredSubmissions.length === 0" class="empty-state">
      <div class="empty-icon">
        <i class="el-icon-document-remove"></i>
      </div>
      <p class="empty-text">暂无考试记录</p>
      <p class="empty-desc">完成考试后，记录将在这里显示</p>
    </div>
  </div>
</template>

<script>
import {onMounted, ref} from 'vue';
import api from "@/api/axios.js";

export default {
  name: 'StudentExamSubmission',
  setup() {
    const examSubmissions = ref([]);
    const searchKeyword = ref('');
    const filteredSubmissions = ref([]);
    const userId = ref('');

    // 根据用户ID获取考试提交记录
    const getAllSubmissionsByUserId = async () => {
      try {
        const response = await api(`/exam-submissions/user/${userId.value}`);
        if (response.status === 200) {
          examSubmissions.value = response.data;
          filteredSubmissions.value = examSubmissions.value;
        }
      } catch (error) {
        console.error('Error fetching exam submissions:', error);
      }
    };

    // 根据关键词进行筛选
    const searchSubmission = () => {
      console.log("筛选keyword:" + searchKeyword.value.trim())
      if (searchKeyword.value.trim() === '') {
        filteredSubmissions.value = examSubmissions.value;
        console.log("筛选内容为空")
      } else {
        filteredSubmissions.value = examSubmissions.value.filter(submission => {
          return submission.userName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
              submission.examTitle.toLowerCase().includes(searchKeyword.value.toLowerCase())
        });
      }
    };

    // 初始化时获取用户的考试提交记录
    onMounted(() => {
      userId.value = localStorage.getItem("id")
      getAllSubmissionsByUserId();
    });

    // 计算成绩百分比
    const getScorePercentage = (score, totalScore) => {
      if (!totalScore || totalScore === 0) return 0;
      return Math.round((score / totalScore) * 100);
    };

    // 获取成绩等级
    const getGrade = (score, totalScore) => {
      const percentage = getScorePercentage(score, totalScore);
      if (percentage >= 90) return '优秀';
      if (percentage >= 80) return '良好';
      if (percentage >= 70) return '中等';
      if (percentage >= 60) return '及格';
      return '不及格';
    };

    // 获取成绩等级类型
    const getGradeType = (score, totalScore) => {
      const percentage = getScorePercentage(score, totalScore);
      if (percentage >= 90) return 'success';
      if (percentage >= 80) return '';
      if (percentage >= 70) return 'warning';
      if (percentage >= 60) return 'info';
      return 'danger';
    };

    // 获取分数样式类
    const getScoreClass = (score, totalScore) => {
      const percentage = getScorePercentage(score, totalScore);
      if (percentage >= 90) return 'score-excellent';
      if (percentage >= 80) return 'score-good';
      if (percentage >= 70) return 'score-average';
      if (percentage >= 60) return 'score-pass';
      return 'score-fail';
    };

    return {
      searchKeyword,
      filteredSubmissions,
      examSubmissions,
      searchSubmission,
      userId,
      getScorePercentage,
      getGrade,
      getGradeType,
      getScoreClass,
    };
  },
};
</script>

<style scoped>
/* 主容器样式 */
.submission-container {
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

/* 表格容器样式 */
.table-container {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.table-container:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 表格样式 */
:deep(.submission-table) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.submission-table .el-table__header) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

:deep(.submission-table .el-table__header th) {
  background: transparent;
  color: #606266;
  font-weight: 600;
  border-bottom: 2px solid #e4e7ed;
}

:deep(.submission-table .el-table__row) {
  transition: all 0.3s ease;
}

:deep(.submission-table .el-table__row:hover) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(102, 177, 255, 0.05));
}

:deep(.submission-table .el-table__row.el-table__row--striped) {
  background: #fafbfc;
}

:deep(.submission-table .el-table__row.el-table__row--striped:hover) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(102, 177, 255, 0.05));
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  font-weight: 600;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

/* 考试标题样式 */
.exam-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.exam-title i {
  color: #409eff;
  font-size: 16px;
}

/* 总分样式 */
.total-score {
  font-weight: 600;
  color: #606266;
  font-size: 16px;
}

/* 得分信息样式 */
.score-info {
  text-align: center;
}

.score {
  font-size: 18px;
  font-weight: 700;
  display: block;
  margin-bottom: 4px;
}

.score-percentage {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

/* 未阅卷状态样式 */
.ungraded-info {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 不同等级分数的颜色 */
.score-excellent {
  color: #67c23a;
}

.score-good {
  color: #409eff;
}

.score-average {
  color: #e6a23c;
}

.score-pass {
  color: #909399;
}

.score-fail {
  color: #f56c6c;
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: 8px;
  font-weight: 500;
  padding: 4px 12px;
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
  .submission-container {
    padding: 20px;
  }
  
  .page-header,
  .table-container {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .submission-container {
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
  
  .search-input {
    width: 100%;
  }
  
  .table-container {
    padding: 16px;
  }
  
  /* 隐藏部分列在小屏幕上 */
  :deep(.submission-table .el-table__cell:nth-child(2)) {
    display: none;
  }
  
  .user-info {
    flex-direction: column;
    gap: 4px;
    align-items: flex-start;
  }
  
  .user-avatar {
    align-self: center;
  }
}

@media (max-width: 480px) {
  .page-header,
  .table-container {
    padding: 12px;
  }
  
  .page-title {
    font-size: 20px;
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
  .submission-container {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d3748 100%);
  }
  
  .page-header,
  .table-container,
  .empty-state {
    background: #2d3748;
    color: #e2e8f0;
  }
  
  :deep(.submission-table .el-table__header) {
    background: linear-gradient(135deg, #4a5568 0%, #2d3748 100%);
  }
  
  :deep(.submission-table .el-table__header th) {
    color: #e2e8f0;
    border-bottom-color: #4a5568;
  }
  
  :deep(.submission-table .el-table__row.el-table__row--striped) {
    background: #4a5568;
  }
  
  .user-name {
    color: #e2e8f0;
  }
  
  .total-score {
    color: #a0aec0;
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
