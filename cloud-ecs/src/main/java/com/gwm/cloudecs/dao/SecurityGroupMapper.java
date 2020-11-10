package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.SecurityGroup;

public interface SecurityGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SecurityGroup record);

    int insertSelective(SecurityGroup record);

    SecurityGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SecurityGroup record);

    int updateByPrimaryKey(SecurityGroup record);
}