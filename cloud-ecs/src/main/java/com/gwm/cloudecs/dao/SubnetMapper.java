package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.Subnet;

public interface SubnetMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Subnet record);

    int insertSelective(Subnet record);

    Subnet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Subnet record);

    int updateByPrimaryKey(Subnet record);
}