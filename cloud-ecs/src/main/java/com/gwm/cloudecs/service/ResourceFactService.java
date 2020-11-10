package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.InstanceGroupDTO;
import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;

public interface ResourceFactService {

    public ResponseResult totalResourceList(ResourceTotalListDTO resDTO);
}
