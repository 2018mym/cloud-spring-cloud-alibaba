package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.common.model.entity.Volume;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.dao.SnapshotMapper;
import com.gwm.cloudecs.model.DTO.SnapshotDTO;
import com.gwm.cloudecs.model.DTO.SnapshotDeleteDTO;
import com.gwm.cloudecs.model.DTO.SnapshotListDTO;
import com.gwm.cloudecs.model.DTO.SnapshotUpdateDTO;
import com.gwm.cloudecs.model.VO.SnapshotListVO;
import com.gwm.cloudecs.model.entity.Snapshot;
import com.gwm.cloudecs.service.SnapshotService;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SnapshotServiceImpl implements SnapshotService {

    @Autowired
    SnapshotMapper snapshotMapper;

    @Autowired
    VolumeMountService volumeMountService;

    @Autowired
    @Lazy
    VolumeService volumeService;

    @Override
    public ResponseResult snapshotList(SnapshotListDTO snapshotListDTO) {

        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(snapshotListDTO.getPageNum(), snapshotListDTO.getPageSize());
            List<SnapshotListVO> list = snapshotMapper.snapshotList(snapshotListDTO);
            for (SnapshotListVO snapshot : list) {
                String s = HttpClient.get(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/snapshot/status?snapshot_id=%s&region_name=%s", snapshot.getUuid(), snapshot.getRegion())));
                JSONObject parse = (JSONObject) JSONObject.parse(s);
                JSONObject data = parse.getJSONObject("data");
                snapshot.setStatus(convertStatus(data.getString("status")));
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

    @Override
    public ResponseResult snapshotInfo(String snapshotId, String regionName) {
        JSONObject snapshotDate = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/snapshot/detail?snapshot_id=%s&region_name=%s", snapshotId, regionName)));
        return ResponseResult.succObj(snapshotDate);
    }

    @Override
    public ResponseResult snapshotCreate(SnapshotDTO snapshotDTO) {
        try {
            List<Map<String, String>> volumeCount = snapshotMapper.getVolumeCount(snapshotDTO.getVolumeId());
            if (volumeCount.size() > 2) {
                throw new CommonCustomException(CommonEnum.SNAPSHOTTOTALERROR);
            }
            boolean flag = volumeMountService.checkVolumeMount(snapshotDTO.getVolumeId());
            Volume volume = volumeService.selectByVolumeId(snapshotDTO.getVolumeId());
            snapshotDTO.setEnv(volume.getEnv());
            snapshotDTO.setRegion(volume.getRegion());
            snapshotDTO.setZone(volume.getZone());
            snapshotDTO.setVolumeSize(volume.getSize().toString());
            snapshotDTO.setType(volume.getType());

            JSONObject jsonObject = new JSONObject() {{
                put("volume_id", snapshotDTO.getVolumeId());
                put("region_name", snapshotDTO.getRegion());
                put("snap_name", snapshotDTO.getName());
                put("force", flag);
            }};
            JSONObject jsonObject1 = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/snapshot/create"), jsonObject.toJSONString());
            String snapshotId = jsonObject1.getString("id");
            snapshotDTO.setUuid(snapshotId);
            snapshotDTO.setStatus("1");
            snapshotMapper.insert(snapshotDTO);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ(CommonEnum.CREATESUCCESS);
    }

    @Override
    public ResponseResult snapshotUpdate(SnapshotUpdateDTO snapshotUpdateDTO) {
        try {
            Snapshot snapshot = snapshotMapper.selectBySnapshotId(snapshotUpdateDTO.getSnapshotId());
            JSONObject jsonObject = new JSONObject() {{
                put("snapshot_id", snapshotUpdateDTO.getSnapshotId());
                put("region_name", snapshotUpdateDTO.getRegion());
                put("name", snapshotUpdateDTO.getName());
                put("description", snapshotUpdateDTO.getDescription());
            }};
            HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/snapshot/update"), jsonObject.toJSONString());
            snapshot.setUpdateAt(new Date());
            snapshot.setDescription(snapshotUpdateDTO.getDescription());
            snapshot.setName(snapshotUpdateDTO.getName());
            snapshot.setRegion(snapshotUpdateDTO.getRegion());
            snapshot.setUserId(snapshotUpdateDTO.getUserId());
            snapshot.setProjectId(snapshotUpdateDTO.getGroupId());
            snapshotMapper.updateByPrimaryKey(snapshot);
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
    public ResponseResult snapshotDelete(SnapshotDeleteDTO snapshotDeleteDTO) {
        for (String snapshotId : snapshotDeleteDTO.getSnapshotIds()) {
            try {
                Snapshot snapshot = snapshotMapper.selectBySnapshotId(snapshotId);
                JSONObject jsonObject = new JSONObject() {{
                    put("snapshot_id", snapshotId);
                    put("region_name", snapshotDeleteDTO.getRegion());
                }};
                HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/snapshot/delete"), jsonObject.toJSONString());
                snapshot.setDeleted(1);
                snapshot.setDeletedAt(new Date());
                snapshotMapper.updateByPrimaryKey(snapshot);
            } catch (CommonCustomException ce) {
                log.error(ce.getMessage(), ce);
                return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return ResponseResult.error();
            }
        }

        return ResponseResult.succ(CommonEnum.DELSUCCESS);
    }

    private String convertStatus(String status) {
        if (status == null) return "0";
        switch (status) {
            case "creating":
                return "1";
            case "available":
                return "2";
            case "backing-up":
                return "3";
            case "deleting":
                return "4";
            case "error":
                return "5";
            case "deleted":
                return "6";
            case "restoring":
                return "7";
            case "error_deleting":
                return "8";
        }
        return "0";
    }

    @Override
    public Snapshot selectBySnapshotId(String snapshotId) {
        return snapshotMapper.selectBySnapshotId(snapshotId);
    }

    @Override
    public ResponseResult volumeSnapshot(String volumeId) {
        List<Map<String, String>> volumeCount = snapshotMapper.getVolumeCount(volumeId);
        return ResponseResult.succObj(volumeCount);
    }
}