package com.jdsbbmq.dao;

import com.jdsbbmq.pojo.Approval;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.pojo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {

    //添加权限（教师与课程的关系）
    int addRole(@Param("role") Role role);

    //删除权限（教师与课程的关系）
    int deleteRole(@Param("id") String id);

    //查询权限id
    String getRoleID(@Param("role") Role role);

    //获取权限信息
    List<Role> roleList(@Param("teacher_id") String teacher_id);

    //审批流
    Course courseFlow(@Param("id") String id);

    //获得审批人列表
    List<Role> getApproverList(@Param("course_id") String course_id);

    //提交前先删除原有的审批流（第三以上）
    int deleteRoleByCourseID(@Param("course_id") String course_id);
}
