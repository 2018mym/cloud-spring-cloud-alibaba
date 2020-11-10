package com.gwm.cloudalert.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class AlertTeamItem {
    private Integer id;
    private String name;
    private String member;
    private Integer groupId;
    private String userId;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Integer deleted;
}
