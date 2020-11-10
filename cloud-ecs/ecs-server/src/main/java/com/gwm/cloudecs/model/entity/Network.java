package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class Network {
    private Integer id;

    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private String description;

    private String env;

    private Integer mtu;

    private String name;

    private String projectId;

    private String region;

    private String status;

    private String type;

    private Date updateAt;

    private String userId;

    private String uuid;

    private String zone;

}