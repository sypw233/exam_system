<template>
  <el-form :model="randomExamForm" ref="randomExamFormRef" label-width="120px">
    <!-- 基本信息部分 -->
    <div class="section-title">基本信息</div>
    
    <!-- 考试名称 -->
    <el-form-item label="考试名称" :rules="[{ required: true, message: '请输入考试名称', trigger: 'blur' }]">
      <el-input v-model="randomExamForm.title" autocomplete="off" placeholder="请输入考试名称" />
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
      <el-input 
        v-model="randomExamForm.description" 
        type="textarea" 
        :rows="3"
        placeholder="请输入考试描述（选填）" 
        autocomplete="off" 
      />
    </el-form-item>

    <!-- 时间设置部分 -->
    <div class="section-title">时间设置</div>
    
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
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
    </div>

    <!-- 分数设置 -->
    <div class="section-title">分数设置</div>
    
    <!-- 试卷总分 -->
    <el-form-item label="试卷总分" :rules="[{ required: true, message: '请设置试卷总分', trigger: 'blur' }]">
      <el-input-number 
        v-model="randomExamForm.totalScore" 
        :min="1" 
        :max="100" 
        :step="5"
        style="width: 180px;"
      />
    </el-form-item>

    <!-- 题目分布 -->
    <div class="section-title">题目分布设置</div>
    
    <el-form-item>
      <!-- 题目分布卡片 -->
      <div v-for="(distribution, index) in randomExamForm.questionDistribution" :key="index" class="distribution-card">
        <div class="card-header">
          <div class="card-title">
            <div class="type-icon">
              {{ distribution.type === 'single' ? '单' : 
                 distribution.type === 'multiple' ? '多' : 
                 distribution.type === 'true_false' ? '判' : 
                 distribution.type === 'fill_blank' ? '填' : '?' }}
            </div>
            {{ distribution.type === 'single' ? '单选题' : 
               distribution.type === 'multiple' ? '多选题' : 
               distribution.type === 'true_false' ? '判断题' : 
               distribution.type === 'fill_blank' ? '填空题' : '未知题型' }}
          </div>
        </div>
        
        <div class="card-body">
          <!-- 题型选择 -->
          <el-form-item label="题型">
            <el-select v-model="distribution.type" placeholder="请选择题型" style="width: 100%">
              <el-option label="单选题" value="single"></el-option>
              <el-option label="多选题" value="multiple"></el-option>
              <el-option label="判断题" value="true_false"></el-option>
              <el-option label="填空题" value="fill_blank"></el-option>
            </el-select>
          </el-form-item>
          
          <!-- 分类选择 -->
          <el-form-item label="分类">
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
          <el-form-item label="难度">
            <el-select v-model="distribution.difficulty" placeholder="请选择难度(可选)" style="width: 100%" clearable>
              <el-option label="简单" value="easy"></el-option>
              <el-option label="中等" value="medium"></el-option>
              <el-option label="困难" value="hard"></el-option>
            </el-select>
          </el-form-item>
          
          <!-- 题目数量 -->
          <el-form-item label="数量">
            <el-input-number v-model="distribution.count" :min="1" :max="20" style="width: 100%" />
          </el-form-item>
          
          <!-- 每题分数 -->
          <el-form-item label="每题分数">
            <el-input-number v-model="distribution.scorePerQuestion" :min="1" :max="20" style="width: 100%" />
          </el-form-item>
        </div>
        
        <!-- 删除按钮 -->
        <el-button 
          type="danger" 
          @click="removeDistribution(index)" 
          class="delete-btn"
          circle
          size="small"
        >
          <i class="el-icon-delete">🗑️</i>
        </el-button>
      </div>
      
      <!-- 添加分布按钮 -->
      <el-button 
        @click="addDistribution" 
        class="add-distribution-btn"
        plain
      >
        <i class="el-icon-plus">➕</i> 添加题目分布
      </el-button>
    </el-form-item>
    
    <!-- 计算总题目数和总分数 -->
    <div class="distribution-summary">
      <div class="summary-item">
        <div class="summary-label">总题目数</div>
        <div class="summary-value">{{ totalQuestionCount }}</div>
      </div>
      
      <div class="summary-item">
        <div class="summary-label">当前分数分配</div>
        <div class="summary-value" :style="{color: currentTotalScore > randomExamForm.totalScore ? '#f56c6c' : '#409EFF'}">
          {{ currentTotalScore }}/{{ randomExamForm.totalScore }}
        </div>
      </div>
      
      <div class="summary-item">
        <div class="summary-label">分配状态</div>
        <div class="summary-value" :style="{color: currentTotalScore > randomExamForm.totalScore ? '#f56c6c' : currentTotalScore < randomExamForm.totalScore ? '#e6a23c' : '#67c23a'}">
          {{ currentTotalScore > randomExamForm.totalScore ? '超出' : currentTotalScore < randomExamForm.totalScore ? '未满' : '正好' }}
        </div>
      </div>
    </div>
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

/* 题目分布卡片样式 */
.distribution-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-left: 3px solid #409EFF;
  position: relative;
  transition: all 0.3s;
}

.distribution-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.distribution-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #e9ecef;
}

.distribution-card .card-title {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
  display: flex;
  align-items: center;
}

.distribution-card .card-title .type-icon {
  margin-right: 8px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #79bbff);
  color: white;
  font-size: 14px;
}

.distribution-card .card-body {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.distribution-card .delete-btn {
  position: absolute;
  top: 15px;
  right: 15px;
}

.distribution-summary {
  background-color: #f0f9ff;
  border-radius: 8px;
  padding: 15px;
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.summary-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
}

.add-distribution-btn {
  width: 100%;
  margin-top: 10px;
  border: 1px dashed #c0c4cc;
  background-color: #f8f9fa;
  transition: all 0.3s;
}

.add-distribution-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

:deep(.el-button) {
  border-radius: 4px;
  transition: all 0.3s;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #409EFF, #79bbff);
  border-color: #409EFF;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #66b1ff, #409EFF);
  border-color: #66b1ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transform: translateY(-1px);
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  border-color: #f56c6c;
  box-shadow: 0 2px 6px rgba(245, 108, 108, 0.2);
}

:deep(.el-button--danger:hover) {
  background: linear-gradient(135deg, #f78989, #f56c6c);
  border-color: #f78989;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
  transform: translateY(-1px);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 25px 0 15px;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 16px;
  background: linear-gradient(to bottom, #409EFF, #79bbff);
  margin-right: 8px;
  border-radius: 2px;
}
</style>