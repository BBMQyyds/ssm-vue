package com.jdsbbmq.servlet;

import com.google.gson.Gson;
import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.service.CourseService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CourseServlet extends HttpServlet {
    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert applicationContext != null;
        courseService = applicationContext.getBean(CourseService.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        switch (pathInfo) {
            case "/courseListS" -> courseList(request, response);
            case "/courseDetailS" -> courseDetail(request, response);
            case "/appliedCourseListS" -> appliedCourseList(request, response);
            case "/courseDetailHavingProgressS" -> courseDetailHavingProgress(request, response);
            case "/selectCourseListA" -> selectCourseList(request, response);
            case "/addCourseA" -> addCourse(request, response);
            case "/deleteCourseA" -> deleteCourse(request, response);
            case "/updateCourseA" -> updateCourse(request, response);
        }
    }

    // 获取课程列表
    private void courseList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用CourseService的courseList方法
        List<Course> result = courseService.courseList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    // 获取课程详情
    private void courseDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String id = new Gson().fromJson(requestBody, String.class);

        // 调用CourseService的courseDetail方法
        Course result = courseService.courseDetail(id);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    // 获取已申请课程列表
    private void appliedCourseList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用CourseService的appliedCourseList方法
        List<Course> result = courseService.appliedCourseList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    // 根据课程id获取课程信息(包含进度)
    private void courseDetailHavingProgress(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Application application = new Gson().fromJson(requestBody, Application.class);

        // 调用CourseService的courseDetailHavingProgress方法
        Course result = courseService.courseDetailHavingProgress(application.getCourse_id(), application.getAccount());

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    // 查询课程列表
    private void selectCourseList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用CourseService的selectCourseList方法
        List<Course> result = courseService.selectCourseList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    // 添加课程
    private void addCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Course course = new Gson().fromJson(requestBody, Course.class);

        // 调用CourseService的addCourse方法
        int result = courseService.addCourse(course);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 删除课程
    private void deleteCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String id = new Gson().fromJson(requestBody, String.class);

        // 调用CourseService的deleteCourse方法
        int result = courseService.deleteCourse(id);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 修改课程
    private void updateCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Course course = new Gson().fromJson(requestBody, Course.class);

        // 调用CourseService的updateCourse方法
        int result = courseService.updateCourse(course);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

}
