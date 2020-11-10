package com.gwm.cloudecs.service.impl;

import com.gwm.cloudecs.dao.VolumeMountMapper;
import com.gwm.cloudecs.model.entity.VolumeMount;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class volumeMountServiceImpl implements VolumeMountService {

    @Autowired
    VolumeMountMapper volumeMountMapper;

    @Autowired
    VolumeService volumeService;


    @Override
    public void insertVolumeMout(String instanceUuid, String volumeUuid, String attachVolumeUuid) {
        VolumeMount volumeMount = new VolumeMount();
        volumeMount.setAttachMode(1);
        volumeMount.setInstanceId(instanceUuid);
        volumeMount.setVolumeId(volumeUuid);
        volumeMount.setUuid(attachVolumeUuid);
        volumeMountMapper.insert(volumeMount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByinstancesUuid(String instanceId) {
        List<VolumeMount> volumeMounts = volumeMountMapper.selectByInstanceId(instanceId);
        volumeMounts.forEach(volumeMount -> {
            deleteByVolumeAndInstances(volumeMount.getInstanceId(), volumeMount.getVolumeId());

        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByVolumeAndInstances(String instancesUuid, String volumeUuid) {
        volumeMountMapper.deleteByVolumeAndInstances(instancesUuid, volumeUuid);
        volumeService.updateVolumeStatus(volumeUuid, "2");
    }

    @Override
    public boolean checkVolumeMount(String volumeId) {
        VolumeMount volumeMount = volumeMountMapper.selectByVolumeId(volumeId);
        if (volumeMount == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public VolumeMount selectByVolumeId(String volumeId) {
       return volumeMountMapper.selectByVolumeId(volumeId);
    }
}
