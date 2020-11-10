package com.gwm.cloudecs.common.model.DTO;


import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudecs.common.model.entity.Volume;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 创建卷的DTO
 */
@Setter
@Getter
@ToString
@ApiModel(value = "云主机对象磁盘模型")
public class InstancseVolumeDTO {
    // 卷的大小，单位GB
    @ApiModelProperty(value = "云主机磁盘的大小，范围在0-1024G", required = false)
    Integer size;
    // 快照的id，选填参数 不能和hint参数同时使用
    @ApiModelProperty(value = "云主机磁盘snapshotId", required = false)
    String snapshot;
    /**
     * cloud_efficiency：高效云盘
     * cloud_ssd：SSD云盘
     * cloud_essd：ESSD云盘
     */
    @ApiModelProperty(value = "阿里云云主机磁盘类型,", required = false)
    String category;
    @ApiModelProperty(value = "是否随云主机释放而释放", required = false)
    Boolean deleteWithInstance;
    @ApiModelProperty(value = "云磁盘的计费模型id", required = false)
    String priceModel;


}