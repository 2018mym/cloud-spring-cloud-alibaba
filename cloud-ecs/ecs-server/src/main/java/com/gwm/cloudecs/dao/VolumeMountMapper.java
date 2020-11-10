package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.VolumeMount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolumeMountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VolumeMount record);

    int insertSelective(VolumeMount record);

    VolumeMount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VolumeMount record);

    int updateByPrimaryKey(VolumeMount record);

    void deleteByVolumeAndInstances(@Param("instanceId") String instancesUuid,
                                    @Param("volumeId") String volumeUuid);

    List<VolumeMount> selectByInstanceId(String instanceId);

    VolumeMount selectByVolumeId(String volumeId);

    VolumeMount selectByattId(String attachmentId);
}