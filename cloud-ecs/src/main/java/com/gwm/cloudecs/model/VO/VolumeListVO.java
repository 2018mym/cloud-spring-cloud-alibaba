package com.gwm.cloudecs.model.VO;

import com.gwm.cloudecs.model.entity.Volume;

public class VolumeListVO extends Volume {
    // 挂载的云主机
    String mountInstance;
    // 创建的磁盘个数
    Integer snapshotCount;
    // 云主机uuid
    String instanceUuid;

    public String getInstanceUuid() {
        return instanceUuid;
    }

    public void setInstanceUuid(String instanceUuid) {
        this.instanceUuid = instanceUuid;
    }

    public Integer getSnapshotCount() {
        return snapshotCount;
    }

    public void setSnapshotCount(Integer snapshotCount) {
        this.snapshotCount = snapshotCount;
    }

    public String getMountInstance() {
        return mountInstance;
    }

    public void setMountInstance(String mountInstance) {
        this.mountInstance = mountInstance;
    }
}
