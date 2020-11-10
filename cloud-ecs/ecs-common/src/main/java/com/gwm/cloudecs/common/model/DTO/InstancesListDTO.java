package com.gwm.cloudecs.common.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 获取云主机列表的list
 */
@Setter
@Getter
@ToString
public class InstancesListDTO extends BaseDTO {
    String name;
    String groupId;
    String userId;
    String env;
    String region;
    String zone;
    String type;
    String status;
    List<String> instanceTypes;
}
