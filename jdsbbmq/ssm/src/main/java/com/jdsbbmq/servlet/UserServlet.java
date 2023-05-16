package com.jdsbbmq.servlet;

import com.google.gson.Gson;
import com.jdsbbmq.pojo.User;
import com.jdsbbmq.service.UserService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert applicationContext != null;
        userService = applicationContext.getBean(UserService.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        switch (pathInfo) {
            case "/login" -> login(request, response);
            case "/register" -> register(request, response);
            case "/userListA" -> userList(request, response);
            case "/deleteUserA" -> deleteUser(request, response);
            case "/updateUserA" -> updateUser(request, response);
            case "/addUserA" -> addUser(request, response);
            case "/getUserName" -> getUserName(request, response);
        }
    }

    // 登录
    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        User user = new Gson().fromJson(requestBody, User.class);

        // 调用UserService的login方法
        User loggedInUser = userService.login(user.getAccount(), user.getPwd());
        if (loggedInUser != null) {
            //id存入cookie
            Cookie id = new Cookie("id", loggedInUser.getCookie_id());
            id.setMaxAge(30 * 60); // 设置Cookie的有效期为半小时（根据需求进行调整）
            id.setDomain("localhost"); // 设置Cookie的域名
            id.setPath("/"); // 设置Cookie的路径
            //user_id存入cookie
            Cookie user_id = new Cookie("user_id", loggedInUser.getId());
            user_id.setMaxAge(30 * 60); // 设置Cookie的有效期为半小时（根据需求进行调整）
            user_id.setDomain("localhost"); // 设置Cookie的域名
            user_id.setPath("/"); // 设置Cookie的路径
            //添加cookie
            response.addCookie(id);
            response.addCookie(user_id);
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(loggedInUser));
    }

    // 注册
    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        User user = new Gson().fromJson(requestBody, User.class);

        // 调用UserService的register方法
        int result = userService.register(user);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 获取用户列表
    private void userList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用UserService的userList方法
        List<User> userList = userService.userList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(userList));
    }

    // 删除用户
    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String id = new Gson().fromJson(requestBody, String.class);

        // 调用UserService的deleteUser方法
        int result = userService.deleteUser(id);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 更新用户
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        User user = new Gson().fromJson(requestBody, User.class);

        // 调用UserService的updateUser方法
        int result = userService.updateUser(user);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 添加用户
    private void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        User user = new Gson().fromJson(requestBody, User.class);

        // 调用UserService的addUser方法
        int result = userService.addUser(user);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 获取用户名
    private void getUserName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用UserService的getUserName方法
        String userName = userService.getUserName(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userName);
    }
}
