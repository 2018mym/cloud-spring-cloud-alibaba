package com.gwm.cloudecs.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class Image {
    private Integer id;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    private Integer deleted;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date deletedAt;

    private String description;

    private String diskFormat;

    private String env;

    private String name;

    private String projectId;

    private String region;

    private String osType;

    private Integer size;

    private String state;

    private String type;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    private String userId;

    private String uuid;

    private String visibility;

    private String zone;

}