package com.gwm.cloudecs.service.impl;

import cn.hutool.json.JSONUtil;
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
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.common.model.DTO.VolumeDTO;
import com.gwm.cloudecs.common.model.entity.Volume;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.dao.VolumeMapper;
import com.gwm.cloudecs.model.DTO.VolumeDeleteDTO;
import com.gwm.cloudecs.common.model.DTO.VolumeListDTO;
import com.gwm.cloudecs.model.DTO.VolumeRevertDTO;
import com.gwm.cloudecs.model.DTO.VolumeUpdateDTO;
import com.gwm.cloudecs.model.VO.VolumeListVO;
import com.gwm.cloudecs.model.entity.CloudAccount;
import com.gwm.cloudecs.model.entity.Snapshot;
import com.gwm.cloudecs.model.entity.VolumeMount;
import com.gwm.cloudecs.service.SnapshotService;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class VolumeServiceImpl implements VolumeService {

    @Autowired
    VolumeMapper volumeMapper;

    @Autowired
    VolumeMountService volumeMountService;

    @Autowired
    @Lazy
    SnapshotService snapshotService;

    @Autowired
    private QuotaHandle quotaHandle;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertRecord(VolumeDTO volumeDTO) throws IOException, ClientException {
        String volumeServer = "";
        try {
            volumeDTO.setBootable("1");
            // vmware 类型的云主机创建磁盘则挂载
            if ("vmware".equals(volumeDTO.getType())) {
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", volumeDTO.getType(), volumeDTO.getZone(), volumeDTO.getRegion()));
                if (JSONUtil.isNull(volumeDTO.getHint()) || volumeDTO.getHint().getString("local_to_instance") == null) {
                    throw new CommonCustomException(CommonEnum.VOLUMETypeERROR);
                }

                JSONObject jsonObject = new JSONObject() {{
                    put("instance_id", volumeDTO.getHint().getString("local_to_instance"));
                    put("disk_size", volumeDTO.getSize());
                    put("vmware_user", cloudAccount.getUserName());
                    put("vmware_password", cloudAccount.getPassword());
                    put("vmware_host", cloudAccount.getUrl());
                }};
                JSONObject jsonObject1 = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/add-volume"), jsonObject.toJSONString());
                volumeServer = jsonObject1.getString("volume_id");
                volumeDTO.setUuid(volumeServer);
                volumeMountService.insertVolumeMout(volumeDTO.getHint().getString("local_to_instance"), volumeServer, volumeServer);
//                updateVolumeStatus(volumeServer, "7");
            } else if ("openstack".equals(volumeDTO.getType())) {
                // 重新组织参数  命名规范不同需要重新组织参数
                JSONObject volumeParam = new JSONObject();
                volumeParam.put("volume_name", volumeDTO.getName());
                volumeParam.put("size", volumeDTO.getSize());
                volumeParam.put("availability_zone", volumeDTO.getZone());
                volumeParam.put("region_name", volumeDTO.getRegion());
                if ("1".equals(volumeDTO.getFlag())) {
                    volumeParam.put("hint", volumeDTO.getHint());
                    volumeParam.put("snapshot", "");
                } else if ("2".equals(volumeDTO.getFlag())) {
                    volumeParam.put("hint", new JSONObject());
                    volumeParam.put("snapshot", volumeDTO.getSnapshot());
                } else {
                    volumeParam.put("hint", new JSONObject());
                    volumeParam.put("snapshot", "");
                }
                JSONObject volumeData = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/volume/create"), volumeParam.toJSONString());
                volumeServer = volumeData.getString("id");
                volumeDTO.setUuid(volumeServer);
            } else if ("aliyun".equals(volumeDTO.getType())) {
                CreateDiskRequest request = new CreateDiskRequest();
                request.setRegionId(volumeDTO.getRegion());
                request.setDiskName(volumeDTO.getName());
                request.setSize(volumeDTO.getSize());
                request.setDiskCategory(volumeDTO.getVolumeType());
//                if ("1".equals(volumeDTO.getFlag())) {
//                    request.setInstanceId(volumeDTO.getInstanceId());
//                    CreateDiskResponse response = GlobalVarConfig.client.getAcsResponse(request);
//                    JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
//                    String diskId = jsonObject.getString("diskId");
//                    volumeDTO.setUuid(diskId);
//                    volumeMountService.insertVolumeMout(volumeDTO.getInstanceId(), diskId, diskId);
//                } else {
                request.setZoneId(volumeDTO.getZone());
                CreateDiskResponse response = GlobalVarConfig.client.getAcsResponse(request);
                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
                String diskId = jsonObject.getString("diskId");
                volumeDTO.setUuid(diskId);
//                }

            }
            volumeDTO.setStatus("1");
            volumeMapper.insert(volumeDTO);
            return volumeServer;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public void updateVolumeStatus(String uuid, String status) {
        volumeMapper.updateVolumeStatus(uuid, status);
    }

    private String convertStatus(String status) {
        if (status == null) return "0";
        switch (status) {
            case "creating":
                // 阿里云状态
            case "Creating":
                return "1";
            case "available":
                // 阿里云状态
            case "Available":
                return "2";
            case "deleting":
                // 阿里云状态
            case "Deleting":
                return "3";
            case "error":
                return "4";
            case "error_deleting":
                return "5";
            case "attaching":
                // 阿里云状态
            case "Attaching":
                return "6";
            case "in-use":
                // 阿里云状态
            case "In_use":
                return "7";
            case "detaching":
                // 阿里云状态
            case "Detaching":
                return "8";
            case "maintenance":
                return "9";
            case "restoring-backup":
                return "10";
            case "error_restoring":
                return "11";
            case "backing-up":
                return "12";
            case "reverting":
                // 阿里云状态
            case "ReIniting":
                return "13";
        }
        return "0";
    }

    @Override
    public ResponseResult volumeList(VolumeListDTO volumeListDTO) {
        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(volumeListDTO.getPageNum(), volumeListDTO.getPageSize());
            List<VolumeListVO> list = volumeMapper.getVolumeList(volumeListDTO);
            for (VolumeListVO volume : list) {

                String s = "{}";
                if ("openstack".equals(volume.getType())) {
                    s = HttpClient.get(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/volume/status?volume_id=%s&region_name=%s", volume.getUuid(), volume.getRegion())));
                    JSONObject parse = (JSONObject) JSONObject.parse(s);
                    JSONObject data = parse.getJSONObject("data");
                    volume.setStatus(convertStatus(data.getString("status")));
                } else if ("vmware".equals(volume.getType())) {
                    volume.setStatus("7");
                } else if ("aliyun".equals(volume.getType())) {
                    DescribeDisksRequest request = new DescribeDisksRequest();
                    request.setRegionId(volume.getRegion());
                    request.setDiskIds("[\"" + volume.getUuid() + "\"]");
                    DescribeDisksResponse response = GlobalVarConfig.client.getAcsResponse(request);
                    JSONObject responseJson = JSONObject.parseObject(new Gson().toJson(response));
                    JSONObject disks = responseJson.getJSONArray("disks").getJSONObject(0);
                    volume.setStatus(convertStatus(disks.getString("status")));
                }
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
            return ResponseResult.error(e.getMessage());
        }

    }

    @Override
    public ResponseResult volumeInfo(String volumeId, String regionName) {
        try {
            Volume volume = volumeMapper.selectByVolumeId(volumeId);
            if ("openstack".equals(volume.getType())) {
                JSONObject volumeDate = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/volume/detail?volume_id=%s&region_name=%s", volumeId, regionName)));
                JSONArray attachments = volumeDate.getJSONArray("attachments");
                if (!attachments.isEmpty()) {
                    JSONObject jsonObject = attachments.getJSONObject(0);
                    VolumeMount volumeMount = volumeMountService.selectByVolumeId(jsonObject.getString("id"));
                    if (volumeMount == null) {
                        volumeMountService.insertVolumeMout(jsonObject.getString("server_id"), jsonObject.getString("volume_id"), jsonObject.getString("attachment_id"));
                    }
                }
                return ResponseResult.succObj(volumeDate);
            } else if ("vmware".equals(volume.getType())) {
                VolumeMount volumeMount = volumeMountService.selectByVolumeId(volumeId);
                CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", volume.getType(), volume.getZone(), volume.getRegion()));
                JSONObject volumeDate = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/volume/detail?type=vmware&vmware_user=%s&vmware_password=%s&vmware_host=%s&instance_id=%s&volume_id=%s", cloudAccount.getUserName(), cloudAccount.getPassword(), cloudAccount.getUrl(), volumeMount.getInstanceId(), volumeId)));
                return ResponseResult.succObj(volumeDate);
            } else if ("aliyun".equals(volume.getType())) {
                DescribeDisksRequest request = new DescribeDisksRequest();
                request.setRegionId(volume.getRegion());
                request.setDiskIds("[\"" + volume.getUuid() + "\"]");
                DescribeDisksResponse response = GlobalVarConfig.client.getAcsResponse(request);
                return ResponseResult.succObj(response);
            } else {
                return ResponseResult.error("不支持的type类型");
            }


        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public List<Volume> getInstanceVolumeList(String instanceId) {
        return volumeMapper.getInstanceVolumeList(instanceId);


    }

    @Override
    public ResponseResult volumeCreate(VolumeDTO volumeDTO) {
        // 检查配额
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        // 磁盘空间
        hashMap.put("10009", volumeDTO.getSize());
        // 磁盘个数
        hashMap.put("10010", 1);
        // 检查配额
        quotaHandle.putQuotas(volumeDTO.getTicket(), Constant.OCCUPY, hashMap);
        try {

            if ("2".equals(volumeDTO.getFlag())) {
                Snapshot snapshot = snapshotService.selectBySnapshotId(volumeDTO.getSnapshot());
                if (volumeDTO.getSize() < Integer.valueOf(snapshot.getVolumeSize())) {
                    throw new CommonCustomException(CommonEnum.VOLUMESIZEERROR);
                }
            }
            insertRecord(volumeDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new QuotaException(-1, e.getMessage(), Constant.FREE, hashMap);
        }
        return ResponseResult.succ();
    }

    @Override
    public ResponseResult volumeUpdate(VolumeUpdateDTO volumeUpdateDTO) {
        Volume volume = volumeMapper.selectByVolumeId(volumeUpdateDTO.getVolumeId());
        try {
            if ("openstack".equals(volume.getType())) {
                JSONObject jsonObject = new JSONObject() {{
                    put("volume_id", volumeUpdateDTO.getVolumeId());
                    put("region_name", volumeUpdateDTO.getRegion());
                    put("name", volumeUpdateDTO.getName());
                    put("description", volumeUpdateDTO.getDescription());
                }};
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/volume/update"), jsonObject.toJSONString());
            }
            volume.setUpdateAt(new Date());
            volume.setDescription(volumeUpdateDTO.getDescription());
            volume.setName(volumeUpdateDTO.getName());
//            volume.setRegion(volumeUpdateDTO.getRegion());
            volumeMapper.updateByPrimaryKey(volume);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.UPDATESUCCESS);

    }

    @Override
    public ResponseResult volumeDelete(VolumeDeleteDTO volumeDeleteDTO) {
        for (String volumeId : volumeDeleteDTO.getVolumeIds()) {
            try {
                Volume volume = volumeMapper.selectByVolumeId(volumeId);
                // vmware 类型的云主机创建磁盘则挂载
                if ("vmware".equals(volume.getType())) {
                    CloudAccount cloudAccount = GlobalVarConfig.cloudAccountMap.get(String.format("%s-%s-%s", volume.getType(), volume.getZone(), volume.getRegion()));
                    VolumeMount volumeMount = volumeMountService.selectByVolumeId(volumeId);
                    JSONObject jsonObject = new JSONObject() {{
                        put("instance_id", volumeMount.getInstanceId());
                        put("disk_id", volumeId);
                        put("vmware_user", cloudAccount.getUserName());
                        put("vmware_password", cloudAccount.getPassword());
                        put("vmware_host", cloudAccount.getUrl());
                    }};
                    HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/instance/delete-volume"), jsonObject.toJSONString());
                    volumeMountService.deleteByVolumeAndInstances(volumeMount.getInstanceId(), volumeMount.getVolumeId());
                    volumeDelete(volumeId);
                } else if ("openstack".equals(volume.getType())) {
                    boolean flag = volumeMountService.checkVolumeMount(volumeId);
                    if (flag) {
                        throw new CommonCustomException(CommonEnum.DELETEVOLUMEERROR);
                    }
                    JSONObject jsonObject = new JSONObject() {{
                        put("volume_id", volumeId);
                        put("region_name", volumeDeleteDTO.getRegion());
                    }};
                    HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/volume/delete"), jsonObject.toJSONString());
                    volume.setDeleted(1);
                    volume.setDeletedAt(new Date());
                    volumeMapper.updateByPrimaryKey(volume);
                } else if ("aliyun".equals(volume.getType())) {
                    boolean flag = volumeMountService.checkVolumeMount(volumeId);
                    if (flag) {
                        throw new CommonCustomException(CommonEnum.DELETEVOLUMEERROR);
                    }
                    DeleteDiskRequest request = new DeleteDiskRequest();
                    request.setRegionId(volume.getRegion());
                    request.setDiskId(volume.getUuid());
                    GlobalVarConfig.client.getAcsResponse(request);
                    volume.setDeleted(1);
                    volume.setDeletedAt(new Date());
                    volumeMapper.updateByPrimaryKey(volume);
                }
                // 检查配额
                HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
                // 磁盘空间
                hashMap.put("10009", volume.getSize());
                // 磁盘个数
                hashMap.put("10010", 1);
                // 检查配额
                quotaHandle.putQuotas(volumeDeleteDTO.getTicket(), Constant.FREE, hashMap);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ResponseResult.error();
            }
        }

        return ResponseResult.succ(CommonEnum.DELSUCCESS);
    }

    @Override
    public Volume selectByVolumeId(String volumeId) {
        return volumeMapper.selectByVolumeId(volumeId);
    }

    @Override
    public ResponseResult volumeRevert(VolumeRevertDTO volumeRevertDTO) {

        try {
            JSONObject jsonObject = new JSONObject() {{
                put("snapshot_id", volumeRevertDTO.getSnapshotId());
                put("volume_id", volumeRevertDTO.getVolumeId());
                put("region_name", volumeRevertDTO.getRegion());
            }};
            HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/volume/revert"), jsonObject.toJSONString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.REVERTSUCCESS);
    }

    @Override
    public void volumeDelete(String uuid) {
        Volume volume = volumeMapper.selectByVolumeId(uuid);
        volume.setDeleted(1);
        volume.setDeletedAt(new Date());
        volumeMapper.updateByPrimaryKey(volume);
    }

    @Override
    public void updateByPrimaryKey(Volume record) {
        int i = volumeMapper.updateByPrimaryKey(record);
    }

    @Override
    public void insert(Volume record) {
        int i = volumeMapper.insert(record);
    }

    @Override
    public ResponseResult getDiskType(String region, String zone) {
        try {
            DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
            request.setRegionId(region);
            request.setZoneId(zone);
            request.setDestinationResource("DataDisk");
            request.setResourceType("disk");
            if ("cn-hangzhou".equals(region)) {
                DescribeAvailableResourceResponse response = GlobalVarConfig.client.getAcsResponse(request);
                return ResponseResult.succObj(response);
//                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
//                JSONArray availableZones = jsonObject.getJSONArray("availableZones");
//                JSONArray collect = availableZones.stream()
//                        .filter(json -> "cn-hangzhou-h".equals(((JSONObject) json).getString("zoneId")) ||
//                                "cn-hangzhou-i".equals(((JSONObject) json).getString("zoneId"))
//                                || "cn-hangzhou-j".equals(((JSONObject) json).getString("zoneId")))
//                        .collect(Collectors.toCollection(JSONArray::new));
//                jsonObject.put("availableZones", collect);
//                return ResponseResult.succObj(jsonObject);
            } else if ("cn-beijing".equals(region)) {
                DescribeAvailableResourceResponse response = GlobalVarConfig.client.getAcsResponse(request);
                return ResponseResult.succObj(response);
//                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
//                JSONArray availableZones = jsonObject.getJSONArray("availableZones");
//                JSONArray collect = availableZones.stream()
//                        .filter(json -> "cn-beijing-h".equals(((JSONObject) json).getString("zoneId")) ||
//                                "cn-beijing-a".equals(((JSONObject) json).getString("zoneId"))
//                                || "cn-beijing-j".equals(((JSONObject) json).getString("zoneId")))
//                        .collect(Collectors.toCollection(JSONArray::new));
//                jsonObject.put("availableZones", collect);
//                return ResponseResult.succObj(jsonObject);
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
    public ResponseResult volumeList2(VolumeListDTO volumeListDTO) {
        List<Volume> list = volumeMapper.getVolumeListByType(volumeListDTO);
        return ResponseResult.succObj(list);
    }
}
