package com.jdsbbmq.service;

import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {

    //添加申请
    Integer applyCourse(Application application);

    //查询要审批的申请
    List<Application> applyList(String account);

    //删除申请
    Integer deleteApplication(String application_id);

    //修改申请
    Integer updateApplication(Application application);

    //获取全部申请信息
    List<Course> applyListAll(String account);

    //获得证明文件
    MultipartFile getProof(String id);
}
