package com.gwm.cloudecs.model.VO;

import com.gwm.cloudecs.model.entity.Snapshot;

public class SnapshotListVO extends Snapshot {
    String volumeName;

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }
}
