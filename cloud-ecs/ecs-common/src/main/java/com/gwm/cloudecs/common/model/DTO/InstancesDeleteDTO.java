package com.gwm.cloudecs.common.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class InstancesDeleteDTO {
    List<String> instanceIds;
    String resourceCode;
    String region;
    String ticket;
    Integer count;

}
