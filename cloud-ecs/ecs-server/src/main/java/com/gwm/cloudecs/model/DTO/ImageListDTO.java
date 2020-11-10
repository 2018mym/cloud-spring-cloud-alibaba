package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel(value = "获取镜像列表对象")
public class ImageListDTO extends BaseDTO {
    @ApiModelProperty(value = "查询条件，镜像名称", required = false, allowableValues = "default")
    String name;
    String env;
    @ApiModelProperty(value = "查询条件，镜像region", required = true, allowableValues = "default")
    String region;
    String visibility;
    @ApiModelProperty(value = "查询条件，镜像类型", required = true, allowableValues = "default")
    String osType;
    @ApiModelProperty(value = "查询条件，云主机规格", required = true, allowableValues = "default")
    String instanceType;
}
