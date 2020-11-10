package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@ToString
public class CloudAccount implements Serializable {
    private Integer id;

    private String uuid;

    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private String domainId;

    private String password;

    private String projectId;

    private Date updateAt;

    private String url;

    private String userName;

    private String type;

    private String region;

    private String zone;

    private String datacenter;

    private String datastore;

    private String cluster;

    private String datastoreCluster;

    private String vmFolder;

    private String network;

    private static final long serialVersionUID = 1L;
}