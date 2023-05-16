package com.jdsbbmq.dao;

import com.jdsbbmq.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserMapper {

    //登录
    User login(@Param("account") String account, @Param("pwd") String pwd);

    //更新cookie
    void updateCookie(@Param("id") String id, @Param("user_id") String user_id, @Param("start") Date start, @Param("end") Date end);

    //判断登录状态
    String isLogin(@Param("id") String id, @Param("user_id") String user_id, @Param("date") Date date);

    //注册
    Integer register(@Param("user") User user);

    //判断是否存在
    Integer isExist(@Param("account") String account);

    //获得用户id
    String getUserID(@Param("account") String account);

    //获得用户名字
    String getUserName(@Param("id") String id);

    //用户列表
    List<User> userList();

    //删除用户
    Integer deleteUser(@Param("id") String id);

    //修改用户
    Integer updateUser(@Param("user") User user);

    //根据名字查找老师id
    String getTeacherID(@Param("name") String name);

}
