package com.gwm.clouduser.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PriceModelListDTO extends BaseDTO {
    String type;
    String cloudType;
    String region;
    String zone;
    String  flavorId;

}
