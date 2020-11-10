package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 云主机下的卷列表
 */
@Setter
@Getter
@ToString
public class InstancesVolumeDTO extends BaseDTO {
    String instanceId;

}
