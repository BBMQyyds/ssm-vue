package com.jdsbbmq.service.impl;

import cn.hutool.core.lang.UUID;
import com.jdsbbmq.dao.UserMapper;
import com.jdsbbmq.pojo.User;
import com.jdsbbmq.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    //登录
    @Override
    public User login(String account, String password) {
        User user = userMapper.login(account, password);
        if (user != null) {
            //生成cookie_id
            user.setCookie_id(UUID.randomUUID().toString());
            //开始时间
            Date start = new Date();
            //结束时间 30分钟后
            Date end = new Date(start.getTime() + 30 * 60 * 1000);
            //更新cookie
            userMapper.updateCookie(user.getCookie_id(), user.getId(), start, end);
            return user;
        } else {
            return new User();
        }
    }

    @Override
    //注册
    public Integer register(User user) {
        if (userMapper.isExist(user.getAccount()) != 0) {
            return 0;
        } else {
            user.buildUser();
            return userMapper.register(user);
        }
    }

    //用户列表
    @Override
    public List<User> userList(String account) {
        if (account.startsWith("a")) {
            return userMapper.userList();
        } else {
            return null;
        }
    }

    //删除用户
    @Override
    public Integer deleteUser(String id) {
        return userMapper.deleteUser(id);
    }

    //新增用户
    @Override
    public Integer addUser(User user) {
        if (userMapper.isExist(user.getAccount()) != 0) {
            return 0;
        } else {
            user.buildUser();
            return userMapper.register(user);
        }
    }

    //修改用户
    @Override
    public Integer updateUser(User user) {
        return userMapper.updateUser(user);
    }

    //获得用户名
    @Override
    public String getUserName(String account) {
        String id = userMapper.getUserID(account);
        return userMapper.getUserName(id);
    }
}
