package com.gwm.cloudecs.model.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class Instances {
    private Integer id;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    private Integer deleted;

    private Date deletedAt;

    private String description;

    private String env;

    private String flavorId;

    private String hostname;

    private String imageUuid;

    private String ipAddr;

    private Integer memoryMb;

    private String name;

    private String osType;

    private String projectId;

    private String region;

    private Integer rootGb;

    private Integer state;

    // 网络ID
    private String network;

    private String type;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    private String userId;

    private String uuid;

    private Integer vcpus;

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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env == null ? null : env.trim();
    }

    public String getFlavorId() {
        return flavorId;
    }

    public void setFlavorId(String flavorId) {
        this.flavorId = flavorId == null ? null : flavorId.trim();
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname == null ? null : hostname.trim();
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid == null ? null : imageUuid.trim();
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr == null ? null : ipAddr.trim();
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

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType == null ? null : osType.trim();
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

    public Integer getRootGb() {
        return rootGb;
    }

    public void setRootGb(Integer rootGb) {
        this.rootGb = rootGb;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network == null ? null : network.trim();
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

    public Integer getVcpus() {
        return vcpus;
    }

    public void setVcpus(Integer vcpus) {
        this.vcpus = vcpus;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? null : zone.trim();
    }
}