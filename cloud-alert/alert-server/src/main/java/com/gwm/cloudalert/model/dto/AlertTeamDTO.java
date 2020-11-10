package com.gwm.cloudalert.model.dto;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@ToString
public class AlertTeamDTO extends BaseDTO {
private Integer id;
private String name;
private String member;
private Integer groupId;
private String userId;
private String note;
}
