package com.jdsbbmq.service;

import com.jdsbbmq.pojo.Approval;

import java.util.List;

public interface ApprovalService {

    //查询自己的审批
    List<Approval> approvalList(String account);

    //通过申请
    Integer passApplication(Approval approval);

    //驳回申请
    Integer failApplication(Approval approval);

    //撤销审批
    Integer deleteApproval(Approval approval);

    //修改驳回理由
    Integer updateApproval(Approval approval);

    //查询全部审批
    List<Approval> approvalListAll(String account);
}
