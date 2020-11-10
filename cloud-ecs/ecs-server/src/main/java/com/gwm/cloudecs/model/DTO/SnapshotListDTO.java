package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 卷列表的DTO
 */
@Setter
@Getter
@ToString
public class SnapshotListDTO extends BaseDTO {
    String name;
    String groupId;
    String userId;
    String env;
    String region;
    String zone;
    String status;
    String type;

}
