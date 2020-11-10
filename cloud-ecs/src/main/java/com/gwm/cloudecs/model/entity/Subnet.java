package com.gwm.cloudecs.model.entity;

import org.springframework.data.annotation.Transient;

import java.util.Date;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr == null ? null : cidr.trim();
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

    public String getEnableDhcp() {
        return enableDhcp;
    }

    public void setEnableDhcp(String enableDhcp) {
        this.enableDhcp = enableDhcp == null ? null : enableDhcp.trim();
    }

    public String getGwIp() {
        return gwIp;
    }

    public void setGwIp(String gwIp) {
        this.gwIp = gwIp == null ? null : gwIp.trim();
    }

    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion == null ? null : ipVersion.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNetwordId() {
        return networdId;
    }

    public void setNetwordId(String networdId) {
        this.networdId = networdId == null ? null : networdId.trim();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
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
}