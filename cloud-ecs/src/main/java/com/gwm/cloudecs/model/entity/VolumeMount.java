package com.gwm.cloudecs.model.entity;

import java.util.Date;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAttachMode() {
        return attachMode;
    }

    public void setAttachMode(Integer attachMode) {
        this.attachMode = attachMode;
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId == null ? null : instanceId.trim();
    }

    public String getMountpoint() {
        return mountpoint;
    }

    public void setMountpoint(String mountpoint) {
        this.mountpoint = mountpoint == null ? null : mountpoint.trim();
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

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId == null ? null : volumeId.trim();
    }
}