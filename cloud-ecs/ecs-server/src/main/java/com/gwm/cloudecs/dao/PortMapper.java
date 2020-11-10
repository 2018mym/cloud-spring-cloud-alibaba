package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.Port;

public interface PortMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Port record);

    int insertSelective(Port record);

    Port selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Port record);

    int updateByPrimaryKey(Port record);
}