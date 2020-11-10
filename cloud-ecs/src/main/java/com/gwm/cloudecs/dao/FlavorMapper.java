package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.Flavor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlavorMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Flavor record);

    int insertSelective(Flavor record);

    Flavor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Flavor record);

    int updateByPrimaryKey(Flavor record);

    Flavor getRecordByUUid(String flavorUuid);

    List<Flavor> getFlavorList(Flavor record);
}