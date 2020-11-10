package com.gwm.cloudecs.model.VO;

import com.gwm.cloudecs.model.entity.Snapshot;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SnapshotListVO extends Snapshot {
    String volumeName;

}
