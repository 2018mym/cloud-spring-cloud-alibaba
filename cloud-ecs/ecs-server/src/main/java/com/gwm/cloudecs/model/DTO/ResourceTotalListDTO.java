package com.gwm.cloudecs.model.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResourceTotalListDTO {
   private  String  groupId;
   private  String  userId;
   private  String  env;
   private  String  region;
}
