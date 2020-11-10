package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.Network;

public interface NetworkMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Network record);

    int insertSelective(Network record);

    Network selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Network record);

    int updateByPrimaryKey(Network record);
}