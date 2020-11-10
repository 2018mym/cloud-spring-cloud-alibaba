package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.InstanceGroupMember;

public interface InstanceGroupMemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InstanceGroupMember record);

    int insertSelective(InstanceGroupMember record);

    InstanceGroupMember selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InstanceGroupMember record);

    int updateByPrimaryKey(InstanceGroupMember record);
}