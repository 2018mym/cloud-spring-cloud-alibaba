package com.gwm.cloudecs.model.DTO;

/**
 * 云主机状态查询
 */
public class InstancesStatusDTO {
    String uuid;
    String region;
    Integer state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
