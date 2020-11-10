package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.util.Date;

@Setter
@Getter
@ToString
public class Subnet {
    private Integer id;

    private String cidr;

    private Date createAt;

    @Transient
    private Integer deleted;

    private Date deletedAt;

    private String enableDhcp;

    private String gwIp;

    private String ipVersion;

    private String name;

    private String networdId;

    private String projectId;

    private Date updateAt;

    private String uuid;
}