package com.gwm.cloudecs.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.model.VO.UserVo;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.model.entity.CloudOrder;
import com.gwm.cloudecs.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
@RestController
@RequestMapping("/api/workflow")
public class WorkFlowController {

    @Value("${user.url}")
    private String url;

    @Autowired
    WorkService workService;

    /*
     *获取当前用户的审批组用户列表数据
     */
    @GetMapping("/getAuditList")
    public Object  getAuditList(HttpServletRequest request)  {
        try {
            //String url = "http://dev.paas.gwm.cn/api/v1/system/group/manages";
            String ticket = request.getHeader("ticket");
            String responseBody = HttpClient.get(url+"/api/v1/system/group/manages", new HashMap<String, String>() {{
                put("ticket", ticket);
            }});
            JSONObject jsonResult = (JSONObject) JSONObject.parse(responseBody);
            return  jsonResult;

        } catch (Exception e) {
            throw new CommonCustomException(-1, e.getMessage());
        }
    }

    /*
     *提交
     */
    @PostMapping("/submit")
    public Object  submit(CloudOrder cloudOrder)  {
        return  workService.submit(cloudOrder);
    }


    @PostMapping("/audit")
    public Object  audit(HttpServletRequest request,CloudOrder cloudOrder)  {
        System.out.println("================");
        String userId = request.getParameter("userId");
        System.out.println("============"+userId);
        String processId = "96237dfc-de11-11ea-9470-32ddbf7ebcd4";
        String userCode = "gw00179595";
        String pass ="1";  //nickName
        String comment = "审核通过";
        //ResponseResult result  = taskService.completeTask(processId, userCode, pass, comment);
        return  null;
    }
















}
