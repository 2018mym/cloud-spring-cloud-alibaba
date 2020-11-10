package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.InstanceGroupDTO;
import com.gwm.cloudecs.model.entity.InstanceGroup;
import com.gwm.cloudecs.model.entity.KeyPairs;

import java.util.List;

public interface ServerGroupService {

    public ResponseResult getServerGroupList(InstanceGroupDTO instanceGroupDTO);
    //public ResponseResult getOneServerGroup(InstanceGroup instanceGroup);

    public ResponseResult createServerGroup(String ticket,InstanceGroup instanceGroup);

    public ResponseResult delServerGroup(String ticket,List<Integer> ids)  throws Exception;
}
