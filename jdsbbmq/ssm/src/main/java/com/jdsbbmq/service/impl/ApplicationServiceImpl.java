package com.jdsbbmq.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.MD5;
import com.jdsbbmq.dao.ApplicationMapper;
import com.jdsbbmq.dao.UserMapper;
import com.jdsbbmq.pojo.Application;
import com.jdsbbmq.pojo.Course;
import com.jdsbbmq.service.ApplicationService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Resource
    private ApplicationMapper applicationMapper;

    @Resource
    private UserMapper userMapper;

    //添加申请
    @Override
    public Integer applyCourse(Application application) {
        if (application.getAccount().startsWith("s")) {
            application.setId(UUID.randomUUID().toString());
            application.setStu_id(userMapper.getUserID(application.getAccount()));
            application.setApply_time(new Date());
            //将申请中的图片存入issue文件
            if (application.getFile() != null) {
                String filename = saveFile(application.getFile(), application.getId());
                application.setProof_path(filename);
            }
            //判断reason是否为空值
            if (application.getReason().isEmpty()) {
                application.setReason(null);
            }
            return applicationMapper.insert(application);
        } else {
            return null;
        }
    }

    //将申请中的图片存入issue文件
    private String saveFile(MultipartFile file, String application_id) {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String path = "C:\\javaweb\\jdsbbmq\\issue\\";
        String newFilename = application_id + suffix;
        File destFile = new File(path + newFilename);
        try {
            file.transferTo(destFile);
            return path + newFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //查询要审批的申请
    @Override
    public List<Application> applyList(String account) {
        if (account.startsWith("t")) {
            List<Application> applications = applicationMapper.applyList(userMapper.getUserID(account));
            for (Application application : applications) {
                if (application.getProof_path() != null) {
                    application.setProof_path(MD5.create().digestHex16(application.getProof_path()));
                }
            }
            return applications;
        } else {
            return null;
        }
    }

    //删除申请
    @Override
    public Integer deleteApplication(String application_id) {
        return applicationMapper.deleteApplication(application_id);
    }

    //修改申请
    @Override
    public Integer updateApplication(Application application) {
        return applicationMapper.updateApplication(application);
    }

    //获取全部申请信息
    @Override
    public List<Course> applyListAll(String account) {
        if (account.startsWith("a")) {
            return applicationMapper.applyListAll();
        } else {
            return null;
        }
    }

    //获得证明文件
    @Override
    public MultipartFile getProof(String id) {
        //获得证明文件的路径和文件名
        String path = applicationMapper.getProofPath(id);
        if (path == null) {
            return null;
        }
        //返回MultipartFile类型的文件
        return convert(new File(path));
    }

    private MultipartFile convert(File file) {
        FileItem fileItem;
        try {
            fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            fileItem.getOutputStream().write(Files.readAllBytes(file.toPath()));
            return new CommonsMultipartFile(fileItem);
        } catch (IOException e) {
            return null;
        }
    }
}
