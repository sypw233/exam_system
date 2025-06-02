<template>
  <div id="login" class="login-container">
    <!-- 使用 Element Plus 的 Grid 系统，居中对齐 -->
    <el-row class="main-container" type="flex" justify="center" align="middle" style="height: 100vh;">

      <el-card  shadow="hover" class="login-box">
        <div class="top">
          <i class="el-icon-user"></i><span class="title">用户登录</span>
        </div>
        <br>
        <!-- 登录表单 -->
        <el-form :model="form" ref="formData" label-width="80px">
          <!-- 用户名 -->
          <el-form-item label="用户名" :rules="usernameRules">
            <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <!-- 密码 -->
          <el-form-item label="密码" :rules="passwordRules">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <!-- 登录按钮 -->

        </el-form>
        <el-form-item>
          <el-button type="primary" @click="login">登录</el-button>
        </el-form-item>
        <!-- 注册按钮 -->
        <el-form-item>
          <el-button type="success" @click="goToRegister">注册</el-button>
        </el-form-item>
      </el-card>

    </el-row>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '@/api/axios';  // 导入封装好的 API 请求

export default {
  name: 'Login',
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
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;  /* 背景色 */
}

.login-box {
  background-color: #fff;
  padding: 20px;
  border-radius: 20px;
  width: 100%;
  max-width: 400px;
  min-width: 400px;  /* 设置最小宽度 */

}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}
.top {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;

}

.title {
  font-weight: bold;
  font-size: 24px;
}

 .el-input__inner {
   border-radius: 10px !important;
 }

.el-form-item {
  margin-bottom: 20px;  /* 表单项底部间距 */
}

.el-button {
  width: 100%;  /* 按钮宽度占满父容器 */
}

.register {
  text-align: center;
  margin-top: 20px;
}

.el-button {
  border-radius: 10px;  /* 按钮圆角 */
}

.register a {
  color: #409EFF;  /* 链接颜色 */
}
</style>
