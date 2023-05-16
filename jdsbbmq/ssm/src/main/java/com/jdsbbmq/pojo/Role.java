package com.jdsbbmq.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private String id;
    private String teacher_id;
    private String course_id;
    private int sequence;
    private Date create_time;
    //附加
    private String teacher_name;
    private String course_title;
    private String course_type;
    private String speaker_name;
    private String speaker_id;
    private String supervisor_name;
    private String supervisor_id;
    //老师名字列表
    private List<String> teacher_names;

    public void buildRole(String teacher_id){
        this.teacher_id = teacher_id;
        this.id = UUID.randomUUID().toString();
        this.create_time = new Date();
    }
}
