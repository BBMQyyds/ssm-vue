import axios from 'axios'
import {eventBus} from '@ai-zen/event-bus';

// 创建axios实例
const request = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: 'http://localhost:80/jdsbbmq_backend_war_exploded/',
    // 超时
    timeout: 10000,
    // 跨域请求时是否需要使用凭证
    withCredentials: true,
    // 设置Content-Type，规定了前后端的交互使用json
    headers: {'Content-Type': 'application/json;charset=utf-8'}
})

request.interceptors.response.use(response => {
    // 对响应数据做处理
    return response;
}, error => {
    // 对响应错误做处理
    if (error.response.status === 302) {
        // 处理302状态码的操作
        eventBus.emit('login');
    }
    if (error.response.status === 403) {
        // 处理403状态码的操作
        eventBus.emit('role');
    }
    return Promise.reject(error);
});

export default request
