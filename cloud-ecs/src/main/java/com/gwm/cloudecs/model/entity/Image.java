package com.gwm.cloudecs.model.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

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

    public String getDiskFormat() {
        return diskFormat;
    }

    public void setDiskFormat(String diskFormat) {
        this.diskFormat = diskFormat == null ? null : diskFormat.trim();
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env == null ? null : env.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType == null ? null : osType.trim();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility == null ? null : visibility.trim();
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? null : zone.trim();
    }
}