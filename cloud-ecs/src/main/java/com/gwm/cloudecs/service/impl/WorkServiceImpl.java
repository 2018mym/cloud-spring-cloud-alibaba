package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gw.bpm.flowable.client.ProcessInstancesApi;
import com.gw.bpm.flowable.model.ProcessInstanceCreateRequest;
import com.gw.bpm.flowable.model.ProcessInstanceResponse;
import com.gw.bpm.flowable.tenant.TenantContext;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.constant.WorkStatusConstant;
import com.gwm.cloudcommon.util.OrderUtil;
import com.gwm.cloudecs.dao.CloudOrderMapper;
import com.gwm.cloudecs.model.entity.CloudOrder;
import com.gwm.cloudecs.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class WorkServiceImpl implements WorkService {

    @Autowired
    CloudOrderMapper cloudOrderMapper;

    @Autowired
    ProcessInstancesApi processInstancesApi;
    @Autowired
    TaskService taskService;


    @Override
    public ResponseResult submit(CloudOrder cloudOrder) {
        ResponseResult result = null;
        JSONObject returnObject = new JSONObject();
        try {
            ProcessInstanceCreateRequest body = new ProcessInstanceCreateRequest();
            body.setProcessDefinitionKey("aa-bb"); //流程key
            body.setTenantId(TenantContext.getTenantId());
            body.setStartUserId(cloudOrder.getSubmitname());

            ProcessInstanceResponse response = processInstancesApi.createProcessInstance(body);
            if (Objects.nonNull(response)) {
                //aa = response.toString();
                String processId = response.getId(); //流程id
                String userCode = cloudOrder.getSubmitname(); //审批人
                String pass = "1";
                String comment = "审核通过";
                result = taskService.completeTask(processId, userCode, pass, comment);
                if(result.getCode()== 200){
                    cloudOrder.setSubmitdate(new Date()); //提交时间
                    cloudOrder.setStatus(WorkStatusConstant.SUBMIT); //1:已提交  2：已审批  3：已驳回
                    cloudOrder.setWorkflowid(processId); //流程id
                    cloudOrder.setUuid(OrderUtil.getOrderId()); //订单号
                    cloudOrderMapper.insert(cloudOrder);
                    returnObject.put("processId",processId);
                }else{
                    return  result;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseResult.error(ex.getMessage());
        }
        return  ResponseResult.succObj(returnObject);
    }

    public ResponseResult audit(CloudOrder cloudOrder){


    }



}