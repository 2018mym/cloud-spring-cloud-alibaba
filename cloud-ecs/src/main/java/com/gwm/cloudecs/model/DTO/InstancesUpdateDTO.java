package com.gwm.cloudecs.model.DTO;

/**
 * 更新云主机实体
 */
public class InstancesUpdateDTO {
    // 虚拟机名称
    String name;
    String uuid;
    String description;
    String region;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
