package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class InstanceGroupMember {
    private Integer id;

    private Date createAt;

    private String groupId;

    private String instanceId;

    private Date updateAt;

}