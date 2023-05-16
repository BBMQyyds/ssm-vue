package com.jdsbbmq.controller;

import com.jdsbbmq.pojo.Approval;
import com.jdsbbmq.service.ApprovalService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.List;

@Controller
public class ApprovalController {

    @Resource
    private ApprovalService approvalService;

    //查询自己的审批
    @RequestMapping(value = "/approvalListT")
    @ResponseBody
    public List<Approval> approvalList(@RequestBody String account) {
        return approvalService.approvalList(account);
    }

    //通过申请
    @RequestMapping(value = "/passApplicationT")
    @ResponseBody
    public Integer passApplication(@RequestBody Approval approval) {
        return approvalService.passApplication(approval);
    }

    //驳回申请
    @RequestMapping(value = "/failApplicationT")
    @ResponseBody
    public Integer failApplication(@RequestBody Approval approval) {
        return approvalService.failApplication(approval);
    }

    //撤销审批
    @RequestMapping(value = "/deleteApprovalT")
    @ResponseBody
    public Integer deleteApproval(@RequestBody Approval approval) {
        return approvalService.deleteApproval(approval);
    }

    //修改驳回理由
    @RequestMapping(value = "/updateApprovalT")
    @ResponseBody
    public Integer updateApproval(@RequestBody Approval approval) {
        return approvalService.updateApproval(approval);
    }

    //查询全部审批
    @RequestMapping(value = "/approvalListAllA")
    @ResponseBody
    public List<Approval> approvalListAll(@RequestBody String account) {
        return approvalService.approvalListAll(account);
    }
}
