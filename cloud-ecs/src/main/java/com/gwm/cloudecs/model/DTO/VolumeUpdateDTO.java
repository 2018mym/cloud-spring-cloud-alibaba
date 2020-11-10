package com.gwm.cloudecs.model.DTO;

public class VolumeUpdateDTO {
    // 卷的名称
    String name;
    // 卷所在的region
    String region;
    // 卷的uuid
    String volumeId;
    // 卷的描述信息
    String description;
    String userId;
    String groupId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
