package com.gwm.clouduser.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AuditDTO {

    private  String  id;

    private  String  reason ;
    private  String  paramType;

  }
