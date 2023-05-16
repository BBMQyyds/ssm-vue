package com.jdsbbmq.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String id;
    private String type;
    private String title;
    private String description;
    private String speaker_id;
    private String speaker_name;
    private String supervisor_id;
    private String supervisor_name;
    //进度
    private int progress;
    private int finish;
    //申请时间
    private Date apply_time;
    //申请理由
    private String apply_reason;
    //驳回理由
    private String reject_reason;
    //创建时间
    private Date create_time;
    //申请人数
    private int apply_count;
    //申请id
    private String application_id;
    //申请人
    private String stu_name;
    private String stu_id;

    //附加
    private List<Role> approvers;//审批人列表
    private int approver_count;//审批人数
    private String proof_path;//证明文件路径
}
