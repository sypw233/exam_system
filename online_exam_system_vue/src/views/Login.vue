<template>
  <div id="login" class="login-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <!-- 使用 Element Plus 的 Grid 系统，居中对齐 -->
    <el-row class="main-container" type="flex" justify="center" align="middle" style="height: 100vh;">
      <el-card shadow="hover" class="login-box">
        <!-- 头部标题区域 -->
        <div class="login-header">
          <div class="logo-container">
            <div class="logo-icon">
              <el-icon size="32"><User /></el-icon>
            </div>
          </div>
          <h2 class="login-title">在线考试系统</h2>
          <p class="login-subtitle">欢迎登录</p>
        </div>
        
        <!-- 登录表单 -->
        <el-form :model="form" ref="formData" class="login-form" @keyup.enter="login">
          <!-- 用户名 -->
          <el-form-item :rules="usernameRules">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              class="login-input"
            ></el-input>
          </el-form-item>
          
          <!-- 密码 -->
          <el-form-item :rules="passwordRules">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              class="login-input"
              show-password
              @keyup.enter="login"
            ></el-input>
          </el-form-item>
          
          <!-- 登录按钮 -->
          <el-form-item class="login-button-item">
            <el-button 
              type="primary" 
              @click="login" 
              size="large"
              class="login-button"
              :loading="false"
            >
              登录
            </el-button>
          </el-form-item>
          
          <!-- 注册按钮 -->
          <el-form-item class="register-button-item">
            <el-button 
              type="success" 
              @click="goToRegister"
              size="large"
              class="register-button"
              plain
            >
              注册账号
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-row>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import api from '@/api/axios';  // 导入封装好的 API 请求

export default {
  name: 'Login',
  components: {
    User,
    Lock
  },
  setup() {
    const router = useRouter();  // Vue Router，用于页面跳转
    const form = ref({
      username: '',
      password: '',
      role: ''
    });

    // 表单验证规则
    const usernameRules = [
      { required: true, message: '请输入用户名', trigger: 'blur' }
    ];

    const passwordRules = [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ];

    // 检查是否有有效的 token，若有，则根据角色自动跳转
    const token = localStorage.getItem('token');
    if (token) {
      const role = localStorage.getItem('role');
      switch (role.toUpperCase()) {
        case 'STUDENT':
          router.push('/student-home');
          break;
        case 'TEACHER':
        case 'ADMIN':
          router.push('/dashboard');
          break;
      }
    }

    // 登录方法
    const login = async () => {
      try {
        // 发起登录请求
        const response = await api.post('/users/login', {
          username: form.value.username,
          password: form.value.password,
        });
        const { token, username, role, id } = response.data;
        // 登录成功后保存 token 和用户信息
        localStorage.setItem('token', token);
        localStorage.setItem('username', username);
        localStorage.setItem('role', role.toUpperCase());
        localStorage.setItem('id', id);
        ElMessage.success("登录成功!");

        // 根据用户角色跳转到相应的页面
        switch (role.toUpperCase()) {
          case 'STUDENT':
            await router.push('/student-home');
            break;
          case 'TEACHER':
          case 'ADMIN':
            await router.push('/dashboard');
            break;
        }
      } catch (error) {
        console.error('登录失败', error);
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(error.response.data.message);  // 显示错误信息
        } else {
          ElMessage.error('登录失败,请重试');
        }
      }
    };

    // 跳转到注册页面
    const goToRegister = () => {
      router.push('/register');
    };

    return {
      form,
      usernameRules,
      passwordRules,
      login,
      goToRegister
    };
  }
};
</script>

<style scoped>
/* 登录页面的样式 */
.login-container {
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

/* 登录卡片 */
.login-box {
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

.login-box:hover {
  transform: translateY(-8px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

/* 头部区域 */
.login-header {
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

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 16px;
  color: #7f8c8d;
  margin: 0;
  font-weight: 400;
}

/* 表单样式 */
.login-form {
  margin-top: 0;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item__content) {
  line-height: normal;
}

/* 输入框样式 */
.login-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e1e8ed;
  transition: all 0.3s ease;
  padding: 12px 16px;
}

.login-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.login-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.login-input :deep(.el-input__inner) {
  font-size: 16px;
  color: #2c3e50;
}

.login-input :deep(.el-input__inner::placeholder) {
  color: #bdc3c7;
  font-size: 14px;
}

.login-input :deep(.el-input__prefix) {
  color: #7f8c8d;
}

/* 按钮样式 */
.login-button-item,
.register-button-item {
  margin-bottom: 16px;
}

.login-button {
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

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.register-button {
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

.register-button:hover {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }
  
  .login-box {
    padding: 32px 24px;
    min-width: 320px;
    max-width: 100%;
    border-radius: 20px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .login-subtitle {
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
  .login-box {
    padding: 24px 20px;
    min-width: 280px;
  }
  
  .login-title {
    font-size: 22px;
  }
}

/* 加载状态 */
.login-button.is-loading {
  pointer-events: none;
}

/* 焦点状态优化 */
.login-input :deep(.el-input__wrapper.is-focus) {
  outline: 2px solid rgba(102, 126, 234, 0.2);
  outline-offset: 2px;
}

/* 错误状态 */
.login-form :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #f56565;
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.15);
}

.login-form :deep(.el-form-item.is-error .el-input__wrapper:focus) {
  border-color: #f56565;
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.25);
}
</style>
