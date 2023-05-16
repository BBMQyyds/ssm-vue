package com.jdsbbmq.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Approval {
    private String id;
    private String teacher_id;
    private String application_id;
    private int pass;
    private String reason;
    private Date create_time;
    //附加
    private String course_id;
    private String course_title;
    private String stu_name;
    private String teacher_account;
    private int progress;
    private String teacher_name;
    private int sequence;
    private String proof_path;
}
