package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.SnapshotDTO;
import com.gwm.cloudecs.model.DTO.SnapshotDeleteDTO;
import com.gwm.cloudecs.model.DTO.SnapshotListDTO;
import com.gwm.cloudecs.model.DTO.SnapshotUpdateDTO;
import com.gwm.cloudecs.model.entity.Snapshot;

public interface SnapshotService {
    /**
     * 查询快照列表
     * @param snapshotListDTO
     * @return
     */
    ResponseResult snapshotList(SnapshotListDTO snapshotListDTO);

    /**
     * 获取快照详情
     * @param snapshotId
     * @param regionName
     * @return
     */
    ResponseResult snapshotInfo(String snapshotId, String regionName);

    /**
     * 创建快照
     * @param snapshotDTO
     * @return
     */
    ResponseResult snapshotCreate(SnapshotDTO snapshotDTO);

    /**
     * 快照的更新
     * @param snapshotUpdateDTO
     * @return
     */
    ResponseResult snapshotUpdate(SnapshotUpdateDTO snapshotUpdateDTO);

    /**
     * 快照的删除
     * @param snapshotDeleteDTO
     * @return
     */
    ResponseResult snapshotDelete(SnapshotDeleteDTO snapshotDeleteDTO);

    Snapshot selectBySnapshotId(String snapshotId);

    /**
     * 通过volumeId查询快照列表
     * @param volumeId
     * @return
     */
    ResponseResult volumeSnapshot(String volumeId);
}
