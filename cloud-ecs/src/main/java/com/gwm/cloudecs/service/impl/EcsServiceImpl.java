package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.exception.QuotaException;
import com.gwm.cloudcommon.handler.QuotaHandle;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.dao.FlavorMapper;
import com.gwm.cloudecs.dao.InstancesMapper;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.model.entity.Instances;
import com.gwm.cloudecs.model.entity.Volume;
import com.gwm.cloudecs.schedule.ECSTask;
import com.gwm.cloudecs.schedule.ScheduleUtil;
import com.gwm.cloudecs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class EcsServiceImpl implements EcsService {

    @Value("${ecs.url}")
    private String url;

    @Autowired
    private InstancesMapper instancesMapper;

    @Autowired
    private FlavorService flavorService;

    @Autowired
    private VolumeService volumeService;

    @Autowired
    private VolumeMountService volumeMountService;

    @Autowired
    private QuotaHandle quotaHandle;

    @Autowired
    private FlavorMapper flavorMapper;

    @Autowired
    ImageService imageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult createInstances(InstancesDTO instancesDTO, String userId, String groupId) throws IOException {
        Flavor flavor = flavorService.getRecordByUUid(instancesDTO.getFlavorId());
        Double volumeSize = instancesDTO.getVolumes().stream().mapToDouble(VolumeDTO::getSize).sum();
        // 虚拟机个数
        Integer count = instancesDTO.getCount();
        // 检查配额
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        // 虚拟机个数
        hashMap.put("10003", count);
        // 内存个数
        hashMap.put("10001", flavor.getMemoryMb() * count);
        // vcpu 个数
        hashMap.put("10004", flavor.getVcpus() * count);
        // 磁盘空间
        hashMap.put("10009", volumeSize.intValue() * count);
        // 磁盘空间
        hashMap.put("10010", instancesDTO.getVolumes().size() * count);
        // 检查配额
        quotaHandle.putQuotas(instancesDTO.getTicket(), Constant.OCCUPY, hashMap);
        // 虚拟机的uuid
        List<String> serverList = new ArrayList();
        Map<String, List<String>> instanceVolumeMap = new HashMap<>();
        try {
            Image record = imageService.getRecordByUUid(instancesDTO.getImageUuid());
            // 重新组织参数  命名规范不同需要重新组织参数
            JSONObject ecsParam = new JSONObject();
            ecsParam.put("security_groups", instancesDTO.getSecurityGroups());
            ecsParam.put("image", instancesDTO.getImageUuid());
            ecsParam.put("flavor", instancesDTO.getFlavorId());
            ecsParam.put("adminPass", instancesDTO.getAdminPass());
            ecsParam.put("key_name", instancesDTO.getKeyName());
            ecsParam.put("region_name", instancesDTO.getRegion());
            ecsParam.put("availability_zone", instancesDTO.getZone());
            ecsParam.put("network_id", instancesDTO.getNetwork());
            ecsParam.put("server_group", instancesDTO.getServerGroup());
            ecsParam.put("availabilityZone", instancesDTO.getZone());
            instancesDTO.setState(2);
            instancesDTO.setRootGb(flavor.getRootGb());
            instancesDTO.setType(flavor.getType());
            instancesDTO.setMemoryMb(flavor.getMemoryMb());
            instancesDTO.setVcpus(flavor.getVcpus());
            instancesDTO.setUserId(userId);
            instancesDTO.setProjectId(groupId);
            instancesDTO.setOsType(record.getOsType());
            String name = instancesDTO.getName();
            // 创建虚拟机个数
            for (int i = 0; i < count; i++) {
                String formatName = String.format("%s-%s", name, i + 1);
                ecsParam.put("name", formatName);
                JSONObject escData = HttpClient.postEcs(String.format(url, "/bcp/v2/instance/create"), ecsParam.toJSONString());
                String server = escData.getString("server");
                instancesDTO.setName(formatName);
                instancesDTO.setUuid(server);
                instancesMapper.insert(instancesDTO);
                // 创建磁盘
                List<String> volumeServerList = new ArrayList<>();
                for (int j = 0; j < instancesDTO.getVolumes().size(); j++) {
                    VolumeDTO volumeDTO = instancesDTO.getVolumes().get(j);
                    volumeDTO.setName(String.format("volume-%s-%s", instancesDTO.getName(), j + 1));
                    volumeDTO.setEnv(instancesDTO.getEnv());
                    volumeDTO.setRegion(instancesDTO.getRegion());
                    volumeDTO.setZone(instancesDTO.getZone());
                    volumeDTO.setUserId(userId);
                    volumeDTO.setProjectId(groupId);
                    volumeDTO.setFlag("2");
                    volumeServerList.add(volumeService.insertRecord(volumeDTO));
                }
                instanceVolumeMap.put(server, volumeServerList);
                serverList.add(server);
            }
            for (Map.Entry<String, List<String>> stringListEntry : instanceVolumeMap.entrySet()) {
                LogUtil.info(String.format("创建云主机定时线程开启.....线程名称为:%s", String.format("ecs-%s", stringListEntry.getKey())));
                ScheduleUtil.stard(new ECSTask(String.format("ecs-%s", stringListEntry.getKey()), stringListEntry.getKey(), stringListEntry.getValue(), url, instancesDTO.getRegion()), 10, 5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            handleInstanceVolume(instanceVolumeMap, instancesDTO.getRegion());
            throw new QuotaException(-1, e.getMessage(), Constant.FREE, hashMap);
        }
        return ResponseResult.succObj(CommonEnum.CREATESUCCESS, new JSONObject() {{
            put("serverList", serverList);
        }});
    }

    private void handleInstanceVolume(Map<String, List<String>> instanceVolumeMap, String region) {
        for (Map.Entry<String, List<String>> stringListEntry : instanceVolumeMap.entrySet()) {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", stringListEntry.getKey());
                put("region_name", region);
            }};
            try {
                HttpClient.postEcs(String.format(url, "/bcp/v2/instance/delete"), jsonObject.toJSONString());
            } catch (Exception e) {
                LogUtil.error(e.getMessage(), e);
            }
            stringListEntry.getValue().forEach(volumeId->{
                JSONObject jsonObject2 = new JSONObject() {{
                    put("volume_id", volumeId);
                    put("region_name", region);
                }};
                try {
                    HttpClient.postEcs(String.format(url, "/bcp/v2/volume/delete"), jsonObject2.toJSONString());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(), e);
                }
            });

        }
    }

    @Override
    public void updateInstanceStatus(String server, Integer status) {
        instancesMapper.updateInstanceStatus(server, status);
    }

    @Override
    public void updateInstance(String server, Integer status, String ip) {
        Instances instances = instancesMapper.selectByUuid(server);
        instances.setIpAddr(ip);
        instances.setState(status);
        instancesMapper.updateByPrimaryKey(instances);
    }

    @Override
    public ResponseResult instanceStart(String instanceId, String regionName) {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instanceId);
                put("region_name", regionName);
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/instance/start"), jsonObject.toJSONString());
            updateInstanceStatus(instanceId, 1);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.STARTSUCCESS);
    }

    @Override
    public ResponseResult instanceStop(String instanceId, String regionName) {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instanceId);
                put("region_name", regionName);
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/instance/stop"), jsonObject.toJSONString());
            updateInstanceStatus(instanceId, 5);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.STOPSUCCESS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult instanceDelete(InstancesDeleteDTO instancesDeleteDTO) throws IOException {
        // 检查配额
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        Instances instances = instancesMapper.selectByUuid(instancesDeleteDTO.getInstanceId());
        Flavor flavor = flavorService.getRecordByUUid(instances.getFlavorId());
//        List<Volume> instanceVolumeList = volumeService.getInstanceVolumeList(instances.getUuid());
//        if (instanceVolumeList != null && !instanceVolumeList.isEmpty()) {
//            Double volumeSize = instanceVolumeList.stream().mapToDouble(Volume::getSize).sum();
//             磁盘空间
//            hashMap.put("10009", volumeSize.intValue());
        // 磁盘空间
//            hashMap.put("10010", instanceVolumeList.size());
//        }
//
        // 虚拟机个数
        hashMap.put("10003", 1);
//        // 内存个数
        hashMap.put("10001", flavor.getMemoryMb());
//        // vcpu 个数
        hashMap.put("10004", flavor.getVcpus());

//        // 虚拟机的uuid
//        String server = "";

        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instancesDeleteDTO.getInstanceId());
                put("region_name", instancesDeleteDTO.getRegion());
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/instance/delete"), jsonObject.toJSONString());
            instances.setState(9);
            instances.setDeleted(1);
            instances.setDeletedAt(new Date());
            instancesMapper.updateByPrimaryKey(instances);
            volumeMountService.deleteByinstancesUuid(instancesDeleteDTO.getInstanceId());
            quotaHandle.putQuotas(instancesDeleteDTO.getTicket(), Constant.FREE, hashMap);


        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
