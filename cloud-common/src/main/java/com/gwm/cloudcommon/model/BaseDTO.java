package com.gwm.cloudcommon.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BaseDTO {
    int pageNum = 1;
    int pageSize =10;
}
