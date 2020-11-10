package com.gwm.cloudecs.common.model.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gwm.cloudecs.common.model.entity.Instances;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;

/**
 * 创建云主机时所有的DTO
 */
@Setter
@Getter
@ToString
@ApiModel(value = "云主机对象模型")
@Slf4j
public class InstancesDTO extends Instances implements Cloneable {
    // 使用已有字段
//    // 虚拟机名称
//    String name;
//    // 	镜像的uuid
//    String image;
//    // flavor的uuid
//    String flavor;
//    // 区域名称
//    String regionName;
//    // zone名称
//    String availabilityZone;
    // 资源编码
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    String resourceCode;
    // 安全组列表，安全组名称
    @ApiModelProperty(value = "云主机安全组信息，仅OpenStack支持的参数", required = false, allowableValues = "default")
    List<String> securityGroups;
    @ApiModelProperty(value = "云主机安全组信息，仅aliyun支持的参数")
    String securityGroup;
    // 虚拟机的密码
    @ApiModelProperty(value = "云主机密码，仅OpenStack支持的参数", required = false)
    String adminPass;
    // 秘钥对的名称
    @ApiModelProperty(value = "云主机密码，仅OpenStack支持的参数", required = false)
    String keyName;
    // 创建个数
    @ApiModelProperty(value = "云主机个数", required = true)
    Integer count;
    // 容灾组的uuid
    @ApiModelProperty(value = "云主机容灾组，仅OpenStack支持的参数", required = false)
    String serverGroup;
    @ApiModelProperty(value = "云主机交换机id，公有云阿里云必填参数", required = false)
    String vSwitchId;
    @ApiModelProperty(value = "云主机公网出带宽最大值，不填默认为0 填写范围0-100", required = false)
    Integer internetMaxBandwidthOut;
    /**
     * PayByBandwidth：按固定带宽计费
     * PayByTraffic（默认）：按使用流量计费
     */
    @ApiModelProperty(value = "云主机支付方式，公有云阿里云必填参数 ", required = false)
    String chargeType;
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    String ticket;
    // 磁盘信息
    @ApiModelProperty(value = "云主机挂载的磁盘，仅OpenStack支持的参数，但是VMware中需要有这个参数，传空即可", required = true)
    List<InstancseVolumeDTO> volumes;

    // 配额信息
    HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
    @Override
    public InstancesDTO clone() {
        InstancesDTO instancesDTO = null;
        try {
            instancesDTO = (InstancesDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            log.error(e.getMessage(), e);
        }
        return instancesDTO;
    }
}