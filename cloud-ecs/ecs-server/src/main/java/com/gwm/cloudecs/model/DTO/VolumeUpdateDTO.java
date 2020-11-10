package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VolumeUpdateDTO {
    // 卷的名称
    String name;
    // 卷所在的region
    String region;
    // 卷的uuid
    String volumeId;
    // 卷的描述信息
    String description;
    String userId;
    String groupId;
}
