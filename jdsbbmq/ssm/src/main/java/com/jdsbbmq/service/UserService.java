package com.jdsbbmq.service;

import com.jdsbbmq.pojo.User;

import java.util.List;

public interface UserService {

    //登录
    User login(String account, String password);

    //注册
    Integer register(User user);

    //用户列表
    List<User> userList(String account);

    //删除用户
    Integer deleteUser(String id);

    //新增用户
    Integer addUser(User user);

    //修改用户
    Integer updateUser(User user);

    //获得用户名
    String getUserName(String account);
}
