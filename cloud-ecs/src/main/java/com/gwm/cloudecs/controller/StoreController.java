package com.gwm.cloudecs.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.CheckParamUtil;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.model.entity.Volume;
import com.gwm.cloudecs.service.SnapshotService;
import com.gwm.cloudecs.service.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 存储相关接口
 */
@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Value("${ecs.url.old}")
    private String httpIp;

    @Autowired
    VolumeService volumeService;

    @Autowired
    SnapshotService snapshotService;

    /**
     * 获取所有卷信息
     */
    @GetMapping("/volume/list/v1")
    public Object volumeList() {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/volume/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取所有卷信息
     */
    @GetMapping("/volume/info/v1")
    public Object volumeInfo(@RequestParam("volumeId") String volumeId) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/volume/info?volume_id=" + volumeId));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取所有快照信息
     */
    @GetMapping("/snapshot/list/v1")
    public Object snapshotList() {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/snapshot/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }


    // ----------------------------------   v2

    /**
     * 获取所有卷信息 V2
     */
    @GetMapping("/volume/list/v2")
    public ResponseResult volumeList2(HttpServletRequest request, VolumeListDTO volumeListDTO) {
        CheckParamUtil.checkTypeRegion(volumeListDTO.getType(), volumeListDTO.getRegion());
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        volumeListDTO.setGroupId(groupId);
        volumeListDTO.setUserId(userId);
        return volumeService.volumeList(volumeListDTO);

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
    public ResponseResult volumeCreate(@RequestHeader("ticket") String ticket, HttpServletRequest request, @RequestBody VolumeDTO volumeDTO) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        volumeDTO.setUserId(userId);
        volumeDTO.setTicket(ticket);
        volumeDTO.setProjectId(groupId);
        return volumeService.volumeCreate(volumeDTO);
    }

    /**
     * 更新卷V2
     */
    @PostMapping("/volume/update/v2")
    public ResponseResult volumeUpdate(HttpServletRequest request, @RequestBody VolumeUpdateDTO volumeUpdateDTO) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        volumeUpdateDTO.setUserId(userId);
        volumeUpdateDTO.setGroupId(groupId);
        return volumeService.volumeUpdate(volumeUpdateDTO);

    }

    /**
     * 删除卷V2
     */
    @PostMapping("/volume/delete/v2")
    public ResponseResult volumeDelete(@RequestHeader("ticket") String ticket, HttpServletRequest request, @RequestBody VolumeDeleteDTO volumeDeleteDTO) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
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
    public ResponseResult snapshotList(HttpServletRequest request, SnapshotListDTO snapshotListDTO) {
        CheckParamUtil.checkTypeRegion(snapshotListDTO.getType(), snapshotListDTO.getRegion());
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
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
    public ResponseResult snapshotCreate(HttpServletRequest request, @RequestBody SnapshotDTO snapshotDTO) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        snapshotDTO.setUserId(userId);
        snapshotDTO.setProjectId(groupId);
        return snapshotService.snapshotCreate(snapshotDTO);

    }

    /**
     * 更新快照V2
     */
    @PostMapping("/snapshot/update/v2")
    public ResponseResult snapshotUpdate(HttpServletRequest request, @RequestBody SnapshotUpdateDTO snapshotUpdateDTO) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        snapshotUpdateDTO.setUserId(userId);
        snapshotUpdateDTO.setGroupId(groupId);
        return snapshotService.snapshotUpdate(snapshotUpdateDTO);

    }

    /**
     * 删除 快照V2
     */
    @PostMapping("/snapshot/delete/v2")
    public ResponseResult snapshotDelete(HttpServletRequest request, @RequestBody SnapshotDeleteDTO snapshotDeleteDTO) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        snapshotDeleteDTO.setUserId(userId);
        snapshotDeleteDTO.setGroupId(groupId);
        return snapshotService.snapshotDelete(snapshotDeleteDTO);

    }
}
