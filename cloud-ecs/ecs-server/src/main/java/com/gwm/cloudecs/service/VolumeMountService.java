package com.gwm.cloudecs.service;

import com.gwm.cloudecs.model.entity.VolumeMount;

public interface VolumeMountService {
    /**
     * 云主机挂载磁盘。插入关联表
     * @param instanceUuid
     * @param volumeUuid
     * @param attachVolumeUuid
     */
    void insertVolumeMout(String instanceUuid, String volumeUuid, String attachVolumeUuid);

    /**
     * 云主机卸载。删除关联信息
     * @param instanceId
     */
    void deleteByinstancesUuid(String instanceId);

    /**
     * 删除云主机和磁盘的对应关系
     * @param instancesUuid
     * @param volumeUuid
     */
    void deleteByVolumeAndInstances(String instancesUuid, String volumeUuid);

    /**
     * 检测磁盘是否被挂载
     * @param volumeId
     */
    boolean checkVolumeMount(String volumeId);

    /**
     * 通过volumeId获取对象
     * @param volumeId
     * @return
     */
    VolumeMount selectByVolumeId(String volumeId);
}
