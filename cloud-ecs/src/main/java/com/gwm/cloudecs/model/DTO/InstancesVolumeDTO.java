package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;

/**
 * 云主机下的卷列表
 */
public class InstancesVolumeDTO extends BaseDTO {
    String instanceId;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
