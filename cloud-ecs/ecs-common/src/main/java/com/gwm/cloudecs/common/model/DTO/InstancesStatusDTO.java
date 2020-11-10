package com.gwm.cloudecs.common.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 云主机状态查询
 */
@Setter
@Getter
@ToString
public class InstancesStatusDTO {
    String uuid;
    String region;
    Integer state;
    String type;
    String zone;

}
