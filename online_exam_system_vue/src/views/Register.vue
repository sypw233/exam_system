<template>
  <div id="register" class="register-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <el-row class="main-container" type="flex" justify="center" align="middle" style="height: 100vh;">
      <el-card shadow="hover" class="register-box">
        <!-- 头部标题区域 -->
        <div class="register-header">
          <div class="logo-container">
            <div class="logo-icon">
              <el-icon size="32"><User /></el-icon>
            </div>
          </div>
          <h2 class="register-title">在线考试系统</h2>
          <p class="register-subtitle">用户注册</p>
        </div>
        <!-- 注册表单 -->
        <el-form :model="form" ref="formData" class="register-form">
          <!-- 用户名输入框 -->
          <el-form-item :rules="usernameRules">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              class="register-input"
            ></el-input>
          </el-form-item>

          <!-- 密码输入框 -->
          <el-form-item :rules="passwordRules">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              class="register-input"
              show-password
            ></el-input>
          </el-form-item>

          <!-- 邮箱输入框 -->
          <el-form-item :rules="emailRules">
            <el-input 
              v-model="form.email" 
              type="email" 
              placeholder="请输入邮箱"
              prefix-icon="Message"
              size="large"
              class="register-input"
            ></el-input>
          </el-form-item>

          <!-- 角色选择框 -->
          <el-form-item :rules="roleRules">
            <el-select 
              v-model="form.role" 
              placeholder="请选择角色"
              size="large"
              class="register-select"
            >
              <el-option label="学生" value="STUDENT"></el-option>
              <el-option label="教师" value="TEACHER"></el-option>
            </el-select>
          </el-form-item>

          <!-- 注册按钮 -->
          <el-form-item class="register-button-item">
            <el-button 
              type="primary" 
              @click="register"
              size="large"
              class="register-button"
            >
              注册
            </el-button>
          </el-form-item>
          
          <!-- 登录链接 -->
          <el-form-item class="login-link-item">
            <el-button 
              type="success" 
              @click="goToLogin"
              size="large"
              class="login-link-button"
              plain
            >
              已有账号？立即登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

    </el-row>
  </div>
</template>

<script>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Message } from '@element-plus/icons-vue';
import api from '@/api/axios';  // 引入配置好的 axios 实例

export default {
  name: 'Register',
  components: {
    User,
    Lock,
    Message
  },
  setup() {
    const router = useRouter();  // 获取路由实例，用于页面跳转
    const form = ref({
      username: '',
      password: '',
      email: '',
      role: 'STUDENT',  // 默认角色为学生
    });

    // 表单验证规则
    const usernameRules = [
      {required: true, message: '请输入用户名', trigger: 'blur'},  // 用户名必填
    ];
    const passwordRules = [
      {required: true, message: '请输入密码', trigger: 'blur'},  // 密码必填
    ];
    const emailRules = [
      {required: true, message: '请输入邮箱', trigger: 'blur'},  // 邮箱必填
      {type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur'},  // 邮箱格式验证
    ];
    const roleRules = [
      {required: true, message: '请选择角色', trigger: 'change'},  // 角色必选
    ];

    // 注册请求
    const register = async () => {
      try {
        // 向后端发送注册请求
        const response = await api.post('/users/register', form.value);
        console.log('注册成功', response);
        await router.push('/');  // 注册成功后跳转到登录页
      } catch (error) {
        console.error('注册失败', error);
        // 捕获并处理错误响应
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(error.response.data.message);  // 显示错误信息
        } else {
          ElMessage.error('注册失败,请重试!');  // 默认错误消息
        }
      }
    };

    // 跳转到登录页面
    const goToLogin = () => {
      router.push('/');
    };

    return {
      form,
      usernameRules,
      passwordRules,
      emailRules,
      roleRules,
      register,
      goToLogin
    };
  }
};
</script>

<style scoped>
/* 注册页面的样式 */
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

/* 背景装饰元素 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.3;
  }
}

/* 主容器 */
.main-container {
  position: relative;
  z-index: 2;
}

/* 注册卡片 */
.register-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 24px;
  width: 100%;
  max-width: 420px;
  min-width: 380px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.register-box:hover {
  transform: translateY(-8px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

/* 头部区域 */
.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-container {
  margin-bottom: 16px;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  color: white;
  margin: 0 auto 16px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.logo-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

.register-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
  font-weight: 400;
}

/* 表单样式 */
.register-form {
  margin-top: 0;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.register-form :deep(.el-form-item__content) {
  line-height: normal;
}

/* 输入框样式 */
.register-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e1e8ed;
  transition: all 0.3s ease;
  padding: 12px 16px;
}

.register-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.register-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.register-input :deep(.el-input__inner) {
  font-size: 16px;
  color: #2c3e50;
}

.register-input :deep(.el-input__inner::placeholder) {
  color: #bdc3c7;
  font-size: 14px;
}

.register-input :deep(.el-input__prefix) {
  color: #7f8c8d;
}

/* 选择框样式 */
.register-select {
  width: 100%;
}

.register-select :deep(.el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e1e8ed;
  transition: all 0.3s ease;
  padding: 12px 16px;
}

.register-select :deep(.el-select__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.register-select :deep(.el-select__wrapper.is-focused) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.register-select :deep(.el-select__placeholder) {
  color: #bdc3c7;
  font-size: 14px;
}

/* 按钮样式 */
.register-button-item,
.login-link-item {
  margin-bottom: 16px;
}

.register-button {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.register-button:active {
  transform: translateY(0);
}

.login-link-button {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  border: 2px solid #667eea;
  color: #667eea;
  background: transparent;
  transition: all 0.3s ease;
}

.login-link-button:hover {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    padding: 20px;
  }
  
  .register-box {
    padding: 32px 24px;
    min-width: 320px;
    max-width: 100%;
    border-radius: 20px;
  }
  
  .register-title {
    font-size: 24px;
  }
  
  .register-subtitle {
    font-size: 14px;
  }
  
  .logo-icon {
    width: 56px;
    height: 56px;
  }
  
  .circle-1,
  .circle-2,
  .circle-3 {
    display: none;
  }
}

@media (max-width: 480px) {
  .register-box {
    padding: 24px 20px;
    min-width: 280px;
  }
  
  .register-title {
    font-size: 22px;
  }
}

/* 加载状态 */
.register-button.is-loading {
  pointer-events: none;
}

/* 焦点状态优化 */
.register-input :deep(.el-input__wrapper.is-focus) {
  outline: 2px solid rgba(102, 126, 234, 0.2);
  outline-offset: 2px;
}

.register-select :deep(.el-select__wrapper.is-focused) {
  outline: 2px solid rgba(102, 126, 234, 0.2);
  outline-offset: 2px;
}

/* 错误状态 */
.register-form :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #f56565;
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.15);
}

.register-form :deep(.el-form-item.is-error .el-input__wrapper:focus) {
  border-color: #f56565;
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.25);
}

.register-form :deep(.el-form-item.is-error .el-select__wrapper) {
  border-color: #f56565;
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.15);
}

.register-form :deep(.el-form-item.is-error .el-select__wrapper.is-focused) {
  border-color: #f56565;
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.25);
}
</style>
