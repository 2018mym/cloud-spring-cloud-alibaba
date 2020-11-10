package com.gwm.clouduser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gw.bpm.flowable.client.ProcessInstancesApi;
import com.gw.bpm.flowable.model.ProcessInstanceCreateRequest;
import com.gw.bpm.flowable.model.ProcessInstanceResponse;
import com.gw.bpm.flowable.tenant.TenantContext;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.constant.WorkStatusConstant;
import com.gwm.cloudcommon.util.OrderUtil;
import com.gwm.cloudecs.client.CloudEcsClient;
import com.gwm.cloudecs.client.CloudStoreClient;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.common.model.DTO.VolumeDTO;
import com.gwm.clouduser.dao.CloudOrderMapper;
import com.gwm.clouduser.model.DTO.OrderListDTO;
import com.gwm.clouduser.model.entity.CloudOrder;
import com.gwm.clouduser.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class WorkServiceImpl implements WorkService {

    @Autowired
    CloudOrderMapper cloudOrderMapper;

    @Autowired
    ProcessInstancesApi processInstancesApi;
    @Autowired
    TaskService taskService;

    @Autowired
    CloudEcsClient cloudEcsClient;
    @Autowired
    CloudStoreClient cloudStoreClient;

    @Value("${user.processDefinitionKey}")
    private String processDefinitionKey;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult submit(CloudOrder cloudOrder) {
        ResponseResult result = null;
        JSONObject returnObject = new JSONObject();
        try {
            ProcessInstanceCreateRequest body = new ProcessInstanceCreateRequest();
            body.setProcessDefinitionKey(processDefinitionKey); //流程key
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
        /*
        String processId = OrderUtil.getOrderId();
        cloudOrder.setSubmitdate(new Date()); //提交时间
        cloudOrder.setStatus(WorkStatusConstant.SUBMIT); //1:已提交  2：已审批  3：已驳回
        cloudOrder.setWorkflowid(processId); //流程id
        cloudOrder.setUuid(OrderUtil.getOrderId()); //订单号
        cloudOrderMapper.insert(cloudOrder);
        returnObject.put("processId",processId);*/

        return  ResponseResult.succObj(returnObject);
    }
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult reSubmit(CloudOrder cloudOrder) {
        ResponseResult result = null;
        JSONObject returnObject = new JSONObject();
        try{
            CloudOrder order =  cloudOrderMapper.selectByPrimaryKey(cloudOrder.getId());

            String processId =   order.getWorkflowid();    //"96237dfc-de11-11ea-9470-32ddbf7ebcd4";
            String userCode =    order.getSubmitcode(); //"gw00179595";
            String pass = "1";  //nickName
            String comment = "审核通过";
            result = taskService.completeTask(processId, userCode, pass, comment);
            if(result.getCode()== 200){
                order.setSubmitdate(new Date()); //提交时间
                order.setStatus(WorkStatusConstant.SUBMIT); //1:已提交  2：已审批  3：已驳回
                order.setParams(cloudOrder.getParams()); //参数
                order.setDetail(cloudOrder.getDetail()); //详情
                order.setType(cloudOrder.getType());
                order.setAuditcode(cloudOrder.getAuditcode());
                order.setAuditname(cloudOrder.getAuditname()); //审核人
                cloudOrderMapper.updateByPrimaryKey(order);
                returnObject.put("processId",processId);
            }else{
                return  result;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return ResponseResult.error(ex.getMessage());
        }
        return  ResponseResult.succObj(returnObject);
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseResult audit(String ticket,
                                String groupId,
                                String userId,
                                CloudOrder cloudOrder){
        ResponseResult result = null;
        try {
            CloudOrder order =  cloudOrderMapper.selectByPrimaryKey(cloudOrder.getId());
            String processId =   order.getWorkflowid();    //"96237dfc-de11-11ea-9470-32ddbf7ebcd4";
            String userCode =    order.getAuditcode(); //"gw00179595";

            String pass = "1";  //nickName
            String comment = "审核通过";
            result = taskService.completeTask(processId, userCode, pass, comment);
            if(result.getCode()== 200){
                order.setAuditdate(new Date()); //审核时间
                order.setStatus(WorkStatusConstant.AUDIT); //1:已提交  2：已审批  3：已驳回
                order.setReason(cloudOrder.getReason()); //通过原有
                cloudOrderMapper.updateByPrimaryKey(order);

                //调用创建接口
                JSONObject parse = (JSONObject) JSONObject.parse(order.getParams());
                System.out.println("parse:"+parse);
                //InstancesDTO instancesDTO = new InstancesDTO ();
                if("1".equals(order.getType())) {//云主机
                    InstancesDTO instancesDTO = JSONObject.toJavaObject(parse, InstancesDTO.class);
                    result = cloudEcsClient.createEcsV2(ticket, groupId, userId, instancesDTO);
                }else if("2".equals(order.getType())){ //硬盘
                    VolumeDTO volumeDTO  = JSONObject.toJavaObject(parse, VolumeDTO.class);
                    result =cloudStoreClient.volumeCreate(ticket, groupId, userId, volumeDTO);
                }
                //System.out.println("result:"+result);
            }else{
                return  result;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return ResponseResult.error(ex.getMessage());
        }
        return  result;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult reject(CloudOrder cloudOrder){
        ResponseResult result = null;
        try {
            CloudOrder order =  cloudOrderMapper.selectByPrimaryKey(cloudOrder.getId());
            String processId =   order.getWorkflowid();    //"96237dfc-de11-11ea-9470-32ddbf7ebcd4";
            String userCode =    order.getAuditcode(); //"gw00179595";
            String pass = "0";  //nickName
            String comment = "审核驳回";
            result = taskService.completeTask(processId, userCode, pass, comment);
            if(result.getCode()== 200){
                order.setAuditdate(new Date()); //审核时间
                order.setStatus(WorkStatusConstant.REJECT); //1:已提交  2：已审批  3：已驳回
                order.setReason(cloudOrder.getReason()); //通过原有
                cloudOrderMapper.updateByPrimaryKey(order);
            }else{
                return  result;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return ResponseResult.error(ex.getMessage());
        }
        return  ResponseResult.succ();
    }


    public ResponseResult getPersonList(OrderListDTO orderListDTO){
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(orderListDTO.getPageNum(), orderListDTO.getPageSize());
        List<CloudOrder> orderList = cloudOrderMapper.getOrderList(orderListDTO);
        PageInfo pageResult = new PageInfo(orderList);
        jsonObject.put("totalNum", pageResult.getTotal());
        jsonObject.put("list", pageResult.getList());
        return ResponseResult.succObj(jsonObject);
    }

    public ResponseResult detail(String id){
        //JSONObject jsonObject = new JSONObject();
        CloudOrder order =  cloudOrderMapper.selectByPrimaryKey(new Long(id));
        //jsonObject.put("detail", order);
        return ResponseResult.succObj(order);
    }






}