<template>
  <el-input
      v-model="searchKeyword"
      placeholder="输入关键词"
      @input="searchSubmission"
      style="width: 200px; margin-right: 10px;"
  />
  <el-table
      :data="filteredSubmissions"
      style="width: 100%"
  >
    <el-table-column prop="userName" label="用户名"></el-table-column>
    <el-table-column prop="examId" label="考试ID"></el-table-column>
    <el-table-column prop="examTitle" label="考试"></el-table-column>
    <el-table-column prop="examTotalScore" label="总分"></el-table-column>
    <el-table-column prop="submissionScore" label="得分"></el-table-column>
  </el-table>

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

    return {
      searchKeyword,
      filteredSubmissions,
      examSubmissions,
      searchSubmission,
      userId,
    };
  },
};
</script>

<style scoped>

</style>
