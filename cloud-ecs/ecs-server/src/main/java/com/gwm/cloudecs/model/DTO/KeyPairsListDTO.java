package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class KeyPairsListDTO extends BaseDTO {
    String name;
    String env;
    String region;
    String groupId;
    String userId;
}
