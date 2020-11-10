package com.gwm.cloudecs.model.VO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VolumeTotalListVO {
    //存储个数
    int  size;

    // 磁盘个数
    int volumeCount;

}
