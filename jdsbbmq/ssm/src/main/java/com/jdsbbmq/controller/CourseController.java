package com.jdsbbmq.controller;

import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class CourseController {

    @Resource
    private CourseService courseService;

    //可选课程列表
    @RequestMapping("/courseListS")
    @ResponseBody
    public List<Course> courseList(@RequestBody String account) {
        return courseService.courseList(account);
    }

    //根据课程id获取课程信息
    @RequestMapping("/courseDetailS")
    @ResponseBody
    public Course courseDetail(@RequestBody String id) {
        return courseService.courseDetail(id);
    }

    //已申请课程列表
    @RequestMapping("/appliedCourseListS")
    @ResponseBody
    public List<Course> applyCourseList(@RequestBody String account) {
        return courseService.appliedCourseList(account);
    }

    //根据课程id获取课程信息(包含进度)
    @RequestMapping("/courseDetailHavingProgressS")
    @ResponseBody
    public Course courseDetailHavingProgress(@RequestBody Application application) {
        return courseService.courseDetailHavingProgress(application.getCourse_id(), application.getAccount());
    }

    //查询课程列表
    @RequestMapping("/selectCourseListA")
    @ResponseBody
    public List<Course> selectCourseList(@RequestBody String account) {
        return courseService.selectCourseList(account);
    }

    //添加课程
    @RequestMapping("/addCourseA")
    @ResponseBody
    public Integer addCourse(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    //删除课程
    @RequestMapping("/deleteCourseA")
    @ResponseBody
    public Integer deleteCourse(@RequestBody String id) {
        return courseService.deleteCourse(id);
    }

    //修改课程
    @RequestMapping("/updateCourseA")
    @ResponseBody
    public Integer updateCourse(@RequestBody Course course) {
        return courseService.updateCourse(course);
    }

}
