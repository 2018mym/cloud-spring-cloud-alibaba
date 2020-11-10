package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.ObsAccountListDTO;
import com.gwm.cloudecs.model.entity.ObsAccount;

import java.util.List;

public interface ObsAccountService {
    public ResponseResult getAccountList(ObsAccountListDTO accountListDTO);

    public ResponseResult addAccountInfo(ObsAccount account) ;

    public ResponseResult updateCloudAccountInfo(ObsAccount account) ;

    public ResponseResult updateCloudAccountQuota(ObsAccount account) ;

    public ResponseResult delInfo(List<Integer> ids) throws Exception;

    public ResponseResult getBucket(ObsAccount account);



}
