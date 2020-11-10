package com.gwm.clouduser.dao;


import com.gwm.clouduser.model.DTO.OrderListDTO;
import com.gwm.clouduser.model.entity.CloudOrder;
import com.gwm.clouduser.model.entity.CloudOrderExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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

    List<CloudOrder> getOrderList(OrderListDTO record);


}