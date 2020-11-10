package com.gwm.clouduser.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.clouduser.model.DTO.AuditDTO;
import com.gwm.clouduser.model.DTO.OrderListDTO;
import com.gwm.clouduser.model.entity.CloudOrder;
import com.gwm.clouduser.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/workflow")
/*
 * 审批模块
 */
public class WorkFlowController {

    @Value("${user.url}")
    private String url;

    @Autowired
    WorkService workService;


    /*
     *获取当前用户的审批组用户列表数据
     */
    @GetMapping("/getAuditMemberList")
    public Object getAuditList(@RequestHeader("ticket") String ticket) {
        try {
            //String url = "http://dev.paas.gwm.cn/api/v1/system/group/manages";
            //String ticket = request.getHeader("ticket");
            String responseBody = HttpClient.get(url + "/api/v1/system/group/manages", new HashMap<String, String>() {{
                put("ticket", ticket);
            }});
            JSONObject jsonResult = (JSONObject) JSONObject.parse(responseBody);
            return jsonResult;

        } catch (Exception e) {
            throw new CommonCustomException(-1, e.getMessage());
        }
    }

    /*
     *提交
     */
    @PostMapping("/submit")
    public Object submit(@RequestHeader("userId") String userId, @RequestBody CloudOrder cloudOrder) {

        cloudOrder.setSubmitcode(userId); //userCode
        //cloudOrder.setSubmitname(userName); //真实名字
        cloudOrder.setSubmitname(userId);
        cloudOrder.setSubmitdate(new Date());
        return workService.submit(cloudOrder);
    }

    /*
     *再提交
     */
    @PostMapping("/reSubmit")
    public Object reSubmit(@RequestHeader("userId") String userId, @RequestBody CloudOrder cloudOrder) {

        return workService.reSubmit(cloudOrder);
    }





    //我提交的和我审批的列表
    @GetMapping("/getList")
    public Object getList(@RequestHeader("userId") String userId, OrderListDTO order) {

        if ("1".equals(order.getParamType())) { //我提交的列表
            order.setSubmitCode(userId);
        }
        if ("2".equals(order.getParamType())) { //我审批的列表
            order.setAuditCode(userId);

        }
        return workService.getPersonList(order);

    }


    @PostMapping("/audit")
    public Object audit(@RequestHeader("ticket") String ticket,
                        @RequestHeader("groupId") String groupId,
                        @RequestHeader("userId") String userId,
                        @RequestBody AuditDTO audit) {
        CloudOrder cloudOrder = new CloudOrder();
        cloudOrder.setId(new Long(audit.getId()));
        cloudOrder.setReason(audit.getReason());
        if ("1".equals(audit.getParamType())) { //表示提交操作:
            return workService.audit(ticket,groupId,userId,cloudOrder);
        }
        if ("0".equals(audit.getParamType())) { //表示驳回操作
            return workService.reject(cloudOrder);
        }
        return null;
    }

    @GetMapping("/detail")
    public Object detail(String id) {
       return  workService.detail(id);

    }
}