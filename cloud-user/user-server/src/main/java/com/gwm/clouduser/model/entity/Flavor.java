package com.gwm.clouduser.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class Flavor {
    private Integer id;

    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private String description;

    private Integer memoryMb;

    private String name;

    private Integer rootGb;

    private Integer swap;

    private String type;

    private Date updateAt;

    private String uuid;

    private Integer vcpus;

    private String region;

    public  Flavor(){

    }


}