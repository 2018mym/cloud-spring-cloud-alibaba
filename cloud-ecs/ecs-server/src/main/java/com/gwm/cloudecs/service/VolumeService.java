package com.gwm.cloudecs.service;

import com.aliyuncs.exceptions.ClientException;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.common.model.DTO.VolumeDTO;
import com.gwm.cloudecs.common.model.DTO.VolumeListDTO;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.common.model.entity.Volume;

import java.io.IOException;
import java.util.List;

public interface VolumeService {

    /**
     * 插入一条新的数据
     * @param volume
     * @return
     * @throws IOException
     */
    String insertRecord(VolumeDTO volume) throws IOException, ClientException;

    /**
     * 通过volumeUuid更新状态
     * @param uuid
     * @param status
     */
    void updateVolumeStatus(String uuid, String status);

    /**
     * 查询volumelist
     * @param volumeListDTO
     * @return
     */
    ResponseResult volumeList(VolumeListDTO volumeListDTO);

    /**
     * volumeInfo2
     * @param volumeId
     * @param regionName
     * @return
     */
    ResponseResult volumeInfo(String volumeId, String regionName);

    /**
     * 通过云主机id查询挂载的卷
     * @param instanceId
     * @return
     */
    List<Volume> getInstanceVolumeList(String instanceId);

    /**
     * 卷的创建
     * @param volumeDTO
     * @return
     */
    ResponseResult volumeCreate(VolumeDTO volumeDTO);

    /**
     * 卷的更新
     * @param volumeUpdateDTO
     * @return
     */
    ResponseResult volumeUpdate(VolumeUpdateDTO volumeUpdateDTO);

    /**
     * 删除卷
     * @param volumeDeleteDTO
     * @return
     */
    ResponseResult volumeDelete(VolumeDeleteDTO volumeDeleteDTO);

    /**
     * 通过uuid查询卷
     * @param volumeId
     * @return
     */
    Volume selectByVolumeId(String volumeId);

    /**
     * 卷回滚
     * @param volumeRevertDTO
     * @return
     */
    ResponseResult volumeRevert(VolumeRevertDTO volumeRevertDTO);


    /**
     * 通过uuid删除该磁盘
     * @param uuid
     */
    void volumeDelete(String uuid);

    void updateByPrimaryKey(Volume record);

    void insert(Volume record);

    ResponseResult getDiskType(String region, String zone);

    ResponseResult volumeList2(VolumeListDTO volumeListDTO);
}
