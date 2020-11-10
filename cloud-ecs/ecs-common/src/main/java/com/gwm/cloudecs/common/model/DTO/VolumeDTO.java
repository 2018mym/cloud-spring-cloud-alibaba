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
@ApiModel(value = "云磁盘对象模型")
public class VolumeDTO extends Volume {
    //    //卷的名称
//    String volumeName;
//    // 卷的大小，单位GB
//    Integer size;
//    // 卷的所在zone
//    String availabilityZone;
//    // 卷所在的region
//    String regionName;
    // 快照的id，选填参数 不能和hint参数同时使用
    @ApiModelProperty(value = "磁盘创建参数。快照参数，阿里云暂时不支持该参数", required = false)
    String snapshot;
    String ticket;
    //    // 卷调度相关的，参数，在创建虚拟机时，如果选择了卷，则需要传入该参数，不能和snapshot 参数同时使用
    JSONObject hint;
    // 标记使用hint参数还是snapshot参数
    /**
     * 1 表示 使用hint参数  云主机创建
     * 2 表示 使用snapshot创建
     * 3 表示 普通创建
     */
    @ApiModelProperty(value = "磁盘创建时，1 云主机创建，2 使用snapshot创建，3 为普通创建 阿里云暂时仅支持 3", required = true)
    String flag;
    // 阿里云创建磁盘时，选择云主机
    @ApiModelProperty(value = "阿里云磁盘创建时，如果选中云主机，此参数为必输项(但是云主机和磁盘都是包年包月。所以暂时不支持) ", required = false)
    String instanceId;

}