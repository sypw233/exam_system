<template>
  <el-header>
    <el-input
        v-model="searchKeyword"
        placeholder="输入关键词进行搜索"
        @input="searchSubmission"
        style="width: 200px; margin-right: 10px;"
    />
  </el-header>
  <el-main>
    <el-table
        :data="filteredSubmissions"
        style="width: 100%"
    >
      <el-table-column prop="userName" label="用户名"></el-table-column>
      <el-table-column prop="examId" label="考试ID"></el-table-column>
      <el-table-column prop="examTitle" label="考试"></el-table-column>
      <el-table-column prop="examTotalScore" label="总分"></el-table-column>
      <el-table-column prop="submissionScore" label="得分"></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button @click="deleteSubmissions(scope.row.id)" type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-main>
</template>

<script>
import {onMounted, ref} from 'vue';
import api from "@/api/axios.js";

export default {
  name: 'ExamSubmission',

  setup() {
    const examSubmissions = ref([]);
    const searchKeyword = ref('');
    const filteredSubmissions = ref([]);


    // 查询所有考试提交记录
    const getAllSubmissions = async () => {
      try {
        const response = await api(`/exam-submissions/all`);
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
      // console.log("筛选keyword:" + searchKeyword.value.trim())
      if (searchKeyword.value.trim() === '') {
        filteredSubmissions.value = examSubmissions.value;
        // console.log("筛选内容为空")
      } else {
        filteredSubmissions.value = examSubmissions.value.filter(submission => {
          return submission.userName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
              submission.examTitle.toLowerCase().includes(searchKeyword.value.toLowerCase())
        });
      }
    };

    // 删除某个考试提交记录
    const deleteSubmissions = async (submissionId) => {
      try {
        await api.delete(`/exam-submissions/${submissionId}`);
        alert('删除成功');
        await getAllSubmissions(); // 删除成功后重新获取考试列表
      } catch (error) {
        console.error('删除失败', error);
      }
    };

    // 初始化时获取所有提交记录
    onMounted(() => {
      getAllSubmissions();
    });

    return {
      searchKeyword,
      filteredSubmissions,
      examSubmissions,
      deleteSubmissions,
      getAllSubmissions,
      searchSubmission,
    };
  },
};
</script>

<style scoped>
.el-button {
  border-radius: 10px;
}
</style>
