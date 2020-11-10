package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 更新云主机实体
 */
@Setter
@Getter
@ToString
public class InstancesUpdateDTO {
    // 虚拟机名称
    String name;
    String uuid;
    String description;
    String region;
}
