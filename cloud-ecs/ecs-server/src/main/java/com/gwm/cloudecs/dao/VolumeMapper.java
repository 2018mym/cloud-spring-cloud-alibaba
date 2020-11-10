package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;
import com.gwm.cloudecs.common.model.DTO.VolumeListDTO;
import com.gwm.cloudecs.model.VO.VolumeListVO;
import com.gwm.cloudecs.model.VO.VolumeTotalListVO;
import com.gwm.cloudecs.common.model.entity.Volume;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolumeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Volume record);

    Volume selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Volume record);

    void updateVolumeStatus(@Param("uuid") String uuid,
                            @Param("status") String status);

    List<Volume> selectByInstanceId(String instanceId);

    List<VolumeListVO> getVolumeList(VolumeListDTO volumeListDTO);

    List<Volume> getInstanceVolumeList(String instanceId);

    Volume selectByVolumeId(String volumeId);

    List<VolumeTotalListVO> totalVolumeByProjectId(ResourceTotalListDTO dto);

    /**
     * 通过type 获取volumes
     * @param volumeListDTO
     * @return
     */
    List<Volume> getVolumeListByType(VolumeListDTO volumeListDTO);
}