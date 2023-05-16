package com.jdsbbmq.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import com.jdsbbmq.dao.CourseMapper;
import com.jdsbbmq.dao.RoleMapper;
import com.jdsbbmq.dao.UserMapper;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.pojo.Role;
import com.jdsbbmq.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    //可选课程列表
    @Override
    public List<Course> courseList(String account) {
        if (account.startsWith("s")) {
            String id = userMapper.getUserID(account);
            return courseMapper.courseList(id);
        } else {
            return null;
        }
    }

    //根据课程id获取课程信息
    @Override
    public Course courseDetail(String id) {
        return courseMapper.selectCourseDetail(id);
    }

    //已申请课程列表
    @Override
    public List<Course> appliedCourseList(String account) {
        if (account.startsWith("s")) {
            String id = userMapper.getUserID(account);
            return courseMapper.selectAppliedCourseList(id);
        } else {
            return null;
        }
    }

    //根据课程id获取课程信息(包含进度)
    @Override
    public Course courseDetailHavingProgress(String course_id, String account) {
        String user_id = userMapper.getUserID(account);
        Course course = courseMapper.selectCourseDetailHavingProgress(course_id, user_id);
        if (course.getProof_path() != null) {
            course.setProof_path(MD5.create().digestHex16(course.getProof_path()));
        }
        return course;
    }

    //添加课程
    @Override
    public Integer addCourse(Course course) {
        course.setId(UUID.randomUUID().toString());
        course.setCreate_time(new Date());
        return courseMapper.addCourse(course);
    }

    //删除课程
    @Override
    public Integer deleteCourse(String id) {
        return courseMapper.deleteCourse(id);
    }

    //修改课程
    @Override
    @Transactional
    public Integer updateCourse(Course course) {
        course.setSpeaker_id(userMapper.getTeacherID(course.getSpeaker_name()));
        course.setSupervisor_id(userMapper.getTeacherID(course.getSupervisor_name()));
        if (course.getSpeaker_id() == null || course.getSupervisor_id() == null) {
            return null;
        } else {
            int success = courseMapper.updateCourse(course);
            if (success == 1) {
                //主讲,主管老师
                Role role0 = course.getApprovers().get(0);
                Role role1 = course.getApprovers().get(1);
                String id0 = roleMapper.getRoleID(role0);
                String id1 = roleMapper.getRoleID(role1);
                if (id0 != null && id1 != null) {
                    role0.setId(id0);
                    role1.setId(id1);
                    int del = roleMapper.deleteRole(role0.getId());
                    if (del == 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return 0;
                    }
                    del = roleMapper.deleteRole(role1.getId());
                    if (del == 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return 0;
                    }
                }
                //主讲,主管老师
                role0.buildRole(userMapper.getTeacherID(role0.getTeacher_name()));
                role1.buildRole(userMapper.getTeacherID(role1.getTeacher_name()));
                if (role0.getTeacher_id() == null || role1.getTeacher_id() == null) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return 0;
                }
                roleMapper.addRole(role0);
                roleMapper.addRole(role1);
                return 1;
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return 0;
            }
        }
    }

    //查询课程列表
    @Override
    public List<Course> selectCourseList(String account) {
        if (account.startsWith("a")) {
            return courseMapper.selectCourseList();

        } else {
            return null;
        }
    }
}
