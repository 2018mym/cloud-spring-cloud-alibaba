package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class SnapshotDeleteDTO {
    // 卷所在的region
    String region;
    // 卷的uuid
    List<String> snapshotIds;
    String userId;
    String groupId;

}
