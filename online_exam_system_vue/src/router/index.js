import {createRouter, createWebHistory} from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import answer from "@/views/student/Answer.vue";
import ExamSubmissionManager from "@/views/Dashboard/ExamSubmissionManager.vue";
import StudentHome from "@/views/student/StudentHome.vue";
import Dashboard from "@/views/Dashboard/Dashboard.vue";
import UserManager from "@/views/Dashboard/UserManager.vue";
import QuestionManager from "@/views/Dashboard/QuestionManager.vue";
import ExamManager from "@/views/Dashboard/ExamManager.vue";
import AnswerResult from "@/views/student/AnswerResult.vue";
import error404 from "@/views/error404.vue";
import CourseManager from "@/views/Dashboard/CourseManager.vue";

const routes = [
    // 登录注册
    {path: '/', name: 'Login', component: Login},
    {path: '/register', name: 'Register', component: Register},
    {
        // 后台
        path: '/dashboard',
        name: 'dashboard',
        component: Dashboard,
        meta: {requiresAuth: true, roles: ['ADMIN', 'TEACHER']},
        children: [
            {path: '/dashboard/user', name: 'user-manger', component: UserManager, meta: {roles: ['ADMIN']}},
            {
                path: '/dashboard/exam',
                name: 'exam-manager',
                component: ExamManager,
                meta: {roles: ['ADMIN', 'TEACHER']}
            },
            {
                path: '/dashboard/question',
                name: 'question-manager',
                component: QuestionManager,
                meta: {roles: ['ADMIN', 'TEACHER']}
            },
            {
                path: '/dashboard/exam-submission',
                name: 'exam-submission-manager',
                component: ExamSubmissionManager,
                meta: {roles: ['ADMIN', 'TEACHER']}
            },
            {
                path: '/dashboard/',
                name: 'course-manager',
                component: CourseManager,
                meta: {roles: ['ADMIN', 'TEACHER']}
            },
        ]
    },
    // 学生界面
    {path: '/answer', name: 'answer', component: answer, meta: {requiresAuth: true, roles: ['STUDENT']}},
    {
        path: '/answer/result',
        name: 'answer_result',
        component: AnswerResult,
        meta: {requiresAuth: true, roles: ['STUDENT']}
    },
    {
        path: '/student-home',
        name: 'student-home',
        component: StudentHome,
        meta: {requiresAuth: true, roles: ['STUDENT']}
    },
    // { path: '/page-test', name: 'page-home', component: page_home, children: [
    //         { path: '/page1', name: 'page1', component: page1 },
    //         { path: '/page2', name: 'page2', component: page2 },
    //     ]},
    {path: '/:pathMatch(.*)*', name: '404', component: error404},
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
});

// 全局路由守卫
router.beforeEach((to, from, next) => {
    const userRole = localStorage.getItem('role'); // 获取用户角色
    const requiresAuth = to.meta.requiresAuth;
    const allowedRoles = to.meta.roles;

    // 如果需要认证且用户没有登录
    if (requiresAuth && !userRole) {
        next({name: 'Login'});
        return;
    }

    // 如果用户角色不在允许的角色列表中，跳转到学生首页
    if (requiresAuth && allowedRoles && !allowedRoles.includes(userRole)) {
        if (userRole === 'STUDENT') {
            next({name: 'student-home'}); // 学生跳转到学生首页
        } else {
            next({name: 'Login'}); // 未登录或没有权限，跳转到登录页
        }
        return;
    }

    // 默认允许访问
    next();
});

export default router;
