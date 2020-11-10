package com.gwm.cloudecs.common.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class Instances {
    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Integer id;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Date createAt;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Integer deleted;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Date deletedAt;

    @ApiModelProperty(value = "云主机描述信息", required = false)
    private String description;

    @ApiModelProperty(value = "云主机所属的环境", required = true)
    private String env;

    @ApiModelProperty(value = "云主机配置信息id，阿里云对应为InstanceType参数", required = true)
    private String flavorId;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String hostname;

    @ApiModelProperty(value = "云主机镜像id", required = true)
    private String imageUuid;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String ipAddr;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Integer memoryMb;

    @ApiModelProperty(value = "云主机名称",required = true)
    private String name;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String osType;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String projectId;

    @ApiModelProperty(value = "云主机region区域",required = true)
    private String region;

    @ApiModelProperty(value = "云主机创建类型--阿里云参数镜像大小。这里默认为系统盘大小",required = true)
    private Integer rootGb;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Integer state;

    // 网络ID
    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String network;

    @ApiModelProperty(value = "云主机创建类型--暂时支持vmware,openstack参数",required = true)
    private String type;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Date updateAt;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String userId;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String uuid;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private Integer vcpus;

    @ApiModelProperty(value = "云主机所在zone",required = true)
    private String zone;

    @ApiModelProperty(hidden=true)
    @JsonIgnore
    private String orderId;

    @ApiModelProperty(hidden=true)
    private String priceModel;
}