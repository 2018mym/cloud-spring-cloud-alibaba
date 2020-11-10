package com.gwm.cloudecs.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.CheckParamUtil;
import com.gwm.cloudecs.common.model.DTO.VolumeDTO;
import com.gwm.cloudecs.common.model.DTO.VolumeListDTO;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.service.SnapshotService;
import com.gwm.cloudecs.service.VolumeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 存储相关接口
 */
@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    VolumeService volumeService;

    @Autowired
    SnapshotService snapshotService;

    // ----------------------------------   v2

    /**
     * 获取所有卷信息 V2
     */
    @GetMapping("/volume/list/v2")
    public ResponseResult volumeList2(@RequestHeader("groupId") String groupId, @RequestHeader("userId") String userId, VolumeListDTO volumeListDTO) {
        CheckParamUtil.checkTypeRegion(volumeListDTO.getType(), volumeListDTO.getRegion());
        volumeListDTO.setGroupId(groupId);
        volumeListDTO.setUserId(userId);
        return volumeService.volumeList(volumeListDTO);

    }

    /**
     * 获取所有卷信息 V2
     */
    @PostMapping("/volume/list/v2")
    public ResponseResult volumeList2(@RequestBody VolumeListDTO volumeListDTO) {
        return volumeService.volumeList2(volumeListDTO);

    }

    /**
     * 获取单个卷信息V2
     */
    @GetMapping("/volume/info/v2")
    public ResponseResult volumeInfo2(HttpServletRequest request) {
        String volumeId = request.getParameter("volumeId");
        String regionName = request.getParameter("region");
        return volumeService.volumeInfo(volumeId, regionName);

    }

    /**
     * 创建卷V2
     */
    @PostMapping("/volume/create/v2")
    public ResponseResult volumeCreate(@RequestHeader("ticket") String ticket,
                                       @RequestHeader("groupId") String groupId,
                                       @RequestHeader("userId") String userId,
                                       @RequestBody VolumeDTO volumeDTO) {
        volumeDTO.setUserId(userId);
        volumeDTO.setTicket(ticket);
        volumeDTO.setProjectId(groupId);
        return volumeService.volumeCreate(volumeDTO);
    }

    /**
     * 更新卷V2
     */
    @PostMapping("/volume/update/v2")
    public ResponseResult volumeUpdate(@RequestHeader("groupId") String groupId,
                                       @RequestHeader("userId") String userId,
                                       @RequestBody VolumeUpdateDTO volumeUpdateDTO) {
        volumeUpdateDTO.setUserId(userId);
        volumeUpdateDTO.setGroupId(groupId);
        return volumeService.volumeUpdate(volumeUpdateDTO);

    }

    /**
     * 删除卷V2
     */
    @PostMapping("/volume/delete/v2")
    public ResponseResult volumeDelete(@RequestHeader("ticket") String ticket,
                                       @RequestHeader("groupId") String groupId,
                                       @RequestHeader("userId") String userId,
                                       @RequestBody VolumeDeleteDTO volumeDeleteDTO) {
        volumeDeleteDTO.setUserId(userId);
        volumeDeleteDTO.setGroupId(groupId);
        volumeDeleteDTO.setTicket(ticket);
        return volumeService.volumeDelete(volumeDeleteDTO);

    }

    /**
     * 回滚卷V2
     */
    @PostMapping("/volume/revert/v2")
    public ResponseResult volumeRevert(@RequestBody VolumeRevertDTO volumeRevertDTO) {
        return volumeService.volumeRevert(volumeRevertDTO);
    }

    /**
     * 卷下的快照V2
     */
    @GetMapping("/volume/snpashot/v2")
    public ResponseResult volumeSnapshot(@RequestParam("volumeId") String volumeId) {
        return snapshotService.volumeSnapshot(volumeId);

    }


    /**
     * 获取所有快照信息 V2
     */
    @GetMapping("/snapshot/list/v2")
    public ResponseResult snapshotList(@RequestHeader("groupId") String groupId,
                                       @RequestHeader("userId") String userId,
                                       SnapshotListDTO snapshotListDTO) {
        CheckParamUtil.checkTypeRegion(snapshotListDTO.getType(), snapshotListDTO.getRegion());
        snapshotListDTO.setGroupId(groupId);
        snapshotListDTO.setUserId(userId);
        return snapshotService.snapshotList(snapshotListDTO);
    }

    /**
     * 获取单个快照信息V2
     */
    @GetMapping("/snapshot/info/v2")
    public ResponseResult snapshotInfo2(HttpServletRequest request) {
        String snapshotId = request.getParameter("snapshotId");
        String regionName = request.getParameter("region");
        return snapshotService.snapshotInfo(snapshotId, regionName);

    }

    /**
     * 创建快照 V2
     */
    @PostMapping("/snapshot/create/v2")
    public ResponseResult snapshotCreate(@RequestHeader("groupId") String groupId,
                                         @RequestHeader("userId") String userId,
                                         @RequestBody SnapshotDTO snapshotDTO) {
        snapshotDTO.setUserId(userId);
        snapshotDTO.setProjectId(groupId);
        return snapshotService.snapshotCreate(snapshotDTO);

    }

    /**
     * 更新快照V2
     */
    @PostMapping("/snapshot/update/v2")
    public ResponseResult snapshotUpdate(@RequestHeader("groupId") String groupId,
                                         @RequestHeader("userId") String userId,
                                         @RequestBody SnapshotUpdateDTO snapshotUpdateDTO) {
        snapshotUpdateDTO.setUserId(userId);
        snapshotUpdateDTO.setGroupId(groupId);
        return snapshotService.snapshotUpdate(snapshotUpdateDTO);

    }

    /**
     * 删除 快照V2
     */
    @PostMapping("/snapshot/delete/v2")
    public ResponseResult snapshotDelete(@RequestHeader("groupId") String groupId,
                                         @RequestHeader("userId") String userId,
                                         @RequestBody SnapshotDeleteDTO snapshotDeleteDTO) {
        snapshotDeleteDTO.setUserId(userId);
        snapshotDeleteDTO.setGroupId(groupId);
        return snapshotService.snapshotDelete(snapshotDeleteDTO);

    }

    /**
     * 获取该区域下的可用磁盘类型
     */
    @GetMapping("/get/disk/type")
    @ApiOperation("获取该区域下的可用磁盘类型")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "zone", value = "zone", required = true)
    })
    public ResponseResult getDiskType(@RequestParam(value = "region") String region,
                                      @RequestParam(value = "zone") String zone) {
        return volumeService.getDiskType(region, zone);
    }
}
