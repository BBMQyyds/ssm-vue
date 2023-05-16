package com.jdsbbmq.controller;

import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.pojo.Role;
import com.jdsbbmq.pojo.User;
import com.jdsbbmq.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class RoleController {

    @Resource
    private RoleService roleService;

    //添加权限（教师与课程的关系）
    @RequestMapping("/addOrUpdateRole")
    @ResponseBody
    public int addOrUpdateRole(@RequestBody Role role) {
        return roleService.addOrUpdateRole(role);
    }

    //获取权限信息
    @RequestMapping("/roleListT")
    @ResponseBody
    public List<Role> roleList(@RequestBody String account) {
        return roleService.roleList(account);
    }

    //审批流
    @RequestMapping("/courseFlowA")
    @ResponseBody
    public Course courseFlow(@RequestBody User user) {
        return roleService.courseFlow(user);
    }

    //提交审批流
    @RequestMapping("/submitFlowA")
    @ResponseBody
    public int submitFlow(@RequestBody Course course) {
        return roleService.submitFlow(course);
    }
}
