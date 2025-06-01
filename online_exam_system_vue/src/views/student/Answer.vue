<template>
  <el-container>
    <el-header class="exam-header" height="auto">
      <div class="exam-info">
        <h1 class="exam-title">{{ exam.title }}</h1>
        <p class="exam-time">截止时间：{{ formatDateTime(exam.endTime) }}</p>
        <p class="exam-countdown" v-if="remainingTime > 0">
          倒计时：{{ formattedTime }}
        </p>
        <p class="exam-countdown-ended" v-else>
          考试时间已结束，正在提交试卷...
        </p>
      </div>
      <el-button type="primary" size="default" @click="submitExam" class="submit-btn" :disabled="remainingTime <= 0">
        提交考试
      </el-button>
    </el-header>

    <el-main class="exam-main">
      <el-form :model="answers" label-position="top">
        <el-row v-for="(question, index) in exam.questions" :key="question.id" class="question-container">
          <el-col :span="24">
            <div class="question">
              <h3>{{ index + 1 }}. {{ question.content }}</h3>

              <!-- 单选题 -->
              <template v-if="question.type === 'single'">
                <el-radio-group v-model="answers[question.id]">
                  <el-row v-for="(option, key) in JSON.parse(question.options)" :key="key" class="option-row">
                    <el-col :span="12">
                      <el-radio :value="key">{{ option }}</el-radio>
                    </el-col>
                  </el-row>
                </el-radio-group>
              </template>

              <!-- 多选题 -->
              <template v-if="question.type === 'multiple'">
                <el-checkbox-group v-model="answers[question.id]">
                  <el-row v-for="(option, key) in JSON.parse(question.options)" :key="key" class="option-row">
                    <el-col :span="12">
                      <el-checkbox :value="key">{{ option }}</el-checkbox>
                    </el-col>
                  </el-row>
                </el-checkbox-group>
              </template>

              <!-- 判断题 -->
              <template v-if="question.type === 'true_false'">
                <el-radio-group v-model="answers[question.id]">
                  <el-radio value="true">正确</el-radio>
                  <el-radio value="false">错误</el-radio>
                </el-radio-group>
              </template>

              <!-- 填空题 -->
              <template v-if="question.type === 'fill_blank'">
                <el-input v-model="answers[question.id]" placeholder="请输入答案" class="fill-blank-input"/>
              </template>

              <!-- 简答题 -->
              <template v-if="question.type === 'short_answer'">
                <el-input 
                  type="textarea" 
                  v-model="answers[question.id]" 
                  placeholder="请详细回答问题..." 
                  class="textarea-input"
                  :rows="6"
                  maxlength="1000"
                  show-word-limit
                />
              </template>

              <!-- 论述题 -->
              <template v-if="question.type === 'essay'">
                <el-input 
                  type="textarea" 
                  v-model="answers[question.id]" 
                  placeholder="请详细论述..." 
                  class="textarea-input"
                  :rows="10"
                  maxlength="2000"
                  show-word-limit
                />
              </template>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-main>

    <el-footer class="exam-footer">
      <!-- 留空或根据需要添加内容 -->
    </el-footer>
    <el-backtop :visibility-height="100" :right="100" :bottom="100" />
  </el-container>
</template>

<script>
import { onMounted, ref, computed, onUnmounted } from "vue";
import api from "@/api/axios.js";
import router from "@/router/index.js";
import { useRoute } from "vue-router";

export default {
  name: "Answer",
  setup() {
    const route = useRoute();
    const examId = ref();
    const exam = ref({
      title: null,
      startTime: null,
      endTime: null,
      questions: [],
    });
    const remainingTime = ref(0); // 剩余时间（秒）
    const intervalId = ref(null); // 定时器ID

    const getExamData = async () => {
      try {
        const res = await api(`/exams/${examId.value}/details`);
        exam.value.title = res.data.title;
        exam.value.startTime = res.data.startTime;
        exam.value.endTime = res.data.endTime;
        exam.value.questions = res.data.questions;

        // 计算剩余时间
        const endTime = new Date(res.data.endTime).getTime();
        const currentTime = Date.now();
        remainingTime.value = Math.max(Math.floor((endTime - currentTime) / 1000), 0);

        // 启动倒计时
        startCountdown();
      } catch (error) {
        console.log(error.response.status);
        if (error.response.status === 409) {
          alert("获取试卷失败:已经提交过该试卷");
        } else if (error.response.status === 403) {
          alert("获取试卷失败:当前考试不在时间内");
        }
        await router.push({path: "/"});
      }
    };

    const startCountdown = () => {
      if (intervalId.value) return; // 避免重复启动
      intervalId.value = setInterval(() => {
        if (remainingTime.value > 0) {
          remainingTime.value--;
        } else {
          clearInterval(intervalId.value);
          submitExam(); // 时间结束自动提交试卷
        }
      }, 1000);
    };

    const stopCountdown = () => {
      if (intervalId.value) {
        clearInterval(intervalId.value);
        intervalId.value = null;
      }
    };

    const answers = ref({});
    const formattedTime = computed(() => {
      const minutes = Math.floor(remainingTime.value / 60);
      const seconds = remainingTime.value % 60;
      return `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
    });

    const submitExam = async () => {
      stopCountdown(); // 停止倒计时
      const submitData = {
        userId: localStorage.getItem("id"),
        examId: examId.value,
        answers: answers.value,
      };

      try {
        const res = await api.post(`/exam-submissions/submit`, submitData);
        alert("提交成功");
        await router.push({
          path: "/answer/result",
          query: {
            totalScore: res.data.submitScore,
            submitTime: res.data.submitTime,
          },
        });
      } catch (error) {
        console.error("提交失败", error);
      }
    };

    onMounted(() => {
      examId.value = route.query.examId;
      getExamData();
    });

    /**
     * 格式化日期时间为中国北京时间
     * @param {string} dateTimeString - ISO格式的日期时间字符串
     * @returns {string} 格式化后的日期时间字符串 (YYYY-MM-DD HH:mm:ss)
     */
    const formatDateTime = (dateTimeString) => {
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

    onUnmounted(() => {
      stopCountdown(); // 组件卸载时清除定时器
    });

    return {
      exam,
      examId,
      answers,
      getExamData,
      submitExam,
      remainingTime,
      formattedTime,
      formatDateTime,
    };
  },
};
</script>

<style scoped>
.exam-header {
  background-color: #f0f2f5;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.exam-title {
  font-size: 24px;
  margin-bottom: 10px;
}

.exam-time {
  font-size: 14px;
  color: #888;
  margin-bottom: 10px;
}



.exam-main {
  background-color: #fff;
  padding: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.question-container {
  margin-bottom: 30px;
}

.question {
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #fafafa;
  max-width: 800px;
  margin: 0 auto;
}

h3 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.el-radio-group, .el-checkbox-group {
  margin-top: 10px;
}

.option-row {
  margin-bottom: 10px;
}

.el-radio, .el-checkbox {
  margin-right: 15px;
}

.textarea-input {
  width: 100%;
  height: 120px;
  margin-top: 10px;
}

.fill-blank-input {
  width: 100%;
  margin-top: 10px;
}
.el-button {
  border-radius: 8px;
}
.exam-footer {
  text-align: center;
  padding: 20px;
}
</style>
