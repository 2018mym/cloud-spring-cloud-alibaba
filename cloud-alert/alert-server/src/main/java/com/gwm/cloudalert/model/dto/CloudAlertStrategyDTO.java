package com.gwm.cloudalert.model.dto;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Setter
@Getter
@ToString
public class CloudAlertStrategyDTO extends BaseDTO{
    private Integer id;
    private String strategyName;
    private String alarmStrategyId;
    private String noticeStartTime;
    private String noticeEndTime;
    private String alertType;
    private String resourcesType;
    private String monitorType;
    private Integer checkWindow;
    private Integer count;
    private Integer alarmWindow;
    private Integer monitorLevel;
    private Integer threshold;
    private String method;
    private String alertTeamsId;
    private Integer groupId;
    private String userId;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Integer deleted;
    private String host;
}