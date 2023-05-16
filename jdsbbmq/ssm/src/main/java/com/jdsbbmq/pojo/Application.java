package com.jdsbbmq.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private String id;
    private String reason;
    private Date apply_time;
    private String course_id;
    private String stu_id;
    private int progress;
    private int finish;
    //附加
    private String account;
    private String course_name;
    private String stu_name;
    private MultipartFile file;
    private String proof_path;
}
