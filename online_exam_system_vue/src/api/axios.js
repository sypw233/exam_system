// src/api/axios.js
import axios from 'axios';
import router from "@/router/index.js";

const api = axios.create({
    // baseURL: 'http://api.exam.lililili.icu',  // 设置后端 API 地址
    baseURL: 'http://localhost:9999/api',  // 设置后端 API 地址
    timeout: 10000,  // 设置超时时间
});

api.interceptors.request.use(
    config => {
        // 如果存在 token, 添加 Authorization 头部
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        router.push('/').then(r => alert("登录失效,请重新登录!"));
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
        // 处理正常响应
        return response;
    },
    (error) => {
        if (error.response.status === 500) {
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            localStorage.removeItem('role');
            router.push('/').then(r => alert('请重新登录后再试!'));
        }
        return Promise.reject(error);
    }
);

export default api;
