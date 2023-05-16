package com.jdsbbmq.dao;

import com.jdsbbmq.pojo.Approval;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApprovalMapper {

    //查询自己的审批
    List<Approval> approvalList(@Param("teacher_id") String id);

    //通过申请
    Integer passApplication(@Param("approval") Approval approval);

    //驳回申请
    int failApplication(@Param("approval") Approval approval);

    //撤销审批
    Integer deleteApproval(@Param("id") String id);

    //修改驳回理由
    Integer updateApproval(@Param("approval") Approval approval);

    //查询全部审批
    List<Approval> approvalListAll();
}