//            throw new QuotaException(-1, e.getMessage(), Constant.OCCUPY, hashMap);
            throw new CommonCustomException(-1, e.getMessage());
        }
        return ResponseResult.succ(CommonEnum.STARTSUCCESS);
    }

    @Override
    public ResponseResult instanceList(InstancesListDTO instancesListDTO) {
        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(instancesListDTO.getPageNum(), instancesListDTO.getPageSize());
            List<Instances> list = instancesMapper.getInstancesList(instancesListDTO);
            for (Instances instances : list) {
                String s = HttpClient.get(String.format(url, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s", instances.getUuid(), instances.getRegion())));
                JSONObject parse = (JSONObject) JSONObject.parse(s);
                JSONObject data = parse.getJSONObject("data");
                instances.setState(convertStatus(data.getString("status")));
            }
            PageInfo pageResult = new PageInfo(list);
            jsonObject.put("totalNum", pageResult.getTotal());
            jsonObject.put("list", pageResult.getList());
            return ResponseResult.succObj(jsonObject);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }

    }

    private Integer convertStatus(String status) {
        if (status == null) return 0;
        switch (status) {
            case "active":
                return 1;
            case "building":
                return 2;
            case "paused":
                return 3;
            case "suspended":
                return 4;
            case "stopped":
                return 5;
            case "rescued":
                return 6;
            case "resized":
                return 7;
            case "soft-delete":
                return 8;
            case "deleted":
                return 9;
            case "error":
                return 10;
            case "rebuilding":
                return 11;
        }
        return 0;
    }

    @Override
    public ResponseResult instanceRestart(String instanceId, String restartFlag, String regionName) {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instanceId);
                put("region_name", regionName);
                put("restart_flag", restartFlag);
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/instance/restart"), jsonObject.toJSONString());
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.RESTARTSUCCESS);

    }

    @Override
    public ResponseResult instanceVncConsole(String instanceId, String regionName) {
        try {
            JSONObject escData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/vnc-console?instance_id=%s&region_name=%s", instanceId, regionName)));
            return ResponseResult.succObj(escData);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }

    }

    @Override
    public ResponseResult udpateInfo(InstancesUpdateDTO instancesDTO, String userId, String groupId) {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instancesDTO.getUuid());
                put("region_name", instancesDTO.getRegion());
                put("description", instancesDTO.getDescription());
                put("instance_name", instancesDTO.getName());
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/instance/update"), jsonObject.toJSONString());
            Instances instances = instancesMapper.selectByUuid(instancesDTO.getUuid());
            instances.setRegion(instancesDTO.getRegion());
            instances.setDescription(instancesDTO.getDescription());
            instances.setName(instancesDTO.getName());
            instances.setUserId(userId);
            instances.setUpdateAt(new Date());
            instancesMapper.updateByPrimaryKey(instances);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.UPDATESUCCESS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult instanceAttachVolume(AttachVolumeDTO attachVolumeDTO) throws IOException {
        try {
            attachVolumeDTO.setStatus("available");
            checkInstanceVolume(attachVolumeDTO);
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", attachVolumeDTO.getInstanceId());
                put("region_name", attachVolumeDTO.getRegionName());
                put("volume_id", attachVolumeDTO.getVolumeId());
            }};
            JSONObject attachDate = HttpClient.postEcs(String.format(url, "/bcp/v2/instance/attach-volume"), jsonObject.toJSONString());
            // 挂载信息插入数据库中
            volumeMountService.insertVolumeMout(attachVolumeDTO.getInstanceId(), attachVolumeDTO.getVolumeId(), attachDate.getString("server"));
            volumeService.updateVolumeStatus(attachVolumeDTO.getVolumeId(), "7");

        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            throw ce;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw e;
        }
        return ResponseResult.succ(CommonEnum.ATTACHSUCCESS);
    }

    @Override
    public ResponseResult instanceDettachVolume(List<AttachVolumeDTO> attachVolumeDTOs) {
        try {
            for (AttachVolumeDTO attachVolumeDTO : attachVolumeDTOs) {
                attachVolumeDTO.setStatus("in-use");
                checkInstanceVolume(attachVolumeDTO);
                JSONObject jsonObject = new JSONObject() {{
                    put("instance_id", attachVolumeDTO.getInstanceId());
                    put("region_name", attachVolumeDTO.getRegionName());
                    put("volume_id", attachVolumeDTO.getVolumeId());
                }};
                JSONObject attachDate = HttpClient.postEcs(String.format(url, "/bcp/v2/instance/dettach-volume"), jsonObject.toJSONString());
                // 挂载信息插入数据库中
                volumeMountService.deleteByVolumeAndInstances(attachVolumeDTO.getInstanceId(), attachVolumeDTO.getVolumeId());
//            volumeService.updateVolumeStatus(attachVolumeDTO.getVolumeId(), "2");
            }
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.ATTACHSUCCESS);

    }

    @Override
    public ResponseResult instanceStatus(List<InstancesStatusDTO> instancesStatusList) {
        try {
            for (InstancesStatusDTO instances : instancesStatusList) {
                JSONObject escData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s", instances.getUuid(), instances.getRegion())));
                instances.setState(convertStatus(escData.getString("status")));
            }
            return ResponseResult.succObj(instancesStatusList);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult instanceRebuild(InstancesDTO instancesDTO, String userId, String groupId) {
        try {
            Image record = imageService.getRecordByUUid(instancesDTO.getImageUuid());
            JSONObject jsonObject = new JSONObject() {{
                put("name", instancesDTO.getName());
                put("region_name", instancesDTO.getRegion());
                put("adminPass", instancesDTO.getAdminPass());
                put("image", instancesDTO.getImageUuid());
                put("instance_id", instancesDTO.getUuid());
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/instance/rebuild"), jsonObject.toJSONString());
            Instances instances = instancesMapper.selectByUuid(instancesDTO.getUuid());
            instances.setName(instancesDTO.getName());
            instances.setRegion(instancesDTO.getRegion());
            instances.setImageUuid(instancesDTO.getImageUuid());
            instances.setOsType(record.getOsType());
            instancesMapper.updateByPrimaryKey(instances);
            return ResponseResult.succ(CommonEnum.REBUILDSUCCESS);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }


    }

    @Override
    public ResponseResult instanceVolumeList(InstancesVolumeDTO instancesVolumeDTO) {

        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(instancesVolumeDTO.getPageNum(), instancesVolumeDTO.getPageSize());
            List<Volume> list = volumeService.getInstanceVolumeList(instancesVolumeDTO.getInstanceId());
            PageInfo pageResult = new PageInfo(list);
            jsonObject.put("totalNum", pageResult.getTotal());
            jsonObject.put("list", pageResult.getList());
            return ResponseResult.succObj(jsonObject);
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }

    }

    @Override
    public ResponseResult instanceDetail(String instanceId, String regionName) {
        JSONObject ecs = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/detail?region_name=%s&instance_id=%s", regionName, instanceId)));
        ecs.put("region", regionName);
        return ResponseResult.succObj(ecs);

    }

    private void checkInstanceVolume(AttachVolumeDTO attachVolumeDTO) {
        JSONObject escData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s", attachVolumeDTO.getInstanceId(), attachVolumeDTO.getRegionName())));
        // 虚拟机为关机或开机状态
        if (!("active".equals(escData.getString("status")) || "stopped".equals(escData.getString("status")))) {
            throw new CommonCustomException(CommonEnum.ATTACHINSTANCEERROR);
        }
        JSONObject volumeData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/volume/status?volume_id=%s&region_name=%s", attachVolumeDTO.getVolumeId(), attachVolumeDTO.getRegionName())));
        // 卷必须为in-use状态
        if (!attachVolumeDTO.getStatus().equals(volumeData.getString("status"))) {
            throw new CommonCustomException(CommonEnum.ATTACHVOLUMEEERROR);
        }
    }

    public ResponseResult getFlavorList(Flavor flavor) {
        JSONObject jsonObject = new JSONObject();
        List<Flavor> list = flavorMapper.getFlavorList(flavor);
        jsonObject.put("list", list);
        return ResponseResult.succObj(jsonObject);
    }

    @Override
    public boolean checkInstanceIpaddr(String ipaddr, String instanceId, String groupId) {
        Integer count = instancesMapper.selectByIpaddr(ipaddr, instanceId, groupId);
        if (count != null) {
            return true;
        } else {
            return false;
        }
    }
}
