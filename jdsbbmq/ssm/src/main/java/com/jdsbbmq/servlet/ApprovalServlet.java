package com.jdsbbmq.servlet;

import com.google.gson.Gson;
import com.jdsbbmq.pojo.Approval;
import com.jdsbbmq.service.ApprovalService;
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

public class ApprovalServlet extends HttpServlet {

    private ApprovalService approvalService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        assert applicationContext != null;
        approvalService = applicationContext.getBean(ApprovalService.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        switch (pathInfo) {
            case "/approvalListT" -> approvalList(request, response);
            case "/passApplicationT" -> passApplication(request, response);
            case "/failApplicationT" -> failApplication(request, response);
            case "/deleteApprovalT" -> deleteApproval(request, response);
            case "/updateApprovalT" -> updateApproval(request, response);
            case "/approvalListAllA" -> approvalListAll(request, response);
        }
    }

    // 审批列表
    private void approvalList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用approvalService的approvalList方法
        List<Approval> result = approvalService.approvalList(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }

    // 通过申请
    private void passApplication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Approval approval = new Gson().fromJson(requestBody, Approval.class);

        // 调用approvalService的passApplication方法
        int result = approvalService.passApplication(approval);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 驳回申请
    private void failApplication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Approval approval = new Gson().fromJson(requestBody, Approval.class);

        // 调用approvalService的failApplication方法
        int result = approvalService.failApplication(approval);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 删除审批
    private void deleteApproval(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Approval approval = new Gson().fromJson(requestBody, Approval.class);

        // 调用approvalService的deleteApproval方法
        int result = approvalService.deleteApproval(approval);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 修改审批
    private void updateApproval(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Approval approval = new Gson().fromJson(requestBody, Approval.class);

        // 调用approvalService的updateApproval方法
        int result = approvalService.updateApproval(approval);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.valueOf(result));
    }

    // 审批列表
    private void approvalListAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求体中的数据
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String account = new Gson().fromJson(requestBody, String.class);

        // 调用approvalService的approvalListAll方法
        List<Approval> result = approvalService.approvalListAll(account);

        // 设置响应结果
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(result));
    }
}
