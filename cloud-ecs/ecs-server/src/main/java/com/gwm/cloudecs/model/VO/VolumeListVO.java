package com.gwm.cloudecs.model.VO;

import com.gwm.cloudecs.common.model.entity.Volume;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VolumeListVO extends Volume {
    // 挂载的云主机
    String mountInstance;
    // 创建的磁盘个数
    Integer snapshotCount;
    // 云主机uuid
    String instanceUuid;

}
