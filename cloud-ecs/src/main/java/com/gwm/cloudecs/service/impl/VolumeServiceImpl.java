package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONArray;
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
import com.gwm.cloudecs.dao.VolumeMapper;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.model.VO.VolumeListVO;
import com.gwm.cloudecs.model.entity.Snapshot;
import com.gwm.cloudecs.model.entity.Volume;
import com.gwm.cloudecs.model.entity.VolumeMount;
import com.gwm.cloudecs.service.SnapshotService;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class VolumeServiceImpl implements VolumeService {

    @Value("${ecs.url}")
    private String url;

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
    public String insertRecord(VolumeDTO volumeDTO) throws IOException {
        try {
            // 重新组织参数  命名规范不同需要重新组织参数
            JSONObject volumeParam = new JSONObject();
            volumeDTO.setBootable("1");
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
            JSONObject volumeData = HttpClient.postEcs(String.format(url, "/bcp/v2/volume/create"), volumeParam.toJSONString());
            String volumeServer = volumeData.getString("id");
            volumeDTO.setUuid(volumeServer);
            volumeDTO.setStatus("1");
            volumeMapper.insert(volumeDTO);
            return volumeServer;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
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
                return "1";
            case "available":
                return "2";
            case "deleting":
                return "3";
            case "error":
                return "4";
            case "error_deleting":
                return "5";
            case "attaching":
                return "6";
            case "in-use":
                return "7";
            case "detaching":
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
                String escData = HttpClient.get(String.format(url, String.format("/bcp/v2/volume/status?volume_id=%s&region_name=%s", volume.getUuid(), volume.getRegion())));
                JSONObject parse = (JSONObject) JSONObject.parse(escData);
                JSONObject data = parse.getJSONObject("data");
                volume.setStatus(convertStatus(data.getString("status")));
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

    @Override
    public ResponseResult volumeInfo(String volumeId, String regionName) {
        try {
            JSONObject volumeDate = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/volume/detail?volume_id=%s&region_name=%s", volumeId, regionName)));
            JSONArray attachments = volumeDate.getJSONArray("attachments");
            if (!attachments.isEmpty()) {
                JSONObject jsonObject = attachments.getJSONObject(0);
                VolumeMount volumeMount = volumeMountService.selectByVolumeId(jsonObject.getString("id"));
                if (volumeMount == null) {
                    volumeMountService.insertVolumeMout(jsonObject.getString("server_id"), jsonObject.getString("volume_id"), jsonObject.getString("attachment_id"));
                }
            }
            return ResponseResult.succObj(volumeDate);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
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
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            throw new QuotaException(-1, e.getMessage(), Constant.FREE, hashMap);
        }
        return ResponseResult.succ();
    }

    @Override
    public ResponseResult volumeUpdate(VolumeUpdateDTO volumeUpdateDTO) {
        Volume volume = volumeMapper.selectByVolumeId(volumeUpdateDTO.getVolumeId());
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("volume_id", volumeUpdateDTO.getVolumeId());
                put("region_name", volumeUpdateDTO.getRegion());
                put("name", volumeUpdateDTO.getName());
                put("description", volumeUpdateDTO.getDescription());
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/volume/update"), jsonObject.toJSONString());
            volume.setUpdateAt(new Date());
            volume.setDescription(volumeUpdateDTO.getDescription());
            volume.setName(volumeUpdateDTO.getName());
            volume.setRegion(volumeUpdateDTO.getRegion());
            volumeMapper.updateByPrimaryKey(volume);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.UPDATESUCCESS);

    }

    @Override
    public ResponseResult volumeDelete(VolumeDeleteDTO volumeDeleteDTO) {
        try {
            boolean flag = volumeMountService.checkVolumeMount(volumeDeleteDTO.getVolumeId());
            if (flag) {
                throw new CommonCustomException(CommonEnum.DELETEVOLUMEERROR);
            }
            Volume volume = volumeMapper.selectByVolumeId(volumeDeleteDTO.getVolumeId());
            JSONObject jsonObject = new JSONObject() {{
                put("volume_id", volumeDeleteDTO.getVolumeId());
                put("region_name", volumeDeleteDTO.getRegion());
            }};
            HttpClient.postEcs(String.format(url, "/bcp/v2/volume/delete"), jsonObject.toJSONString());
            volume.setDeleted(1);
            volume.setDeletedAt(new Date());
            volumeMapper.updateByPrimaryKey(volume);
            // 检查配额
            HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
            // 磁盘空间
            hashMap.put("10009", volume.getSize());
            // 磁盘个数
            hashMap.put("10010", 1);
            // 检查配额
            quotaHandle.putQuotas(volumeDeleteDTO.getTicket(), Constant.FREE, hashMap);
        } catch (CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
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
            HttpClient.postEcs(String.format(url, "/bcp/v2/volume/revert"), jsonObject.toJSONString());
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.REVERTSUCCESS);
    }

}
