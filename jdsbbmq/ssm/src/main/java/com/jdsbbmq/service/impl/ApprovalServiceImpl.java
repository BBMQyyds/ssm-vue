package com.jdsbbmq.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.jdsbbmq.dao.ApplicationMapper;
import com.jdsbbmq.dao.ApprovalMapper;
import com.jdsbbmq.dao.UserMapper;
import com.jdsbbmq.pojo.Approval;
import com.jdsbbmq.service.ApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Resource
    private ApprovalMapper approvalMapper;

    @Resource
    private ApplicationMapper applicationMapper;

    @Resource
    private UserMapper userMapper;

    //查询自己的审批
    @Override
    public List<Approval> approvalList(String account) {
        if (account.startsWith("t")) {
            String id = userMapper.getUserID(account);
            List<Approval> approvals = approvalMapper.approvalList(id);
            for (Approval approval : approvals) {
                if (approval.getProof_path() != null) {
                    approval.setProof_path(MD5.create().digestHex16(approval.getProof_path()));
                }
            }
            return approvals;
        } else {
            return null;
        }
    }

    //通过申请
    @Override
    @Transactional
    public Integer passApplication(Approval approval) {
        approval.setId(UUID.randomUUID().toString());
        approval.setTeacher_id(userMapper.getUserID(approval.getTeacher_account()));
        approval.setCreate_time(new Date());
        approval.setPass(1);
        approval.setSequence(approval.getProgress());
        int success = approvalMapper.passApplication(approval);
        if (success == 1) {
            //申请通过，申请进度+1，如果申请进度等于所需审批数，申请完成
            return applicationMapper.updateApplicationPass(approval.getApplication_id(), approval.getCourse_id());
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    //驳回申请
    @Override
    @Transactional
    public Integer failApplication(Approval approval) {
        approval.setId(UUID.randomUUID().toString());
        approval.setTeacher_id(userMapper.getUserID(approval.getTeacher_account()));
        approval.setCreate_time(new Date());
        approval.setPass(0);
        approval.setSequence(approval.getProgress());
        int success = approvalMapper.failApplication(approval);
        if (success == 1) {
            //申请驳回，申请进度+1，finish=-1，申请失败
            return applicationMapper.updateApplicationFail(approval.getApplication_id());
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    //撤销审批
    @Override
    @Transactional
    public Integer deleteApproval(Approval approval) {
        //还原申请进度
        int success = applicationMapper.updateApplicationProgress(approval.getApplication_id());
        if (success == 1) {
            //删除审批
            return approvalMapper.deleteApproval(approval.getId());
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return 0;
        }
    }

    //修改驳回理由
    @Override
    public Integer updateApproval(Approval approval) {
        return approvalMapper.updateApproval(approval);
    }

    //查询全部审批
    @Override
    public List<Approval> approvalListAll(String account) {
        if (account.startsWith("a")) {
            return approvalMapper.approvalListAll();
        } else {
            return null;
        }
    }
}
