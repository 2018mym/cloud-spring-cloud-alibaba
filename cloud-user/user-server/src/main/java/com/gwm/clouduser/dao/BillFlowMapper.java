package com.gwm.clouduser.dao;

import com.gwm.clouduser.model.entity.BillFlow;

public interface BillFlowMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BillFlow record);

    int insertSelective(BillFlow record);

    BillFlow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BillFlow record);

    int updateByPrimaryKey(BillFlow record);
}