package com.jdsbbmq.controller;

import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.service.ApplicationService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    //添加申请
    @RequestMapping("/applyCourseS")
    @ResponseBody
    public Integer applyCourse(@RequestParam(value = "account") String account,
                               @RequestParam("course_id") String courseId,
                               @RequestParam("reason") String reason,
                               @RequestParam(required = false) MultipartFile file) {
        // 创建 Application 对象
        Application application = new Application();
        application.setAccount(account);
        application.setCourse_id(courseId);
        application.setReason(reason);
        application.setFile(file);
        return applicationService.applyCourse(application);
    }

    //查询要审批的申请
    @RequestMapping("/applyListT")
    @ResponseBody
    public List<Application> applyList(@RequestBody String account) {
        return applicationService.applyList(account);
    }

    //删除申请
    @RequestMapping("/deleteApplicationS")
    @ResponseBody
    public Integer deleteApplication(@RequestBody String application_id) {
        return applicationService.deleteApplication(application_id);
    }

    //修改申请
    @RequestMapping("/updateApplicationS")
    @ResponseBody
    public Integer updateApplication(@RequestBody Application application) {
        return applicationService.updateApplication(application);
    }

    //获取全部申请信息
    @RequestMapping("/applyListAllA")
    @ResponseBody
    public List<Course> applyListAll(@RequestBody String account) {
        return applicationService.applyListAll(account);
    }

    @RequestMapping("/getProof")
    public ResponseEntity<byte[]> getProof(@RequestBody String id) throws IOException {
        // 调用应用服务来获取证明文件
        MultipartFile proof = applicationService.getProof(id);

        // 构建HTTP响应对象并设置响应头和响应体
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        if (proof == null) {
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        } else {
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(Objects.requireNonNull(proof.getOriginalFilename())).build());
            // 读取证明文件的字节数组
            byte[] proofBytes = proof.getBytes();
            return new ResponseEntity<>(proofBytes, headers, HttpStatus.OK);
        }
    }
}
