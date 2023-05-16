package com.jdsbbmq.service.impl;

import com.jdsbbmq.dao.RoleMapper;
import com.jdsbbmq.dao.UserMapper;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.pojo.Role;
import com.jdsbbmq.pojo.User;
import com.jdsbbmq.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public int addOrUpdateRole(Role role) {
        String id = roleMapper.getRoleID(role);
        if (id != null) {
            role.setId(id);
            int del = roleMapper.deleteRole(role.getId());
            if (del == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return 0;
            }
        }
        role.buildRole(userMapper.getTeacherID(role.getTeacher_name()));
        if (role.getTeacher_id() == null) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
        return roleMapper.addRole(role);
    }

    //获取权限信息
    @Override
    public List<Role> roleList(String account) {
        String teacher_id = userMapper.getUserID(account);
        return roleMapper.roleList(teacher_id);
    }

    //审批流
    @Override
    public Course courseFlow(User user) {
        if (user.getAccount().startsWith("a")) {
            return roleMapper.courseFlow(user.getCourse_id());
        } else {
            return null;
        }
    }

    //提交审批流
    @Override
    @Transactional
    public int submitFlow(Course course) {
        //提交前先删除原有的审批流（第三以上）
        roleMapper.deleteRoleByCourseID(course.getId());
        List<Role> roles = course.getApprovers();
        int success = 1;
        for (Role role : roles) {
            String id = roleMapper.getRoleID(role);
            if (id != null) {
                role.setId(id);
                int del = roleMapper.deleteRole(role.getId());
                if (del == 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return 0;
                }
            }
            role.buildRole(userMapper.getTeacherID(role.getTeacher_name()));
            if (role.getTeacher_id() == null) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return 0;
            }
            int res = roleMapper.addRole(role);
            if (res == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return 0;
            }
        }
        return success;
    }

}
