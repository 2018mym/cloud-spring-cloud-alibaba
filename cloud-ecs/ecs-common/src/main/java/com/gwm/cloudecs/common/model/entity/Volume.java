package com.gwm.cloudecs.common.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Setter
@Getter
@ToString
public class Volume {
    private Integer id;

    private String bootable;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private String description;

    private String env;

    private String name;

    private String projectId;

    private String region;

    private Integer size;

    private String status;

    private String type;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    private String userId;

    private String uuid;

    @ApiModelProperty(value = "云磁盘创建时，磁盘的类型，必输项 ", required = true)
    private String volumeType;

    @ApiModelProperty(value = "云磁盘创建时，磁盘所属的zone，如果是阿里云，该项与instanceId为互斥项，zone随云主机区域 ", required = true)
    private String zone;

    private String orderId;

    private String priceModel;
}