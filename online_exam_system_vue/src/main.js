// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';  // 引入路由配置
import 'normalize.css/normalize.css';
// 导入 Element Plus 样式
import 'element-plus/dist/index.css';

const app = createApp(App);

app.use(router);  // 使用 Vue Router
app.mount('#app');
