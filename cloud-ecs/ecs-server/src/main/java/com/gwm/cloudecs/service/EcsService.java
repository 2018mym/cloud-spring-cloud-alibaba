package com.gwm.cloudecs.service;

import com.aliyuncs.exceptions.ClientException;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesDeleteDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesListDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesStatusDTO;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.model.entity.Flavor;

import java.io.IOException;
import java.util.List;

public interface EcsService {

    /**创建云主机
     * @param instancesDTO
     * @return
     */
    ResponseResult createInstances(InstancesDTO instancesDTO) throws IOException;

    /**
     * 更新云主机状态
     * @param server
     * @param status
     */
    void updateInstanceStatus(String server, Integer status);

    void updateInstance(String server, Integer status, String ip);

    /**
     * 启动云主机
     * @param instanceId
     * @param regionName
     * @return
     */
    ResponseResult instanceStart(String instanceId, String regionName);

    /**
     * 关闭云主机
     * @param instanceId
     * @param regionName
     * @return
     */
    ResponseResult instanceStop(String instanceId, String regionName);

    /**
     * 删除云主机
     * @param instanceId
     * @return
     */
    ResponseResult instanceDelete(InstancesDeleteDTO instancesDeleteDTO) throws IOException;

    /**
     * 查询云主机资源
     * @param instancesListDTO
     * @return
     */
    ResponseResult instanceList(InstancesListDTO instancesListDTO);

    /**
     * 重启虚拟机
     * @param instanceId
     * @param restartFlag
     * @param regionName
     * @return
     */
    ResponseResult instanceRestart(String instanceId, String restartFlag, String regionName);

    /**
     * 获取vnc接口地址
     * @param instanceId
     * @param regionName
     * @return
     */
    ResponseResult instanceVncConsole(String instanceId, String regionName);

    /**
     * 更新云主机
     * @param instancesUpdateDTO
     * @param userId
     * @param groupId
     * @return
     */
    ResponseResult udpateInfo(InstancesUpdateDTO instancesUpdateDTO, String userId, String groupId);

    /**
     * 虚拟机挂载卷
     * @param attachVolumeDTO
     * @return
     */
    ResponseResult instanceAttachVolume(AttachVolumeDTO attachVolumeDTO) throws IOException, ClientException;

    /**
     * 虚拟机卸载卷
     * @param attachVolumeDTO
     * @return
     */
    ResponseResult instanceDettachVolume(List<AttachVolumeDTO> attachVolumeDTOs);

    /**
     * 虚拟机的uuid
     * @param instancesStatusList
     * @return
     */
    ResponseResult instanceStatus(List<InstancesStatusDTO> instancesStatusList);

    /**
     * 重建虚拟机v2
     * @param instancesDTO
     * @param userId
     * @param groupId
     * @return
     */
    ResponseResult instanceRebuild(InstancesRebuildDTO instancesDTO, String userId, String groupId);

    /**
     * 云主机下卷的列表
     * @param instancesVolumeDTO
     * @return
     */
    ResponseResult instanceVolumeList(InstancesVolumeDTO instancesVolumeDTO);

    /**
     * 获取虚拟机详情
     * @param instanceId
     * @param regionName
     * @return
     */
    ResponseResult instanceDetail(String instanceId, String regionName);

    /**
     * 获取云主机类型
     * @param flavor
     * @return
     */
    ResponseResult getFlavorList(Flavor flavor);

    /**
     * 通过条件查询是否有记录。有记录为真。没记录为假
     * @param ipaddr
     * @param instanceId
     * @param groupId
     * @return
     */
    boolean checkInstanceIpaddr(String ipaddr, String instanceId, String groupId);

    /**
     * @return  获取阿里云的region
     */
    ResponseResult getAliRegion();


    ResponseResult getAliZone(String region);

    /**
     * @param region
     * @return
     */
    ResponseResult getAliInstanceTypes(String region, String zone);

    Instances selectByinstanceId(String server);

    int updateByPrimaryKey(Instances instances);

    ResponseResult instanceList2(InstancesListDTO instancesListDTO);

    ResponseResult getFlavorById(String flavorId);
}
