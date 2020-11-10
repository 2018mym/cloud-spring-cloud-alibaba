package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.SnapshotListDTO;
import com.gwm.cloudecs.model.VO.SnapshotListVO;
import com.gwm.cloudecs.model.entity.Snapshot;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SnapshotMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Snapshot record);

    int insertSelective(Snapshot record);

    Snapshot selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Snapshot record);

    int updateByPrimaryKey(Snapshot record);


    List<SnapshotListVO> snapshotList(SnapshotListDTO snapshotListDTO);

    List<Map<String, String>> getVolumeCount(String volumeId);

    Snapshot selectBySnapshotId(String snapshotId);
}