<template>
  <el-form :model="randomExamForm" ref="randomExamFormRef" label-width="120px">
    <!-- 考试名称 -->
    <el-form-item label="考试名称" :rules="[{ required: true, message: '请输入考试名称', trigger: 'blur' }]">
      <el-input v-model="randomExamForm.title" autocomplete="off" />
    </el-form-item>
    
    <!-- 课程选择 -->
    <el-form-item label="选择课程" :rules="[{ required: true, message: '请选择课程', trigger: 'change' }]">
      <el-select v-model="randomExamForm.courseId" placeholder="请选择课程" style="width: 100%">
        <el-option 
          v-for="course in courseList" 
          :key="course.id" 
          :label="course.courseName" 
          :value="course.id"
        ></el-option>
      </el-select>
    </el-form-item>
    
    <!-- 考试描述 -->
    <el-form-item label="考试描述">
      <el-input v-model="randomExamForm.description" type="textarea" autocomplete="off" />
    </el-form-item>

    <!-- 开始时间 -->
    <el-form-item label="开始时间" :rules="[{ required: true, message: '请选择开始时间', trigger: 'change' }]">
      <el-date-picker
        v-model="randomExamForm.startTime"
        type="datetime"
        placeholder="选择开始时间"
        style="width: 100%;"
      />
    </el-form-item>

    <!-- 结束时间 -->
    <el-form-item label="结束时间" :rules="[{ required: true, message: '请选择结束时间', trigger: 'change' }]">
      <el-date-picker
        v-model="randomExamForm.endTime"
        type="datetime"
        placeholder="选择结束时间"
        style="width: 100%;"
      />
    </el-form-item>

    <!-- 试卷总分 -->
    <el-form-item label="试卷总分" :rules="[{ required: true, message: '请设置试卷总分', trigger: 'blur' }]">
      <el-input-number v-model="randomExamForm.totalScore" :min="1" :max="100" />
    </el-form-item>

    <!-- 题目分布 -->
    <el-form-item label="题目分布">
      <div v-for="(distribution, index) in randomExamForm.questionDistribution" :key="index" style="margin-bottom: 15px; padding: 15px; border: 1px solid #dcdfe6; border-radius: 4px;">
        <!-- 题型选择 -->
        <el-form-item :label="'题型'" label-width="80px">
          <el-select v-model="distribution.type" placeholder="请选择题型" style="width: 100%">
            <el-option label="单选题" value="single"></el-option>
            <el-option label="多选题" value="multiple"></el-option>
            <el-option label="判断题" value="true_false"></el-option>
            <el-option label="填空题" value="fill_blank"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 分类选择 -->
        <el-form-item :label="'分类'" label-width="80px">
          <el-select v-model="distribution.category" placeholder="请选择分类(可选)" style="width: 100%" clearable>
            <el-option 
              v-for="category in categoryList" 
              :key="category" 
              :label="category" 
              :value="category"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 难度选择 -->
        <el-form-item :label="'难度'" label-width="80px">
          <el-select v-model="distribution.difficulty" placeholder="请选择难度(可选)" style="width: 100%" clearable>
            <el-option label="简单" value="easy"></el-option>
            <el-option label="中等" value="medium"></el-option>
            <el-option label="困难" value="hard"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 题目数量 -->
        <el-form-item :label="'数量'" label-width="80px">
          <el-input-number v-model="distribution.count" :min="1" :max="20" />
        </el-form-item>
        
        <!-- 每题分数 -->
        <el-form-item :label="'每题分数'" label-width="80px">
          <el-input-number v-model="distribution.scorePerQuestion" :min="1" :max="20" />
        </el-form-item>
        
        <!-- 删除按钮 -->
        <el-button type="danger" @click="removeDistribution(index)" style="margin-top: 10px">删除</el-button>
      </div>
      
      <!-- 添加分布按钮 -->
      <el-button type="primary" @click="addDistribution" style="margin-top: 10px">添加题目分布</el-button>
    </el-form-item>
    
    <!-- 计算总题目数和总分数 -->
    <el-form-item>
      <div style="display: flex; justify-content: space-between; color: #409EFF;">
        <span>总题目数: {{ totalQuestionCount }}</span>
        <span>当前分数分配: {{ currentTotalScore }}/{{ randomExamForm.totalScore }}</span>
      </div>
    </el-form-item>
  </el-form>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue';
import api from '@/api/axios';

export default {
  name: 'RandomExamForm',
  emits: ['update:randomExamForm', 'submit'],
  props: {
    courseList: {
      type: Array,
      default: () => []
    }
  },
  setup(props, { emit }) {
    const randomExamFormRef = ref(null);
    const categoryList = ref([]);
    
    // 随机组卷表单数据
    const randomExamForm = reactive({
      title: '',
      description: '',
      startTime: '',
      endTime: '',
      creatorId: localStorage.getItem("id"),
      totalScore: 100,
      courseId: '',
      questionDistribution: [
        {
          type: 'single',
          category: null,
          difficulty: null,
          count: 5,
          scorePerQuestion: 4
        }
      ]
    });
    
    // 计算总题目数
    const totalQuestionCount = computed(() => {
      return randomExamForm.questionDistribution.reduce((sum, item) => sum + item.count, 0);
    });
    
    // 计算当前分配的总分
    const currentTotalScore = computed(() => {
      return randomExamForm.questionDistribution.reduce((sum, item) => {
        return sum + (item.count * item.scorePerQuestion);
      }, 0);
    });
    
    // 添加题目分布
    const addDistribution = () => {
      randomExamForm.questionDistribution.push({
        type: 'single',
        category: null,
        difficulty: null,
        count: 5,
        scorePerQuestion: 4
      });
    };
    
    // 删除题目分布
    const removeDistribution = (index) => {
      randomExamForm.questionDistribution.splice(index, 1);
    };
    
    // 获取所有分类
    const getCategories = async () => {
      try {
        // 先获取所有题目，然后提取分类
        const res = await api.get('/questions');
        if (res.data && res.data.data) {
          // 从题目数据中提取唯一的分类
          const categories = [...new Set(res.data.data.map(q => q.category).filter(c => c))];
          categoryList.value = categories;
        }
      } catch (error) {
        console.error('获取分类列表失败', error);
        // 如果获取失败，提供一些默认分类
        categoryList.value = ['数学', '语文', '英语', '物理', '化学', '生物', '历史', '地理', '政治'];
      }
    };
    
    // 提交表单验证
    const validate = () => {
      return new Promise((resolve) => {
        randomExamFormRef.value.validate((valid) => {
          if (valid) {
            // 转换日期格式
            const formData = { ...randomExamForm };
            formData.startTime = formData.startTime.toISOString();
            formData.endTime = formData.endTime.toISOString();
            resolve(formData);
          } else {
            resolve(false);
          }
        });
      });
    };
    
    onMounted(() => {
      getCategories();
    });
    
    return {
      randomExamFormRef,
      randomExamForm,
      categoryList,
      totalQuestionCount,
      currentTotalScore,
      addDistribution,
      removeDistribution,
      validate
    };
  }
};
</script>

<style scoped>
.el-form-item {
  margin-bottom: 22px;
}
</style>