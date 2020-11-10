package com.gwm.cloudecs.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.exception.QuotaException;
import com.gwm.cloudcommon.handler.QuotaHandle;
import com.gwm.cloudcommon.model.VO.UserVo;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.common.model.DTO.*;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.common.model.entity.Volume;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.dao.InstancesMapper;
import com.gwm.cloudecs.dao.PortMapper;
import com.gwm.cloudecs.model.DTO.AttachVolumeDTO;
import com.gwm.cloudecs.model.DTO.InstancesRebuildDTO;
import com.gwm.cloudecs.model.DTO.InstancesUpdateDTO;
import com.gwm.cloudecs.model.DTO.InstancesVolumeDTO;
import com.gwm.cloudecs.model.entity.CloudAccount;
import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.model.entity.Port;
import com.gwm.cloudecs.schedule.*;
import com.gwm.cloudecs.service.*;
import com.gwm.streamsetscommon.client.DOPClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.Result;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EcsServiceImpl implements EcsService {

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
    ImageService imageService;

    @Autowired
    CloudAccountService cloudAccountService;

    @Autowired
    DOPClient dOPClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult createInstances(InstancesDTO instancesDTO) {
        Flavor flavor = flavorService.getRecordByUUid(instancesDTO.getFlavorId());
        Double volumeSize = instancesDTO.getVolumes().stream().mapToDouble(InstancseVolumeDTO::getSize).sum();
        // 虚拟机个数
        Integer count = instancesDTO.getCount();
        // 检查配额
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        // 虚拟机个数
        hashMap.put("10003", count);
        if ("aliyun".equals(instancesDTO.getType())) {
            // 内存个数
            hashMap.put("10001", instancesDTO.getMemoryMb() * count);
            // vcpu 个数
            hashMap.put("10004", instancesDTO.getVcpus() * count);
        } else {
            // 内存个数
            hashMap.put("10001", flavor.getMemoryMb() * count);
            // vcpu 个数
            hashMap.put("10004", flavor.getVcpus() * count);
        }

        // 磁盘空间
        hashMap.put("10009", volumeSize.intValue() * count);
        // 磁盘空间
        hashMap.put("10010", instancesDTO.getVolumes().size() * count);
        // 检查配额
        quotaHandle.putQuotas(instancesDTO.getTicket(), Constant.OCCUPY, hashMap);
        instancesDTO.setHashMap(hashMap);
        Map<String, List<String>> instanceVolumeMap = new HashMap<>();
        try {
            // openstack
            if ("openstack".equals(instancesDTO.getType())) {
                return createOpenstackInstances(instanceVolumeMap, count, flavor, instancesDTO);
                // vmware
            } else if ("vmware".equals(instancesDTO.getType())) {
                return createVmwareInstances(count, flavor, instancesDTO);
            } else if ("aliyun".equals(instancesDTO.getType())) {
                return createAliyunInstances(count, instancesDTO);
            } else {
                return ResponseResult.error("创建云主机失败，没有该type参数");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            handleInstanceVolume(instancesDTO.getType(), instanceVolumeMap, instancesDTO.getRegion());
            throw new QuotaException(-1, e.getMessage(), Constant.FREE, hashMap);
        }
    }

    /**
     * 创建阿里云的云主机
     *
     * @param count
     * @param instancesDTO
     * @return
     */
    private ResponseResult createAliyunInstances(Integer count, InstancesDTO instancesDTO) {
        RunInstancesRequest request = new RunInstancesRequest();
        request.setRegionId(instancesDTO.getRegion());
        request.setImageId(instancesDTO.getImageUuid());
        request.setInstanceType(instancesDTO.getFlavorId());
        request.setSecurityGroupId(instancesDTO.getSecurityGroup());
        request.setVSwitchId(instancesDTO.getVSwitchId());
        request.setInstanceName(instancesDTO.getName());
        request.setPassword(instancesDTO.getAdminPass());
        request.setZoneId(instancesDTO.getZone());
        request.setInternetChargeType(instancesDTO.getChargeType());
        request.setClientToken(UUID.randomUUID().toString());
        request.setAmount(count);
        instancesDTO.setState(2);
        instancesDTO.setCreateAt(new Date());
        instancesDTO.setDeleted(0);

        if (instancesDTO.getVolumes().size() != 0) {
            List<RunInstancesRequest.DataDisk> dataDiskList = new ArrayList<RunInstancesRequest.DataDisk>();
            instancesDTO.getVolumes().forEach(volume -> {
                RunInstancesRequest.DataDisk dataDisk1 = new RunInstancesRequest.DataDisk();
                dataDisk1.setSize(volume.getSize());
                dataDisk1.setCategory(volume.getCategory());
                dataDisk1.setDeleteWithInstance(volume.getDeleteWithInstance());
                dataDiskList.add(dataDisk1);
            });
            request.setDataDisks(dataDiskList);
        }

        // 发起请求并处理返回或异常。
        RunInstancesResponse response;
        try {
            response = GlobalVarConfig.client.getAcsResponse(request);
            JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
            JSONArray instanceIdSets = jsonObject.getJSONArray("instanceIdSets");

            for (int i = 0; i < instanceIdSets.size(); i++) {
                String instanceId = instanceIdSets.getString(i);
                List<VolumeDTO> instancseVolumeDTOList = new ArrayList<>();
                for (int j = 0; j < instancesDTO.getVolumes().size(); j++) {
                    InstancseVolumeDTO instancseVolumeDTO = instancesDTO.getVolumes().get(j);
                    VolumeDTO volumeDTO = new VolumeDTO();
                    volumeDTO.setBootable("1");
                    volumeDTO.setSize(instancseVolumeDTO.getSize());
                    volumeDTO.setName(String.format("volume-%s-%s", instancesDTO.getName(), j + 1));
                    volumeDTO.setEnv(instancesDTO.getEnv());
                    volumeDTO.setRegion(instancesDTO.getRegion());
                    volumeDTO.setUserId(instancesDTO.getUserId());
                    volumeDTO.setProjectId(instancesDTO.getProjectId());
                    volumeDTO.setVolumeType(instancseVolumeDTO.getCategory());
                    volumeDTO.setType(instancesDTO.getType());
                    volumeService.insert(volumeDTO);
                    instancseVolumeDTOList.add(volumeDTO);
                }
                String formatName = String.format("%s-%s", instancesDTO.getName(), i + 1);
                instancesDTO.setName(formatName);
                instancesDTO.setUuid(instanceId);
                instancesMapper.insert(instancesDTO);
                log.info(String.format("创建云主机定时线程开启.....线程名称为:%s", String.format("ecs-aliyun-%s", formatName)));
                ScheduleUtil.stard(new ECSAliyunTask(String.format("ecs-aliyun-%s", formatName), instanceId, instancesDTO.getRegion(), instancseVolumeDTOList), 10, 5, TimeUnit.SECONDS);

            }
            return ResponseResult.succObj(CommonEnum.CREATESUCCESS, new JSONObject() {{
                put("serverList", instanceIdSets);
            }});
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 创建云主机openstack方式
     *
     * @param instancesDTO
     * @return
     */
    private ResponseResult createVmwareInstances(Integer count, Flavor flavor, InstancesDTO instancesDTO) {
        // vmware中的账号
        CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instancesDTO.getType(), instancesDTO.getZone(), instancesDTO.getRegion()));
        if (cloudAccount == null) {
            throw new CommonCustomException(-1, "该区域下不能创建vmware类型的云主机");
        }
        Image record = imageService.getRecordByUUid(instancesDTO.getImageUuid());
        if (record == null) {
            throw new CommonCustomException(-1, "该镜像存在问题，请重试");
        }
        String name = instancesDTO.getName();
        // 创建虚拟机个数
        for (int i = 0; i < count; i++) {
            String threadName = String.format("ecs-%s-%s", name, i + 1);
            String formatName = String.format("%s-%s", name, i + 1);
            instancesDTO.setName(formatName);
            log.info(String.format("创建云主机线程开启.....线程名称为:%s", threadName));
            // 浅克隆。因为不需要修改volume对象
            InstancesDTO clone = instancesDTO.clone();
            ThreadPoolUtils.execute(new ECSVmwareTask(threadName, flavor, clone, GlobalVarConfig.ecsUrl, cloudAccount, record, dOPClient));
        }
        return ResponseResult.succ();
    }

    /**
     * 创建云主机openstack方式
     *
     * @param instancesDTO
     * @return
     */
    private ResponseResult createOpenstackInstances(Map<String, List<String>> instanceVolumeMap, Integer count, Flavor flavor, InstancesDTO instancesDTO) throws IOException, ClientException {
        // 虚拟机的uuid
        List<String> serverList = new ArrayList();
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
        ecsParam.put("type", instancesDTO.getType());
        instancesDTO.setState(2);
        instancesDTO.setRootGb(flavor.getRootGb());
        instancesDTO.setMemoryMb(flavor.getMemoryMb());
        instancesDTO.setVcpus(flavor.getVcpus());
        instancesDTO.setCreateAt(new Date());
        instancesDTO.setDeleted(0);
        instancesDTO.setOsType(record.getOsType());
        String name = instancesDTO.getName();
        // 创建虚拟机个数
        for (int i = 0; i < count; i++) {
            String formatName = String.format("%s-%s", name, i + 1);
            ecsParam.put("name", formatName);
            JSONObject escData = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/create"), ecsParam.toJSONString());
            String server = escData.getString("server");
            instancesDTO.setName(formatName);
            instancesDTO.setUuid(server);
            instancesMapper.insert(instancesDTO);
            // 创建磁盘
            List<String> volumeServerList = new ArrayList<>();
            for (int j = 0; j < instancesDTO.getVolumes().size(); j++) {
                InstancseVolumeDTO instancseVolumeDTO = instancesDTO.getVolumes().get(j);
                VolumeDTO volumeDTO = new VolumeDTO();
                volumeDTO.setSize(instancseVolumeDTO.getSize());
                volumeDTO.setName(String.format("volume-%s-%s", instancesDTO.getName(), j + 1));
                volumeDTO.setEnv(instancesDTO.getEnv());
                volumeDTO.setRegion(instancesDTO.getRegion());
                volumeDTO.setZone(instancesDTO.getZone());
                volumeDTO.setUserId(instancesDTO.getUserId());
                volumeDTO.setProjectId(instancesDTO.getProjectId());
                volumeDTO.setPriceModel(instancesDTO.getPriceModel());
                volumeDTO.setFlag("2");
                volumeDTO.setType(instancesDTO.getType());
                volumeDTO.setVolumeType("1");
                volumeServerList.add(volumeService.insertRecord(volumeDTO));
            }
            instanceVolumeMap.put(server, volumeServerList);
            serverList.add(server);
        }
        for (Map.Entry<String, List<String>> stringListEntry : instanceVolumeMap.entrySet()) {
            log.info(String.format("创建云主机定时线程开启.....线程名称为:%s", String.format("ecs-%s", stringListEntry.getKey())));
            ScheduleUtil.stard(new ECSOpenstackTask(String.format("ecs-%s", stringListEntry.getKey()), stringListEntry.getKey(), stringListEntry.getValue(), GlobalVarConfig.ecsUrl, instancesDTO.getRegion(), dOPClient), 10, 5, TimeUnit.SECONDS);
        }
        return ResponseResult.succObj(CommonEnum.CREATESUCCESS, new JSONObject() {{
            put("serverList", serverList);
        }});
    }


    /**
     * 处理异常云主机问题。
     *
     * @param instanceVolumeMap
     * @param region
     */
    private void handleInstanceVolume(String type, Map<String, List<String>> instanceVolumeMap, String region) {
        if ("openstack".equals(type)) {
            for (Map.Entry<String, List<String>> stringListEntry : instanceVolumeMap.entrySet()) {
                JSONObject jsonObject = new JSONObject() {{
                    put("instance_id", stringListEntry.getKey());
                    put("region_name", region);
                }};
                try {
                    HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/delete"), jsonObject.toJSONString());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                stringListEntry.getValue().forEach(volumeId -> {
                    JSONObject jsonObject2 = new JSONObject() {{
                        put("volume_id", volumeId);
                        put("region_name", region);
                    }};
                    try {
                        HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/volume/delete"), jsonObject2.toJSONString());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            }
        } else if ("vmware".equals(type)) {
            // TO DO

        } else {

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
            }};
            updateInstanceStatus(instanceId, 1);
            Instances instances = instancesMapper.selectByUuid(instanceId);
            if ("openstack".equals(instances.getType())) {
                jsonObject.put("region_name", regionName);
                jsonObject.put("type", "openstack");
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/start"), jsonObject.toJSONString());
            } else if ("vmware".equals(instances.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                jsonObject.put("type", "vmware");
                jsonObject.put("vmware_user", cloudAccount.getUserName());
                jsonObject.put("vmware_password", cloudAccount.getPassword());
                jsonObject.put("vmware_host", cloudAccount.getUrl());
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/start"), jsonObject.toJSONString());
            } else if ("aliyun".equals(instances.getType())) {
                StartInstanceRequest request = new StartInstanceRequest();
                request.setInstanceId(instanceId);
                request.setRegionId(regionName);
                StartInstanceResponse response = GlobalVarConfig.client.getAcsResponse(request);
            }
            updateInstanceStatus(instanceId, 1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.STARTSUCCESS);
    }

    @Override
    public ResponseResult instanceStop(String instanceId, String regionName) {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instanceId);
            }};
            Instances instances = instancesMapper.selectByUuid(instanceId);
            if ("openstack".equals(instances.getType())) {
                jsonObject.put("region_name", regionName);
                jsonObject.put("type", "openstack");
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/stop"), jsonObject.toJSONString());
            } else if ("vmware".equals(instances.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                jsonObject.put("type", "vmware");
                jsonObject.put("vmware_user", cloudAccount.getUserName());
                jsonObject.put("vmware_password", cloudAccount.getPassword());
                jsonObject.put("vmware_host", cloudAccount.getUrl());
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/stop"), jsonObject.toJSONString());
            } else if ("aliyun".equals(instances.getType())) {
                StopInstanceRequest request = new StopInstanceRequest();
                request.setInstanceId(instanceId);
                request.setRegionId(regionName);
                StopInstanceResponse response = GlobalVarConfig.client.getAcsResponse(request);
            }
            updateInstanceStatus(instanceId, 5);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.STOPSUCCESS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult instanceDelete(InstancesDeleteDTO instancesDeleteDTO) {
        try {
            // 检查配额
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            // 虚拟机个数
            hashMap.put("10003", instancesDeleteDTO.getInstanceIds().size());
            for (String instanceId : instancesDeleteDTO.getInstanceIds()) {
                Instances instances = instancesMapper.selectByUuid(instanceId);
                if ("openstack".equals(instances.getType())) {
                    Flavor flavor = flavorService.getRecordByUUid(instances.getFlavorId());

                    //        // 内存个数
                    hashMap.put("10001", hashMap.get("10001") == null ? flavor.getMemoryMb() : hashMap.get("10001") + flavor.getMemoryMb());
                    //        // vcpu 个数
                    hashMap.put("10004", hashMap.get("10004") == null ? flavor.getVcpus() : hashMap.get("10004") + flavor.getVcpus());

                    JSONObject jsonObject = new JSONObject() {{
                        put("instance_id", instanceId);
                        put("region_name", instancesDeleteDTO.getRegion());
                        put("type", "openstack");
                    }};
                    HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/delete"), jsonObject.toJSONString());
                    volumeMountService.deleteByinstancesUuid(instanceId);
                    dOPClient.deleteHostDOP(instances.getIpAddr());
                } else if ("vmware".equals(instances.getType())) {
                    Flavor flavor = flavorService.getRecordByUUid(instances.getFlavorId());
                    //        // 内存个数
                    hashMap.put("10001", hashMap.get("10001") == null ? flavor.getMemoryMb() : hashMap.get("10001") + flavor.getMemoryMb());
                    //        // vcpu 个数
                    hashMap.put("10004", hashMap.get("10004") == null ? flavor.getVcpus() : hashMap.get("10004") + flavor.getVcpus());
                    CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                    JSONObject jsonObject = new JSONObject() {{
                        put("instance_id", instanceId);
                        put("type", "vmware");
                        put("vmware_user", cloudAccount.getUserName());
                        put("vmware_password", cloudAccount.getPassword());
                        put("vmware_host", cloudAccount.getUrl());
                    }};
                    HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/delete"), jsonObject.toJSONString());
                    volumeService.getInstanceVolumeList(instanceId).forEach(volume -> {
                        volumeMountService.deleteByVolumeAndInstances(instances.getUuid(), volume.getUuid());
                        volumeService.volumeDelete(volume.getUuid());
                    });
                    dOPClient.deleteHostDOP(instances.getIpAddr());
                } else if ("aliyun".equals(instances.getType())) {
                    //        // 内存个数
                    hashMap.put("10001", hashMap.get("10001") == null ? instances.getMemoryMb() : hashMap.get("10001") + instances.getMemoryMb());
                    //        // vcpu 个数
                    hashMap.put("10004", hashMap.get("10004") == null ? instances.getVcpus() : hashMap.get("10004") + instances.getVcpus());

                    // 创建API请求并设置参数。
                    DeleteInstanceRequest request = new DeleteInstanceRequest();
                    // 设置一个地域ID。
                    request.setRegionId(instances.getRegion());
                    // 指定一个实例ID。
                    request.setInstanceId(instances.getUuid());

                    // 发起请求并处理应答或异常。
                    DeleteInstanceResponse response = GlobalVarConfig.client.getAcsResponse(request);
                    JSONObject jsonObject1 = JSONObject.parseObject(new Gson().toJson(response));
                    // 删除失败
                    if (StrUtil.isNotEmpty(jsonObject1.getString("code"))) {
                        throw new CommonCustomException(-1, jsonObject1.getString("message"));
                    }
                }
                instances.setState(9);
                instances.setDeleted(1);
                instances.setDeletedAt(new Date());
                instancesMapper.updateByPrimaryKey(instances);


            }
            quotaHandle.putQuotas(instancesDeleteDTO.getTicket(), Constant.FREE, hashMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonCustomException(-1, e.getMessage());
        }

        return ResponseResult.succ(CommonEnum.DELSUCCESS);
    }

    @Override
    public ResponseResult instanceList(InstancesListDTO instancesListDTO) {
        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(instancesListDTO.getPageNum(), instancesListDTO.getPageSize());
            List<Instances> list = instancesMapper.getInstancesList(instancesListDTO);
            for (Instances instances : list) {
                String s = "{}";
                if ("openstack".equals(instances.getType())) {
                    s = HttpClient.get(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s&type=%s", instances.getUuid(), instances.getRegion(), instances.getType())));
                    JSONObject parse = (JSONObject) JSONObject.parse(s);
                    JSONObject data = parse.getJSONObject("data");
                    instances.setState(convertStatus(data.getString("status")));
                } else if ("vmware".equals(instances.getType())) {
                    // 可能该vmware云主机还没创建成功。所以没有uuid 暂时定义状态为创建中
                    if (instances.getUuid() == null || "".equals(instances.getUuid())) {
                        instances.setState(2);
                        continue;
                    }
                    CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                    s = HttpClient.get(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/status?instance_id=%s&vmware_user=%s&type=%s&vmware_password=%s&vmware_host=%s", instances.getUuid(), cloudAccount.getUserName(), instances.getType(), cloudAccount.getPassword(), cloudAccount.getUrl())));
                    JSONObject parse = (JSONObject) JSONObject.parse(s);
                    JSONObject data = parse.getJSONObject("data");
                    instances.setState(convertStatus(data.getString("status")));
                } else if ("aliyun".equals(instances.getType())) {
                    DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
                    request.setRegionId(instances.getRegion());
                    List<String> instanceIdList = new ArrayList<String>();
                    instanceIdList.add(instances.getUuid());
                    request.setInstanceIds(instanceIdList);
                    DescribeInstanceStatusResponse response = GlobalVarConfig.client.getAcsResponse(request);
                    JSONObject responseJson = JSONObject.parseObject(new Gson().toJson(response));
                    JSONArray instanceStatuses = responseJson.getJSONArray("instanceStatuses");
                    log.info("云主机状态查询" + instanceStatuses.toJSONString());
                    if (instanceStatuses.size() == 0) {
                        instances.setState(2);
                    } else {
                        instances.setState(convertStatus(instanceStatuses.getJSONObject(0).getString("status")));
                    }
                }

            }
            // 获取与主机列表通过状态字段查询 暂时不进行使用库中的字段。还有就是使用状态字段时每页数据只能是最大值。
            if (StrUtil.isNotEmpty(instancesListDTO.getStatus())) {
                List<Integer> Integers = Arrays.asList(instancesListDTO.getStatus().split(",")).stream().map(string -> Integer.valueOf(string)).collect(Collectors.toList());
                List<Instances> collect = list.stream()
                        .filter(instance -> Integers.contains(instance.getState())
                        ).collect(Collectors.toList());
                jsonObject.put("totalNum", collect.size());
                jsonObject.put("list", collect);
                return ResponseResult.succObj(jsonObject);
            }
            PageInfo pageResult = new PageInfo(list);
            jsonObject.put("totalNum", pageResult.getTotal());
            jsonObject.put("list", pageResult.getList());
            return ResponseResult.succObj(jsonObject);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }

    }

    private Integer convertStatus(String status) {
        if (status == null) return 0;
        switch (status) {
            case "active":
                // 阿里云状态
            case "Running":
                return 1;
            case "building":
                // 阿里云状态
            case "Pending":
                return 2;
            case "paused":
                return 3;
            case "suspended":
                return 4;
            case "stopped":
                // 阿里云状态
            case "Stopped":
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
            // 阿里云状态 停止中
            case "Stopping":
                return 12;
                // 阿里云状态 启动中
            case "Starting":
                return 13;
        }
        return 0;
    }

    @Override
    public ResponseResult instanceRestart(String instanceId, String restartFlag, String regionName) {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instanceId);
            }};
            Instances instances = instancesMapper.selectByUuid(instanceId);
            if ("openstack".equals(instances.getType())) {
                jsonObject.put("region_name", regionName);
                jsonObject.put("type", "openstack");
                jsonObject.put("restart_flag", restartFlag);
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/restart"), jsonObject.toJSONString());
            } else if ("vmware".equals(instances.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                jsonObject.put("type", "vmware");
                jsonObject.put("vmware_user", cloudAccount.getUserName());
                jsonObject.put("vmware_password", cloudAccount.getPassword());
                jsonObject.put("vmware_host", cloudAccount.getUrl());
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/restart"), jsonObject.toJSONString());
            } else if ("aliyun".equals(instances.getType())) {
                RebootInstanceRequest request = new RebootInstanceRequest();
                request.setInstanceId(instanceId);
                request.setRegionId(regionName);
                RebootInstanceResponse response = GlobalVarConfig.client.getAcsResponse(request);
            }
            updateInstanceStatus(instanceId, 5);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.RESTARTSUCCESS);

    }

    @Override
    public ResponseResult instanceVncConsole(String instanceId, String regionName) {
        try {
            JSONObject escData = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/vnc-console?instance_id=%s&region_name=%s", instanceId, regionName)));
            return ResponseResult.succObj(escData);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }

    }

    @Override
    public ResponseResult udpateInfo(InstancesUpdateDTO instancesDTO, String userId, String groupId) {
        try {
            Instances instances = instancesMapper.selectByUuid(instancesDTO.getUuid());
            if ("openstack".equals(instances.getType())) {
                JSONObject jsonObject = new JSONObject() {{
                    put("instance_id", instancesDTO.getUuid());
                    put("region_name", instancesDTO.getRegion());
                    put("description", instancesDTO.getDescription());
                    put("instance_name", instancesDTO.getName());
                }};
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/update"), jsonObject.toJSONString());
            }
            instances.setRegion(instancesDTO.getRegion());
            instances.setDescription(instancesDTO.getDescription());
            instances.setName(instancesDTO.getName());
            instances.setUserId(userId);
            instances.setUpdateAt(new Date());
            instancesMapper.updateByPrimaryKey(instances);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.UPDATESUCCESS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult instanceAttachVolume(AttachVolumeDTO attachVolumeDTO) throws IOException, ClientException {
        try {
            attachVolumeDTO.setStatus("available");
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", attachVolumeDTO.getInstanceId());
            }};
            if ("openstack".equals(attachVolumeDTO.getType())) {
                jsonObject.put("region_name", attachVolumeDTO.getRegionName());
                jsonObject.put("volume_id", attachVolumeDTO.getVolumeId());
                JSONObject attachDate = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/attach-volume"), jsonObject.toJSONString());

            } else if ("vmware".equals(attachVolumeDTO.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", attachVolumeDTO.getType(), attachVolumeDTO.getZone(), attachVolumeDTO.getRegionName()));
                jsonObject.put("disk_id", attachVolumeDTO.getVolumeId());
                jsonObject.put("vmware_user", cloudAccount.getUserName());
                jsonObject.put("vmware_password", cloudAccount.getPassword());
                jsonObject.put("vmware_host", cloudAccount.getUrl());
                JSONObject jsonObject1 = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/add-volume"), jsonObject.toJSONString());

            } else if ("aliyun".equals(attachVolumeDTO.getType())) {
                AttachDiskRequest request = new AttachDiskRequest();
                request.setRegionId(attachVolumeDTO.getRegionName());
                request.setInstanceId(attachVolumeDTO.getInstanceId());
                request.setDiskId(attachVolumeDTO.getVolumeId());
                request.setDeleteWithInstance(attachVolumeDTO.isDeleteWithInstance());
                AttachDiskResponse response = GlobalVarConfig.client.getAcsResponse(request);
            } else {
                throw new CommonCustomException("不支持的类型");
            }
            // 挂载信息插入数据库中
            volumeMountService.insertVolumeMout(attachVolumeDTO.getInstanceId(), attachVolumeDTO.getVolumeId(), attachVolumeDTO.getVolumeId());
            volumeService.updateVolumeStatus(attachVolumeDTO.getVolumeId(), "7");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
                }};
                if ("openstack".equals(attachVolumeDTO.getType())) {
                    jsonObject.put("region_name", attachVolumeDTO.getRegionName());
                    jsonObject.put("volume_id", attachVolumeDTO.getVolumeId());
                    JSONObject attachDate = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/dettach-volume"), jsonObject.toJSONString());

                } else if ("vmware".equals(attachVolumeDTO.getType())) {
                    CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", attachVolumeDTO.getType(), attachVolumeDTO.getZone(), attachVolumeDTO.getRegionName()));
                    jsonObject.put("disk_id", attachVolumeDTO.getVolumeId());
                    jsonObject.put("vmware_user", cloudAccount.getUserName());
                    jsonObject.put("vmware_password", cloudAccount.getPassword());
                    jsonObject.put("vmware_host", cloudAccount.getUrl());
                    HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/delete-volume"), jsonObject.toJSONString());
                    volumeService.volumeDelete(attachVolumeDTO.getVolumeId());
                } else if ("aliyun".equals(attachVolumeDTO.getType())) {
                    DetachDiskRequest request = new DetachDiskRequest();
                    request.setRegionId(attachVolumeDTO.getRegionName());
                    request.setInstanceId(attachVolumeDTO.getInstanceId());
                    request.setDiskId(attachVolumeDTO.getVolumeId());
                    DetachDiskResponse response = GlobalVarConfig.client.getAcsResponse(request);
                } else {
                    throw new CommonCustomException("不支持的类型");
                }
                // 挂载信息插入数据库中
                volumeMountService.deleteByVolumeAndInstances(attachVolumeDTO.getInstanceId(), attachVolumeDTO.getVolumeId());

            }


        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.DEATTACHSUCCESS);

    }

    @Override
    public ResponseResult instanceStatus(List<InstancesStatusDTO> instancesStatusList) {
        try {
            for (InstancesStatusDTO instances : instancesStatusList) {
                instances.setState(convertStatus(getInstanceStatus(instances)));
            }
            return ResponseResult.succObj(instancesStatusList);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
    }

    public String getInstanceStatus(InstancesStatusDTO instances) throws Exception {
        String s = "{}";
        if ("openstack".equals(instances.getType())) {
            s = HttpClient.get(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s&type=%s", instances.getUuid(), instances.getRegion(), instances.getType())));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            JSONObject data = parse.getJSONObject("data");
            return data.getString("status");
        } else if ("vmware".equals(instances.getType())) {
            // 可能该vmware云主机还没创建成功。所以没有uuid 暂时定义状态为创建中
            if (instances.getUuid() == null || "".equals(instances.getUuid())) {
                return "building";
            }
            CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
            s = HttpClient.get(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/status?instance_id=%s&vmware_user=%s&type=%s&vmware_password=%s&vmware_host=%s", instances.getUuid(), cloudAccount.getUserName(), instances.getType(), cloudAccount.getPassword(), cloudAccount.getUrl())));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            JSONObject data = parse.getJSONObject("data");
            return data.getString("status");
        } else if ("aliyun".equals(instances.getType())) {
            DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
            request.setRegionId(instances.getRegion());
            List<String> instanceIdList = new ArrayList<String>();
            instanceIdList.add(instances.getUuid());
            request.setInstanceIds(instanceIdList);
            DescribeInstanceStatusResponse response = GlobalVarConfig.client.getAcsResponse(request);
            JSONObject responseJson = JSONObject.parseObject(new Gson().toJson(response));
            JSONArray instanceStatuses = responseJson.getJSONArray("instanceStatuses");
            return instanceStatuses.getJSONObject(0).getString("status");
        }
            return null;

    }

    @Override
    public ResponseResult instanceRebuild(InstancesRebuildDTO instancesDTO, String userId, String groupId) {
        try {
            Instances instances = instancesMapper.selectByUuid(instancesDTO.getInstancesId());
            JSONObject jsonObject = new JSONObject() {{
                put("instance_id", instancesDTO.getInstancesId());
                put("type", instances.getType());
            }};
            if ("openstack".equals(instances.getType())) {
                Image record = imageService.getRecordByUUid(instancesDTO.getImageUuid());
                jsonObject.put("name", instancesDTO.getName());
                jsonObject.put("region_name", instances.getRegion());
                jsonObject.put("adminPass", instancesDTO.getAdminPass());
                jsonObject.put("image", instancesDTO.getImageUuid());
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/rebuild"), jsonObject.toJSONString());
                instances.setName(instancesDTO.getName());
                instances.setImageUuid(instancesDTO.getImageUuid());
                instances.setOsType(record.getOsType());
                instancesMapper.updateByPrimaryKey(instances);
            } else if ("vmware".equals(instances.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                jsonObject.put("vmware_user", cloudAccount.getUserName());
                jsonObject.put("vmware_password", cloudAccount.getPassword());
                jsonObject.put("vmware_host", cloudAccount.getUrl());
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/rebuild"), jsonObject.toJSONString());
            } else if ("aliyun".equals(instances.getType())) {
                throw new CommonCustomException("该云主机类型不支持重建操作");
            }
            return ResponseResult.succ(CommonEnum.REBUILDSUCCESS);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }

    }

    @Override
    public ResponseResult instanceDetail(String instanceId, String regionName) {
        try {
            Instances instances = instancesMapper.selectByUuid(instanceId);
            JSONObject ecs = new JSONObject();
            if ("openstack".equals(instances.getType())) {
                ecs = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/detail?type=openstack&region_name=%s&instance_id=%s", regionName, instanceId)));
            } else if ("vmware".equals(instances.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", instances.getType(), instances.getZone(), instances.getRegion()));
                Image record = imageService.getRecordByUUid(instances.getImageUuid());
                Flavor flavor = flavorService.getRecordByUUid(instances.getFlavorId());
                ecs = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/detail?type=vmware&vmware_user=%s&vmware_password=%s&vmware_host=%s&instance_id=%s", cloudAccount.getUserName(), cloudAccount.getPassword(), cloudAccount.getUrl(), instanceId)));
                ecs.put("network", cloudAccount.getNetwork());
                ecs.put("created", DateUtil.format(instances.getCreateAt(), "yyyy-MM-dd HH:mm:ss"));
                ecs.put("template", record.getName());
                ecs.put("zone", instances.getZone());
                ecs.put("imageName", record.getName());
                ecs.put("flavorDisk", flavor.getRootGb());
                ecs.put("ipaddr", instances.getIpAddr());
            } else if ("aliyun".equals(instances.getType())) {
                DescribeInstancesRequest request = new DescribeInstancesRequest();
                request.setRegionId(regionName);
                request.setInstanceIds("[\"" + instances.getUuid() + "\"]");
                DescribeInstancesResponse response = GlobalVarConfig.client.getAcsResponse(request);
                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
                JSONArray instancesJsonArray = jsonObject.getJSONArray("instances");
                ecs = instancesJsonArray.getJSONObject(0);
            }
            ecs.put("type", instances.getType());
            ecs.put("region", regionName);
            return ResponseResult.succObj(ecs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseResult.error();
    }

    private void checkInstanceVolume(AttachVolumeDTO attachVolumeDTO) throws Exception {
        String instanceStatus = getInstanceStatus(new InstancesStatusDTO() {{
            setRegion(attachVolumeDTO.getRegionName());
            setType(attachVolumeDTO.getType());
            setUuid(attachVolumeDTO.getInstanceId());
            setZone(attachVolumeDTO.getZone());
        }});

        // 虚拟机为关机或开机状态
        if (!("active".equals(instanceStatus) || "stopped".equals(instanceStatus)
                ||"Running".equals(instanceStatus) || "Stopped".equals(instanceStatus))) {
            throw new CommonCustomException(CommonEnum.ATTACHINSTANCEERROR);
        }

        if ("openstack".equals(attachVolumeDTO.getType())) {
            JSONObject volumeData = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/volume/status?volume_id=%s&region_name=%s", attachVolumeDTO.getVolumeId(), attachVolumeDTO.getRegionName())));
            // 卷必须为in-use状态
            if (!attachVolumeDTO.getStatus().equals(volumeData.getString("status"))) {
                throw new CommonCustomException(CommonEnum.ATTACHVOLUMEEERROR);
            }
        }

    }

    public ResponseResult getFlavorList(Flavor flavor) {
        JSONObject jsonObject = new JSONObject();
        List<Flavor> list = flavorService.getFlavorList(flavor);
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

    @Override
    public ResponseResult getAliRegion() {
        DescribeRegionsRequest request = new DescribeRegionsRequest();
        try {
            DescribeRegionsResponse response = GlobalVarConfig.client.getAcsResponse(request);
            JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
            // 只有杭州北京两个region
            JSONArray collect = jsonObject.getJSONArray("regions").stream()
                    .filter(json -> "cn-beijing".equals(((JSONObject) json).getString("regionId")) || "cn-hangzhou".equals(((JSONObject) json).getString("regionId")))
                    .collect(Collectors.toCollection(JSONArray::new));
            jsonObject.put("regions", collect);
            return ResponseResult.succObj(jsonObject);
        } catch (ServerException e) {
            log.error(e.getErrMsg(), e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }
        return ResponseResult.error();
    }

    @Override
    public ResponseResult getAliZone(String region) {
        try {
            if ("cn-hangzhou".equals(region)) {
                DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
                request.setRegionId(region);
                request.setDestinationResource("Zone");
                DescribeAvailableResourceResponse response = GlobalVarConfig.client.getAcsResponse(request);
                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
                JSONArray availableZones = jsonObject.getJSONArray("availableZones");
                JSONArray collect = availableZones.stream()
                        .filter(json -> "cn-hangzhou-h".equals(((JSONObject) json).getString("zoneId")) ||
                                "cn-hangzhou-i".equals(((JSONObject) json).getString("zoneId"))
                                || "cn-hangzhou-j".equals(((JSONObject) json).getString("zoneId")))
                        .collect(Collectors.toCollection(JSONArray::new));
                jsonObject.put("availableZones", collect);
                return ResponseResult.succObj(jsonObject);
            } else if ("cn-beijing".equals(region)) {
                DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
                request.setRegionId(region);
                request.setDestinationResource("Zone");
                DescribeAvailableResourceResponse response = GlobalVarConfig.client.getAcsResponse(request);
                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
                JSONArray availableZones = jsonObject.getJSONArray("availableZones");
                JSONArray collect = availableZones.stream()
                        .filter(json -> "cn-beijing-h".equals(((JSONObject) json).getString("zoneId")) ||
                                "cn-beijing-a".equals(((JSONObject) json).getString("zoneId"))
                                || "cn-beijing-j".equals(((JSONObject) json).getString("zoneId")))
                        .collect(Collectors.toCollection(JSONArray::new));
                jsonObject.put("availableZones", collect);
                return ResponseResult.succObj(jsonObject);
            } else {
                return ResponseResult.error("暂时不支持的region");
            }
        } catch (ServerException e) {
            log.error(e.getErrMsg(), e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }
        return ResponseResult.error();
    }

    @Override
    public ResponseResult getAliInstanceTypes(String region, String zone) {
        DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
        request.setRegionId(region);
        request.setDestinationResource("InstanceType");
        if (StrUtil.isNotEmpty(zone)) {
            request.setZoneId(zone);
        }
        try {
            DescribeAvailableResourceResponse response = GlobalVarConfig.client.getAcsResponse(request);
            JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
            JSONArray availableZones = jsonObject.getJSONArray("availableZones");
            JSONObject availableZone = availableZones.getJSONObject(0);
            JSONArray availableResources = availableZone.getJSONArray("availableResources");
            JSONObject availableResource = availableResources.getJSONObject(0);
            JSONArray supportedResources = availableResource.getJSONArray("supportedResources");
            List<String> instanceTypesList = supportedResources.stream().filter(json -> "Available".equals(((JSONObject) json).getString("status")))
                    .map(json -> ((JSONObject) json).getString("value"))
                    .collect(Collectors.toList());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < instanceTypesList.size(); i = i + 10) {
                DescribeInstanceTypesRequest request2 = new DescribeInstanceTypesRequest();
                request2.setRegionId(region);
                request2.setInstanceTypeFamily("ecs.g6");
                List<String> strings = new ArrayList<>();
                if (i + 10 > instanceTypesList.size()) {
                    strings = instanceTypesList.subList(i, instanceTypesList.size());
                } else {
                    strings = instanceTypesList.subList(i, i + 10);
                }

                request2.setInstanceTypess(strings);
                DescribeInstanceTypesResponse response2 = GlobalVarConfig.client.getAcsResponse(request2);
                JSONObject jsonObject2 = JSONObject.parseObject(new Gson().toJson(response2));
                JSONArray instanceTypes = jsonObject2.getJSONArray("instanceTypes");
                jsonArray.addAll(instanceTypes);
            }
            return ResponseResult.succObj(jsonArray);
        } catch (ServerException e) {
            log.error(e.getErrMsg(), e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }
        return ResponseResult.error();
    }

    @Override
    public Instances selectByinstanceId(String server) {
        return instancesMapper.selectByUuid(server);
    }

    @Override
    public int updateByPrimaryKey(Instances instances) {
        return instancesMapper.updateByPrimaryKey(instances);
    }

    @Override
    public ResponseResult instanceList2(InstancesListDTO instancesListDTO) {
        List<Instances> list = instancesMapper.getInstancesList(instancesListDTO);
        return ResponseResult.succObj(list);
    }

    @Override
    public ResponseResult getFlavorById(String flavorId) {
        Flavor recordByUUid = flavorService.getRecordByUUid(flavorId);
        return ResponseResult.succObj(recordByUUid);
    }


}
