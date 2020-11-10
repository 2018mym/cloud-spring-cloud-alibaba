package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class Port {
    private Integer id;

    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private Integer deviceId;

    private Integer deviceType;

    private Integer macAddress;

    private Integer name;

    private Integer networkId;

    private Integer projectId;

    private Integer status;

    private Date updateAt;

    private String uuid;

}