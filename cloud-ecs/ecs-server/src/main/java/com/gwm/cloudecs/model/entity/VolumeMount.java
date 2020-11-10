package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class VolumeMount {
    private Integer id;

    private Integer attachMode;

    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private String instanceId;

    private String mountpoint;

    private Date updateAt;

    private String uuid;

    private String volumeId;

}