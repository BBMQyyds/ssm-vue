package com.jdsbbmq.service;

import com.jdsbbmq.pojo.Approval;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.pojo.Role;
import com.jdsbbmq.pojo.User;

import java.util.List;

public interface RoleService {

    //添加权限（教师与课程的关系）
    int addOrUpdateRole(Role role);

    //获取权限信息
    List<Role> roleList(String account);

    //审批流
    Course courseFlow(User user);

    //提交审批流
    int submitFlow(Course course);
}
