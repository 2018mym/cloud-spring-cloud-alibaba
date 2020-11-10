package com.gwm.cloudcommon.component;

import org.springframework.stereotype.Component;

/**
 * @ClassName: VarConstants
 * @Description: 共用常量
 * @Author: 99958168
 * @Date: 2020-06-11 15:48
 */
@Component
public class VarConstants {

    /**
     * 表主键id
     */
    public static final String TABLE_ID = "tableId";
    /**
     * 发起人用户姓名
     */
    public static final String START_USER_NAME = "startUserName";
    /**
     * 发起人code
     */
    public static final String START_USER_ID = "startUserId";
    /**
     * 任务审批结果:0-退回;1-通过
     */
    public static final String AUDIT_PASS = "pass";
    /**
     * 任务审批状态 0未完成 1已完成 控制已办列表数据展示
     */
    public static final String AUDIT_PASS_STATUS = "auditPassStatus";
    /**
     * 任务审批意见-全局变量
     */
    public static final String AUDIT_COMMENT = "auditComment";
    /**
     * 任务审批意见-局部变量
     */
    public static final String TASK_AUDIT_COMMENT = "taskAuditComment";
    /**
     * 是否是删除的流程：0 未删除状态,1 已删除状态 流程发起默认为0
     */
    public static final String DELETE_FLAG = "deleteFlag";
    /**
     * 常量 0
     */
    public static final String ZERO = "0";
    /**
     * 常量 1
     */
    public static final String ONE = "1";
}
