package com.jdsbbmq.filter;

import com.jdsbbmq.dao.UserMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //鉴权逻辑
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().contains("login") || req.getRequestURI().contains("register")
                || req.getRequestURI().equals("/jdsbbmq_backend_war_exploded/")) {
            chain.doFilter(request, response);
            return;
        }

        Cookie[] cookies;
        if (req.getCookies() == null) {
            cookies = new Cookie[0];
        } else {
            cookies = req.getCookies();
        }

        String id = null;
        String user_id = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("id")) {
                id = cookie.getValue();
            } else if (cookie.getName().equals("user_id")) {
                user_id = cookie.getValue();
            }
        }

        //判断登录状态
        if (id == null || user_id == null) {
            //未登录，res返回302状态码，前端跳转到登录页面
            res.setStatus(302);
            return;
        }

        //判断登录状态
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        UserMapper userMapper = context.getBean(UserMapper.class);
        String account = userMapper.isLogin(id, user_id, new Date());
        if (account == null) {
            //未登录，res返回302状态码，前端跳转到登录页面
            res.setStatus(302);
            return;
        }

        //判断权限
        String url = req.getRequestURI();

        if (!url.endsWith("S") && !url.endsWith("T") && !url.endsWith("A")) {
            chain.doFilter(request, response);
            return;
        }

        if (url.endsWith("S") && !account.startsWith("s")) {
            //权限不足，res返回403状态码，前端跳转到403页面
            res.setStatus(403);
            return;
        }

        if (url.endsWith("T") && !account.startsWith("t")) {
            //权限不足，res返回403状态码，前端跳转到403页面
            res.setStatus(403);
            return;
        }

        if (url.endsWith("A") && !account.startsWith("a")) {
            //权限不足，res返回403状态码，前端跳转到403页面
            res.setStatus(403);
            return;
        }

        //放行
        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) {
        // 初始化方法
    }

    public void destroy() {
        // 销毁方法
    }
}
