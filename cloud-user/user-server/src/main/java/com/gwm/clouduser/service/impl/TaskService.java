package com.gwm.clouduser.service.impl;

import com.gw.bpm.flowable.ApiException;
import com.gw.bpm.flowable.client.TaskCommentsApi;
import com.gw.bpm.flowable.client.TaskVariablesApi;
import com.gw.bpm.flowable.client.TasksApi;
import com.gw.bpm.flowable.model.*;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.clouduser.component.ProcessVariables;
import com.gwm.clouduser.component.VarConstants;
import com.gwm.clouduser.enums.TaskActionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TaskService
 * @Description: 用户任务实例
 * @Author: 99958168
 * @Date: 2020-06-11 16:27
 */
@Service
public class TaskService {

    @Autowired
    TasksApi tasksApi;

    @Autowired
    TaskVariablesApi taskVariablesApi;

    @Autowired
    ProcessVariables processVariables;

    @Autowired
    TaskCommentsApi taskCommentsApi;

    /**
     * 任务办理
     *
     * @param processId
     * @param userCode
     * @param pass
     * @param comment
     * @return
     * @throws ApiException
     */
    public ResponseResult completeTask(String processId, String userCode, String pass, String comment) throws ApiException {
        //logger.info(MessageFormat.format("completeTask任务办理开始{0}", processId));
        //JsonResult jsonResult;
        //一：查询出办理任务
        TaskQueryRequest taskQueryRequest = new TaskQueryRequest();
        taskQueryRequest.processInstanceId(processId).active(true);
        DataResponseTaskResponse response = tasksApi.queryTasks(taskQueryRequest);
        List<TaskResponse> todoList = response.getData();
        if (CollectionUtils.isEmpty(todoList)) {
            //jsonResult = JsonResultUtil.createFailureJsonResult("未查询到待办理任务");
            //return jsonResult;
        	return  ResponseResult.error("未查询到待办理任务");
        }
        TaskResponse taskResponse = todoList.get(0);
        String taskId = taskResponse.getId();
        //二：判断任务是否挂起
        if (taskResponse.getSuspended()) {
            //jsonResult = JsonResultUtil.createFailureJsonResult("流程被挂起,请激活后在进行审批");
           // return jsonResult;
           return  ResponseResult.error("流程被挂起,请激活后在进行审批");
        }
        //三：判断任务认领人是否为空
        if (StringUtils.isBlank(taskResponse.getAssignee())) {
            this.claimTask(taskId, userCode);
        }
        //四: 添加审批意见
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setMessage(comment);
        taskCommentsApi.createTaskComments(taskId, commentRequest);
        //五：封装流程变量
        //全局变量
        List<RestVariable> variables = new ArrayList<>();
        variables.add(processVariables.getRestVariable(VarConstants.AUDIT_PASS, pass));
        variables.add(processVariables.getRestVariable(VarConstants.AUDIT_COMMENT, comment));
        //局部变量
        //logger.info(MessageFormat.format("completeTask加入局部审批状态开始{0}", taskId));
        List<RestVariable> listTaskVariables = taskVariablesApi.listTaskVariables(taskId, "local");
        if (!CollectionUtils.isEmpty(listTaskVariables)) {
            for (RestVariable variable : listTaskVariables) {
                taskVariablesApi.deleteTaskInstanceVariable(taskId, variable.getName(), "local");
            }
        }
        List<RestVariable> body = new ArrayList<>();
        body.add(processVariables.getRestVariableLocal(VarConstants.AUDIT_PASS, pass));
        body.add(processVariables.getRestVariableLocal(VarConstants.AUDIT_PASS_STATUS, VarConstants.ONE));
        body.add(processVariables.getRestVariableLocal(VarConstants.TASK_AUDIT_COMMENT, comment));
        Object object = taskVariablesApi.createTaskVariable(taskId, body, null, null, null);
        //logger.info(MessageFormat.format("completeTask加入局部审批状态结束{0}", object));
        //六 办理任务
        TaskActionRequest taskActionRequest = new TaskActionRequest();
        taskActionRequest.action(TaskActionEnum.COMPLETE.getName());
        taskActionRequest.variables(variables);
        tasksApi.executeTaskAction(taskId, taskActionRequest);
        //jsonResult = JsonResultUtil.createSuccessJsonResult(taskResponse.getProcessInstanceId());
        //return jsonResult;
        //System.out.println("================"+taskResponse.getProcessInstanceId());
    
        return  ResponseResult.succObj(taskResponse.getProcessInstanceId());   //流程id
     
    }

    /**
     * 认领运行中用户任务
     *
     * @param taskId
     * @param userCode
     * @throws ApiException
     */
    public void claimTask(String taskId, String userCode) throws ApiException {
        TaskActionRequest taskActionRequest = new TaskActionRequest();
        taskActionRequest.action(TaskActionEnum.CLAIM.getName());
        taskActionRequest.setAssignee(userCode);
        tasksApi.executeTaskAction(taskId, taskActionRequest);
    }
}
