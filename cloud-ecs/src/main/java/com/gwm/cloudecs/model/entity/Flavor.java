package com.gwm.cloudecs.model.entity;

import java.util.Date;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }


    public Integer getMemoryMb() {
        return memoryMb;
    }

    public void setMemoryMb(Integer memoryMb) {
        this.memoryMb = memoryMb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getRootGb() {
        return rootGb;
    }

    public void setRootGb(Integer rootGb) {
        this.rootGb = rootGb;
    }

    public Integer getSwap() {
        return swap;
    }

    public void setSwap(Integer swap) {
        this.swap = swap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public Integer getVcpus() {
        return vcpus;
    }

    public void setVcpus(Integer vcpus) {
        this.vcpus = vcpus;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }
}