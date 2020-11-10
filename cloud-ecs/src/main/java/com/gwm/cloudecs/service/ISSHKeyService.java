package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.KeyPairsListDTO;
import com.gwm.cloudecs.model.entity.KeyPairs;

import java.util.List;

public interface ISSHKeyService {

    public ResponseResult getSSHKeyList(KeyPairsListDTO keyPairsListDTO);
    public ResponseResult getOneSSHKey(KeyPairs keyParis);

    public ResponseResult createSshkeyInfo(String ticket,KeyPairs keyParis);

    public ResponseResult delSshkeyInfo(String ticket,List<Integer> ids)  throws Exception;
}
