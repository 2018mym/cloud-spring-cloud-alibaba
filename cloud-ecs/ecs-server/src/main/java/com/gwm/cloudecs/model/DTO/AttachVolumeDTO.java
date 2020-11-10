package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudecs.model.entity.VolumeMount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 挂载云主机的DTO
 */
@Setter
@Getter
@ToString
public class AttachVolumeDTO extends VolumeMount {

    String regionName;
    String status;
    String type;
    String zone;
    // 释放实例时，该云盘是否随实例一起释放。 阿里云参数
    boolean deleteWithInstance;
}
