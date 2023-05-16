package com.jdsbbmq.service;

import com.jdsbbmq.pojo.Course;

import java.util.List;

public interface CourseService {

    //可选课程列表
    List<Course> courseList(String account);

    //根据课程id获取课程信息
    Course courseDetail(String id);

    //已申请课程列表
    List<Course> appliedCourseList(String account);

    //根据课程id获取课程信息(包含进度)
    Course courseDetailHavingProgress(String course_id, String account);

    //添加课程
    Integer addCourse(Course course);

    //删除课程
    Integer deleteCourse(String id);

    //修改课程
    Integer updateCourse(Course course);

    //查询课程列表
    List<Course> selectCourseList(String account);
}
