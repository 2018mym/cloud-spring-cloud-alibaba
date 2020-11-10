package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.CloudAccount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudAccountMapper {
    int deleteByPrimaryKey(@Param("id") Integer id, @Param("uuid") String uuid);

    int insert(CloudAccount record);

    int insertSelective(CloudAccount record);

    CloudAccount selectByPrimaryKey(@Param("id") Integer id, @Param("uuid") String uuid);

    int updateByPrimaryKeySelective(CloudAccount record);

    int updateByPrimaryKey(CloudAccount record);

    CloudAccount getRecord(@Param("type") String type,
                           @Param("zone") String zone,
                           @Param("region") String region);

    List<CloudAccount> getRecordListByType(@Param("type") String type, @Param("region") String region);
}