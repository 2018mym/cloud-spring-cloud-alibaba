package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class VolumeDeleteDTO {
    // 卷所在的region
    String region;
    // 卷的uuid
    List<String> volumeIds;
    String userId;
    String groupId;
    String ticket;

}
