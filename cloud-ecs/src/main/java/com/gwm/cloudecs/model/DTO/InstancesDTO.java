package com.gwm.cloudecs.model.DTO;


import com.gwm.cloudecs.model.entity.Instances;

import java.util.List;

/**
 * 创建云主机时所有的DTO
 */
public class InstancesDTO extends Instances {
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
    String resourceCode;
    // 安全组列表，安全组名称
    List<String> securityGroups;
    // 虚拟机的密码
    String adminPass;
    // 秘钥对的名称
    String keyName;
    // 创建个数
    Integer count;
    // 容灾组的uuid
    String serverGroup;
    String ticket;
    // 磁盘信息
    List<VolumeDTO> volumes;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public String getAdminPass() {
        return adminPass;
    }

    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }


    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }

    public List<VolumeDTO> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<VolumeDTO> volumes) {
        this.volumes = volumes;
    }
}