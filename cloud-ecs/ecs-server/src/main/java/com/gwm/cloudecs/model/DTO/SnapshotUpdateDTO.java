package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SnapshotUpdateDTO {
    // 卷的名称
    String name;
    // 卷所在的region
    String region;
    // 快照的uuid
    String snapshotId;
    // 卷的描述信息
    String description;
    String userId;
    String groupId;
}
