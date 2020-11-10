package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.InstanceGroupDTO;
import com.gwm.cloudecs.model.entity.InstanceGroup;

import java.util.List;

public interface InstanceGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InstanceGroup record);

    int insertSelective(InstanceGroup record);

    InstanceGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InstanceGroup record);

    int updateByPrimaryKey(InstanceGroup record);

    List<InstanceGroup> getInstanceGroupList(InstanceGroupDTO instanceGroupDTO);
}