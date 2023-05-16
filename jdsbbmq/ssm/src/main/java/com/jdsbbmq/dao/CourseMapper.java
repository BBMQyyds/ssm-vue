package com.jdsbbmq.dao;

import com.jdsbbmq.pojo.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {

    //可选课程列表
    List<Course> courseList(@Param("user_id") String id);

    //根据课程id获取课程信息
    Course selectCourseDetail(@Param("course_id") String id);

    //已申请课程列表
    List<Course> selectAppliedCourseList(@Param("user_id") String id);

    //根据课程id获取课程信息(包含进度)
    Course selectCourseDetailHavingProgress(@Param("course_id") String course_id, @Param("user_id") String user_id);

    //添加课程
    Integer addCourse(@Param("course") Course course);

    //删除课程
    Integer deleteCourse(@Param("id") String id);

    //修改课程
    Integer updateCourse(@Param("course") Course course);

    //查询课程列表
    List<Course> selectCourseList();

    //获得课程名
    String getCourseName(@Param("id") String id);
}
