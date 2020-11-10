package com.gwm.cloudecs.service.impl;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.dao.CloudAccountMapper;
import com.gwm.cloudecs.model.entity.CloudAccount;
import com.gwm.cloudecs.service.CloudAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudAccountServiceImpl implements CloudAccountService {

    @Autowired
    CloudAccountMapper cloudAccountMapper;

    @Override
    public CloudAccount getRecord(String type, String zone, String region) {
        return  cloudAccountMapper.getRecord(type, zone, region);
    }

    @Override
    public List<CloudAccount> getRecordListByType(String type, String region) {
        return  cloudAccountMapper.getRecordListByType(type, region);

    }

    @Override
    public ResponseResult getZone(String type, String region) {
        List<CloudAccount> recordListByType = cloudAccountMapper.getRecordListByType(type, region);
        return ResponseResult.succObj(recordListByType.stream().map(CloudAccount::getZone).collect(Collectors.toList()));
    }
}
