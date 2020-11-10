package com.gwm.cloudecs.model.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel(value = "云主机重建对象模型")
public class InstancesRebuildDTO {

    @ApiModelProperty(value = "云主机的uuid", required = true)
    String instancesId;

    @ApiModelProperty(value = "云主机重建时的镜像id，仅OpenStack支持的参数", required = false)
    String imageUuid;

    @ApiModelProperty(value = "虚拟机名称，仅OpenStack支持的参数", required = false)
    String name;

    @ApiModelProperty(value = "云主机密码，仅OpenStack支持的参数", required = false)
    String adminPass;

}
