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
public class User {
    private String id;
    private String account;
    private String name;
    private String pwd;
    private String new_pwd;
    private Date create_time;
    private String role;
    private String college;

    //辅助
    private String course_id;
    private String cookie_id;

    //补全空值
    public void buildUser() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.account == null) {
            this.account = "null";
        }
        if (this.pwd == null) {
            this.pwd = "123456";
        }
        if (this.role == null) {
            this.role = "null";
        }
        if (this.college == null) {
            this.college = "null";
        }
        if (this.create_time == null) {
            this.create_time = new Date();
        }
    }
}
