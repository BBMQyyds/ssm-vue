package com.jdsbbmq.controller;

import com.jdsbbmq.pojo.User;
import com.jdsbbmq.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/login")
    @ResponseBody
    //登录
    public User login(@RequestBody User user, HttpServletResponse response) {
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
        return loggedInUser;
    }

    @RequestMapping(value = "/register")
    @ResponseBody
    //注册
    public Integer register(@RequestBody User user) {
        return userService.register(user);
    }

    //用户列表
    @RequestMapping(value = "/userListA")
    @ResponseBody
    public List<User> userList(@RequestBody String account) {
        return userService.userList(account);
    }

    //删除用户
    @RequestMapping(value = "/deleteUserA")
    @ResponseBody
    public Integer deleteUser(@RequestBody String id) {
        return userService.deleteUser(id);
    }

    //新增用户
    @RequestMapping(value = "/addUserA")
    @ResponseBody
    public Integer addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    //修改用户
    @RequestMapping(value = "/updateUserA")
    @ResponseBody
    public Integer updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    //获得用户名
    @RequestMapping(value = "/getUserName")
    @ResponseBody
    public String getUserName(@RequestBody String account) {
        return userService.getUserName(account);
    }

}
