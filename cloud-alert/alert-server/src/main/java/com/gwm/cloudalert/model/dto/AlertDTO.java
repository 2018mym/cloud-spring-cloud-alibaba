package com.gwm.cloudalert.model.dto;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AlertDTO extends BaseDTO {
     Long start;
     Long end;
     String type;
     String step;
     String host;
}
