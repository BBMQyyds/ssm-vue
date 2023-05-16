package com.jdsbbmq.servlet;

import com.google.gson.Gson;
import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.service.ApplicationService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationServlet extends HttpServlet {

    private ApplicationService applicationService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert applicationContext != null;
        applicationService = applicationContext.getBean(ApplicationService.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        switch (pathInfo) {
            case "/applyCourseS" -> applyCourse(request, response);
            case "/applyListT" -> applyList(request, response);
            case "/deleteApplicationS" -> deleteApplication(request, response);
            case "/updateApplicationS" -> updateApplication(request, response);
            case "/applyListAllA" -> applyListAll(request, response);
            case "/getProof" -> getProof(request, response);
        }
    }

    private void applyCourse(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 将HttpServletRequest转化为MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest(request);

        // 获取请求体中的数据
        String account = multipartRequest.getParameter("account");
        String courseId = multipartRequest.getParameter("course_id");
        String reason = multipartRequest.getParameter("reason");
        MultipartFile file = multipartRequest.getFile("file");

        Application application = new Application();
        application.setAccount(account);
        application.setCourse_id(courseId);
        application.setReason(reason);
        application.setFile(file);

        // 调用RoleService的addOrUpdateRole方法
        int result = applicationService.applyCourse(application);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    private void applyList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用ApplicationService的applyList方法
        List<Application> result = applicationService.applyList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    private void deleteApplication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String application_id = new Gson().fromJson(requestBody, String.class);

        // 调用ApplicationService的deleteApplication方法
        int result = applicationService.deleteApplication(application_id);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    private void updateApplication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Application application = new Gson().fromJson(requestBody, Application.class);

        // 调用ApplicationService的updateApplication方法
        int result = applicationService.updateApplication(application);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    private void applyListAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用ApplicationService的applyListAll方法
        List<Course> result = applicationService.applyListAll(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    private void getProof(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String id = new Gson().fromJson(requestBody, String.class);

        // 调用 ApplicationService 的 getProof 方法
        MultipartFile proof = applicationService.getProof(id);

        if (proof == null) {
            // 返回空响应
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            // 设置响应头
            response.setContentType(proof.getContentType());
            response.setContentLengthLong(proof.getSize());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + proof.getOriginalFilename() + "\"");

            // 将文件内容写入响应输出流
            try (InputStream inputStream = proof.getInputStream();
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }

}
