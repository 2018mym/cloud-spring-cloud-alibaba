package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.CloudRsas;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudRsasMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CloudRsas record);

    int insertSelective(CloudRsas record);

    CloudRsas selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CloudRsas record);

    int updateByPrimaryKey(CloudRsas record);

    CloudRsas selectByInstanceId(String instanceId);
}