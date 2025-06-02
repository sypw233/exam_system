<template>
  <div id="register" class="register-container">
    <el-row class="main-container" type="flex" justify="center" align="middle" style="height: 100vh;">

      <el-card shadow="hover" class="register-box">
        <div class="top">
          <i class="el-icon-user"></i><span class="title">用户注册</span>
        </div>
        <!-- 注册表单 -->
        <el-form :model="form" ref="formData" label-width="80px">
          <!-- 用户名输入框 -->
          <el-form-item label="用户名" :rules="usernameRules">
            <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
          </el-form-item>

          <!-- 密码输入框 -->
          <el-form-item label="密码" :rules="passwordRules">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>

          <!-- 邮箱输入框 -->
          <el-form-item label="邮箱" :rules="emailRules">
            <el-input v-model="form.email" type="email" placeholder="请输入邮箱"></el-input>
          </el-form-item>

          <!-- 角色选择框 -->
          <el-form-item label="角色" :rules="roleRules">
            <el-select v-model="form.role" placeholder="请选择角色">
              <el-option label="学生" value="STUDENT"></el-option>
              <el-option label="教师" value="TEACHER"></el-option>
            </el-select>
          </el-form-item>

          <!-- 注册按钮 -->

        </el-form>
        <el-form-item>
          <el-button type="primary" @click="register">注册</el-button>
        </el-form-item>
        <!-- 登录链接 -->
        <p class="login-link">
          已经有账号?
          <router-link to="/">登录</router-link>
        </p>
      </el-card>

    </el-row>
  </div>
</template>

<script>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '@/api/axios';  // 引入配置好的 axios 实例

export default {
  name: 'Register',
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

    return {
      form,
      usernameRules,
      passwordRules,
      emailRules,
      roleRules,
      register
    };
  }
};
</script>

<style scoped>
/* 注册页面的样式 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5; /* 背景色 */
  height: 100vh; /* 屏幕高度 */
}

.register-box {
  background-color: #fff;
  padding: 30px;
  border-radius: 20px;
  width: 400px; /* 固定宽度 */
}
.register-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}
.top {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  margin-bottom: 20px; /* 顶部间距 */
}

.top i {
  font-size: 36px;
  margin-right: 10px; /* 图标与文字之间的间距 */
}

.title {
  font-weight: bold;
  font-size: 28px; /* 字体大小 */
}

.el-form-item {
  margin-bottom: 20px; /* 每个表单项的底部间距 */
}

.el-button {
  width: 100%; /* 按钮宽度占满父容器 */
  border-radius: 20px; /* 圆角按钮 */
  font-size: 16px; /* 字体大小 */
}

.login-link {
  text-align: center;
  margin-top: 20px; /* 登录链接的顶部间距 */
}

.login-link a {
  color: #409EFF; /* 链接颜色 */
  font-size: 16px; /* 链接字体大小 */
}
</style>
