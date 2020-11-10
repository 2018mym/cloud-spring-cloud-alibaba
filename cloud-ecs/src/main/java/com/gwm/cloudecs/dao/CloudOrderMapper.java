package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.CloudOrder;
import com.gwm.cloudecs.model.entity.CloudOrderExample;
import org.apache.ibatis.annotations.Param;

public interface CloudOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CloudOrder record);

    int insertSelective(CloudOrder record);

    CloudOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CloudOrder record, @Param("example") CloudOrderExample example);

    int updateByExampleWithBLOBs(@Param("record") CloudOrder record, @Param("example") CloudOrderExample example);

    int updateByExample(@Param("record") CloudOrder record, @Param("example") CloudOrderExample example);

    int updateByPrimaryKeySelective(CloudOrder record);

    int updateByPrimaryKeyWithBLOBs(CloudOrder record);

    int updateByPrimaryKey(CloudOrder record);
}