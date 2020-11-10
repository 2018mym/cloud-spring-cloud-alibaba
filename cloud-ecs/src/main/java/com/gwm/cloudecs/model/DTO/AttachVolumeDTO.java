package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudecs.model.entity.VolumeMount;

/**
 * 挂载云主机的DTO
 */
public class AttachVolumeDTO extends VolumeMount {

    String regionName;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
