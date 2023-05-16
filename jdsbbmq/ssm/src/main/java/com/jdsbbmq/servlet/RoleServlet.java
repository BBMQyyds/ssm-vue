package com.jdsbbmq.servlet;

import com.google.gson.Gson;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.pojo.Role;
import com.jdsbbmq.pojo.User;
import com.jdsbbmq.service.RoleService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RoleServlet extends HttpServlet {

    private RoleService roleService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert applicationContext != null;
        roleService = applicationContext.getBean(RoleService.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        switch (pathInfo) {
            case "/addOrUpdateRole" -> addOrUpdateRole(request, response);
            case "/roleListT" -> roleList(request, response);
            case "/courseFlowA" -> courseFlow(request, response);
            case "/submitFlowA" -> submitFlow(request, response);
        }
    }

    // 添加或更新角色
    private void addOrUpdateRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Role role = new Gson().fromJson(requestBody, Role.class);

        // 调用RoleService的addOrUpdateRole方法
        int result = roleService.addOrUpdateRole(role);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 获取角色列表
    private void roleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用RoleService的roleList方法
        List<Role> roleList = roleService.roleList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(roleList));
    }

    // 获取课程流程
    private void courseFlow(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        User user = new Gson().fromJson(requestBody, User.class);

        // 调用RoleService的courseFlow方法
        Course course = roleService.courseFlow(user);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(course));
    }

    // 提交课程流程
    private void submitFlow(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Course course = new Gson().fromJson(requestBody, Course.class);

        // 调用RoleService的submitFlow方法
        int result = roleService.submitFlow(course);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }
}

