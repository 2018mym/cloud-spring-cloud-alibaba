package com.gwm.clouduser.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrderListDTO  extends BaseDTO {

    private  String  submitCode;
    private  String  auditCode;
    private  String  paramType;


}
