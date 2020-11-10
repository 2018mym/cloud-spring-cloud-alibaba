package com.gwm.cloudecs.model.VO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class InstanceTotalListVO {

    //云主机台数汇总
    int  vcpus;
    //内存汇总
    int  memory;
    //cup 个数
    int  instanceConut;
}
