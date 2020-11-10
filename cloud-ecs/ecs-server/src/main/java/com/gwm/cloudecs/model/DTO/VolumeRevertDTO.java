package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VolumeRevertDTO {
    // 卷所在的region
    String region;
    // 卷的uuid
    String volumeId;
    String snapshotId;
}
