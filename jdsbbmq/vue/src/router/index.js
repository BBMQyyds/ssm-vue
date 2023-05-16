// 导入用来创建路由和确定路由模式的两个方法
import {createRouter, createWebHistory} from 'vue-router'

/**
 * 定义路由信息
 *
 */
const routes = [
    {
        path: '/',
        redirect: '/login',
        component: () => import('@/views/login'),
        meta: {keepAlive: false}
    },
    {
        name: 'login',
        path: '/login',
        component: () => import('@/views/login'),
        meta: {keepAlive: false}
    },
    {
        name: 'register',
        path: '/register',
        component: () => import('@/views/register'),
        meta: {keepAlive: false}
    },
    {
        name: 'main',
        path: '/main',
        component: () => import('@/views/main'),
        meta: {keepAlive: false},
        children: [
            {
                name: 'apply',
                path: '/apply',
                component: () => import('@/components/student/apply'),
                meta: {keepAlive: false}
            },
            {
                name: 'application',
                path: '/application',
                component: () => import('@/components/student/application'),
                meta: {keepAlive: false}
            },
            {
                name: 'applicationDetail',
                path: '/applicationDetail',
                component: () => import('@/components/student/applicationDetail'),
                meta: {keepAlive: false}
            },
            {
                name: 'courses',
                path: '/courses',
                component: () => import('@/components/student/courses'),
                meta: {keepAlive: false}
            },
            {
                name: 'approve',
                path: '/approve',
                component: () => import('@/components/teacher/approve'),
                meta: {keepAlive: false}
            },
            {
                name: 'approved',
                path: '/approved',
                component: () => import('@/components/teacher/approved'),
                meta: {keepAlive: false}
            },
            {
                name: 'roleList',
                path: '/roleList',
                component: () => import('@/components/teacher/roleList'),
                meta: {keepAlive: false}
            },
            {
                name: 'userManage',
                path: '/userManage',
                component: () => import('@/components/administrator/userManage'),
                meta: {keepAlive: false}
            },
            {
                name: 'user',
                path: '/userManage/user',
                component: () => import('@/components/components/user'),
                meta: {keepAlive: false}
            },
            {
                name: 'courseManage',
                path: '/courseManage',
                component: () => import('@/components/administrator/courseManage'),
                meta: {keepAlive: false}
            },
            {
                name: 'course',
                path: '/courseManage/course',
                component: () => import('@/components/components/course'),
                meta: {keepAlive: false}
            },
            {
                name: 'queryApproval',
                path: '/queryApproval',
                component: () => import('@/components/administrator/queryApproval'),
                meta: {keepAlive: false}
            },
            {
                name: 'queryApplication',
                path: '/queryApplication',
                component: () => import('@/components/administrator/queryApplication'),
                meta: {keepAlive: false}
            },
            {
                name: 'flow',
                path: '/courseManage/flow',
                component: () => import('@/components/components/flow'),
                meta: {keepAlive: false}
            },
            {
                name: 'about',
                path: '/about',
                component: () => import('@/components/about'),
                meta: {keepAlive: false}

            },
            {
                name: 'notFound',
                path: '/notFound',
                component: () => import('@/components/notFound'),
                meta: {keepAlive: false}
            },
        ]
    },
    {
        name: 'notFound',
        path: '/:pathMatch(.*)*',
        component: () => import('@/components/notFound'),
        meta: {keepAlive: false}
    }
]

// 创建路由实例并传递 `routes` 配置
// 我们在这里使用 html5 的路由模式，url中不带有#，部署项目的时候需要注意。
const router = createRouter({
    history: createWebHistory(),
    routes,
})

// 全局的路由守卫,会在每次路由进行跳转的时候执行
router.beforeEach((to) => {
        if (sessionStorage.getItem('account') !== null && sessionStorage.getItem('pwd') !== null) {
            return true;
        } else {
            if (to.name === 'login' || to.name === 'register') {
                return true;
            } else {
                return {name: 'login'}
            }
        }
    }
)

// 讲路由实例导出
export default router