package com.gwm.cloudecs.schedule;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.ecs.model.v20140526.DescribeDisksRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeDisksResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.exceptions.ServerException;
import com.google.gson.Gson;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.SpringUtil;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.common.model.DTO.VolumeDTO;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.service.EcsService;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import com.gwm.cloudecs.service.impl.EcsServiceImpl;
import com.gwm.streamsetscommon.client.DOPClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Optionals;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ecs 创建时所需要的线程类
 */
@Slf4j
public class ECSAliyunTask implements TaskRunnable {

    // 标识线程名称。业务名称
    String thName;
    // 云主机创建后所生成的uuid
    String server;
    // region_name
    String regionName;
    // 该云主机所带的磁盘
    List<VolumeDTO> instancseVolumeDTOList;
    // 云主机更新是否成功
    boolean instanceFlag = false;
    // 云主机所带磁盘更新是否成功
    boolean instanceVolumeFlag = false;

    // 强制关闭线程时间，此时间为间隔次数  时长为count*间隔时间
    int count = 60;


    public ECSAliyunTask(String name, String server, String regionName, List<VolumeDTO> instancseVolumeDTOList) {
        this.thName = name;
        this.server = server;
        this.regionName = regionName;
        this.instancseVolumeDTOList = instancseVolumeDTOList;
    }

    VolumeService volumeService;
    EcsService ecsService;
    VolumeMountService volumeMountService;

    {
        volumeService = (VolumeService) SpringUtil.getBean("volumeServiceImpl");
        ecsService = (EcsServiceImpl) SpringUtil.getBean("ecsServiceImpl");
        volumeMountService = (VolumeMountService) SpringUtil.getBean("volumeMountServiceImpl");
    }


    @Override
    public String getName() {
        return thName;
    }

    @Override
    public void run() {
        try {
            log.info(String.format("查询阿里云ip线程启动，uuid:%s", server));
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request.setRegionId(regionName);
            request.setInstanceIds("[\"" + server + "\"]");
            DescribeInstancesResponse response = GlobalVarConfig.client.getAcsResponse(request);
            JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
            JSONArray instancesJsonArray = jsonObject.getJSONArray("instances");
            JSONObject instanceJson = instancesJsonArray.getJSONObject(0);

            if (StrUtil.isNotEmpty(instanceJson.getString("status")) && !instanceFlag) {
                Integer cpu = instanceJson.getInteger("cpu");
                Integer memory = instanceJson.getInteger("memory");
                String zoneId = instanceJson.getString("zoneId");
                String oSType = instanceJson.getString("oSType");
                JSONArray networkInterfaces = instanceJson.getJSONArray("networkInterfaces");
                String primaryIpAddress = networkInterfaces.getJSONObject(0).getString("primaryIpAddress");
                // 修改库中数据
                Instances instances = ecsService.selectByinstanceId(server);
                instances.setIpAddr(primaryIpAddress);
                instances.setZone(zoneId);
//                instances.setVcpus(cpu);
//                instances.setMemoryMb(memory);
                instances.setOsType("windows".equals(oSType) ? "0" : "linux".equals(oSType) ? "1" : oSType);
                ecsService.updateByPrimaryKey(instances);
                log.info(String.format("查询阿里云ip线程，更新阿里云云主机成功，uuid:%s", server));
                instanceFlag = true;

            }

            DescribeDisksRequest disksRequest = new DescribeDisksRequest();
            disksRequest.setRegionId(regionName);
            disksRequest.setInstanceId(server);
            DescribeDisksResponse disksResponse = GlobalVarConfig.client.getAcsResponse(disksRequest);
            JSONObject disksResponseJson = JSONObject.parseObject(new Gson().toJson(disksResponse));
            JSONArray jsonArray = disksResponseJson.getJSONArray("disks");
            JSONArray dataTypeDisks = jsonArray.stream().filter(json -> "data".equals(((JSONObject) json).getString("type")))
                    .collect(Collectors.toCollection(JSONArray::new));
            if (dataTypeDisks.size() != instancseVolumeDTOList.size()) {
                log.info(String.format("查询阿里云ip线程，更新阿里云磁盘，磁盘数不对应，uuid:%s", server));
                return;
            }
            if (!instanceVolumeFlag) {
                for (int j = 0; j < dataTypeDisks.size(); j++) {
                    JSONObject dataTypeDisk = dataTypeDisks.getJSONObject(j);
                    List<VolumeDTO> collect = instancseVolumeDTOList.stream()
                            .filter(volumeDTO -> volumeDTO.getSize().equals(dataTypeDisk.getInteger("size")))
                            .filter(volumeDTO -> volumeDTO.getVolumeType().equals(dataTypeDisk.getString("category")))
                            .filter(volumeDTO -> volumeDTO.getUuid() == null)
                            .collect(Collectors.toList());
                    if (collect.size() == 0) {
                        log.info(String.format("查询阿里云ip线程，更新阿里云磁盘，磁盘已被更新，uuid:%s", server));
                        log.info(String.format("查询阿里云ip线程，数据被更新。关闭该线程，uuid:%s", server));
                        ScheduleUtil.stop(thName);
                    }
                    VolumeDTO volumeDTO = collect.get(0);
                    volumeDTO.setZone(dataTypeDisk.getString("zoneId"));
                    volumeDTO.setUuid(dataTypeDisk.getString("diskId"));
                    log.info(String.format("查询阿里云ip线程，更新阿里云磁盘，更新zoneId:%s 更新diskId:%s, 更新id为:%s，uuid:%s", volumeDTO.getZone(), volumeDTO.getUuid(), volumeDTO.getId(), server));
                    volumeService.updateByPrimaryKey(volumeDTO);
                    volumeMountService.insertVolumeMout(server, volumeDTO.getUuid(), volumeDTO.getUuid());
                }
                instanceVolumeFlag = true;
            }

            log.info(String.format("查询阿里云ip线程，更新阿里云云主机成功，uuid:%s", server));
            ScheduleUtil.stop(thName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (count-- < 0) {
                log.info(String.format("创建云主机定时线程因该线程超时，则强制关闭.....线程名称为:%s", thName));
                ScheduleUtil.stop(thName);
            }
        }

    }


}
