package com.gwm.cloudecs.controller;


import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.CheckParamUtil;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.model.DTO.*;
import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.service.EcsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/instance")
public class ECSController {

    @Value("${ecs.url}")
    private String url;

    @Autowired
    EcsService ecsService;


    /**
     * 获取VM概要
     */
    @GetMapping("/list/v1")
    public Object instanceList() {
        try {
            String s = HttpClient.get(String.format(url, "/bcp/v1/instance/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 启动虚拟机
     */
    @PostMapping("/start/v1")
    public Object instanceStart(@RequestBody JSONObject jsonObject) {
        try {
            String s = HttpClient.post(String.format(url, "/bcp/v1/instance/start"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 关闭虚拟机
     */
    @PostMapping("/stop/v1")
    public Object instanceStop(@RequestBody JSONObject jsonObject) {
        try {
            String s = HttpClient.post(String.format(url, "/bcp/v1/instance/stop"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取vnc链接
     */
    @GetMapping("/vnc/v1")
    public Object instanceVnc(@RequestParam("vmid") String vmid) {
        try {
            String s = HttpClient.get(String.format(url, "/bcp/v1/instance/vnc/" + vmid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 关闭虚拟机
     */
    @PostMapping("/restart/v1")
    public Object instanceRestart(@RequestBody JSONObject jsonObject) {
        try {
            String s = HttpClient.post(String.format(url, "/bcp/v1/instance/restart"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取镜像列表
     */
    @GetMapping("/image/list/v1")
    public Object instanceImageList() {
        try {
            String s = HttpClient.get(String.format("http://10.255.128.100:8080:%s", "/bcp/v1/image/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 查看虚拟机状态
     */
    @GetMapping("/status/v1")
    public Object instanceStatus(@RequestParam("uuid") String uuid) {
        try {
            String s = HttpClient.get(String.format(url, "/bcp/v1/instance/status/" + uuid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取虚拟机详情
     */
    @GetMapping("/details/v1")
    public Object instanceDetails(@RequestParam("uuid") String uuid) {
        try {
            String s = HttpClient.get(String.format(url, "/bcp/v1/instance/details/" + uuid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取云主机类型接口
     */
    @GetMapping("/flavor/list/v1")
    public Object flavorList() {
        try {
            String s = HttpClient.get(String.format("http://10.255.128.100:8080:%s", "/bcp/v1/instance/flavor-list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取云主机类型接口v2
     */
    @GetMapping("/flavor/list/v2")
    public Object flavorList2(String region) {
            /*
            String s = HttpClient.get(String.format("http://10.255.128.191:8080:%s", "/bcp/v2/instance/flavor-list?region_name="+region_name));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));
            */
        Flavor flavor = new Flavor();
        flavor.setRegion(region);
        return ecsService.getFlavorList(flavor);

    }


    /**
     * 删除虚拟机
     */
    @PostMapping("/create/v1")
    public Object createInfo(@RequestBody JSONObject jsonObject) {
        try {
            String s = HttpClient.post(String.format(url, "/bcp/v1/instance/create"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 删除虚拟机
     */
    @PostMapping("/delete/v1")
    public Object delteInfo(@RequestBody JSONObject jsonObject) {
        try {
            String s = HttpClient.post(String.format(url, "/bcp/v1/instance/delete"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }


    //-----------------------------------V2

    /**
     * 获取云主机list
     */
    @GetMapping("/list/v2")
    public ResponseResult instanceList(HttpServletRequest request, InstancesListDTO instancesListDTO) {
        CheckParamUtil.checkTypeRegion(instancesListDTO.getType(), instancesListDTO.getRegion());
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        instancesListDTO.setGroupId(groupId);
        instancesListDTO.setUserId(userId);
        return ecsService.instanceList(instancesListDTO);

    }

    /**
     * 获取云主机状态v2接口
     */
    @PostMapping("/check/status/v2")
    public ResponseResult instanceStatus(@RequestBody List<InstancesStatusDTO> instancesStatusList) {
        return ecsService.instanceStatus(instancesStatusList);

    }

    /**
     * 创建云主机接口v2版本
     */
    @PostMapping("/create/v2")
    public ResponseResult createInfo(@RequestHeader("ticket") String ticket,
                                     HttpServletRequest request,
                                     @RequestBody InstancesDTO instancesDTO) throws IOException {
        String userId = request.getParameter("userId");
        String groupId = request.getParameter("groupId");
        instancesDTO.setTicket(ticket);
        return ecsService.createInstances(instancesDTO, userId, groupId);
    }

    /**
     * 启动虚拟机V2版本
     */
    @PostMapping("/rebuild/v2")
    public ResponseResult instanceRebuild2(HttpServletRequest request, @RequestBody InstancesDTO instancesDTO) {
        String userId = request.getParameter("userId");
        String groupId = request.getParameter("groupId");
        return ecsService.instanceRebuild(instancesDTO, userId, groupId);
    }

    /**
     * 更新云主机 v2版本
     */
    @PostMapping("/udpate/v2")
    public ResponseResult udpateInfo(HttpServletRequest request, @RequestBody InstancesUpdateDTO instancesUpdateDTO) {
        String userId = request.getParameter("userId");
        String groupId = request.getParameter("groupId");
        return ecsService.udpateInfo(instancesUpdateDTO, userId, groupId);

    }

    /**
     * 启动虚拟机V2版本
     */
    @PostMapping("/start/v2")
    public ResponseResult instanceStart2(@RequestBody JSONObject jsonObject) {
        return ecsService.instanceStart(jsonObject.getString("instanceId"), jsonObject.getString("regionName"));
    }

    /**
     * 关闭虚拟机V2版本
     */
    @PostMapping("/stop/v2")
    public ResponseResult instanceStop2(@RequestBody JSONObject jsonObject) {
        return ecsService.instanceStop(jsonObject.getString("instanceId"), jsonObject.getString("regionName"));
    }

    /**
     * 删除虚拟机V2版本
     */
    @PostMapping("/delete/v2")
    public ResponseResult instanceDelete2(@RequestHeader("ticket") String ticket, HttpServletRequest request, @RequestBody InstancesDeleteDTO instancesDeleteDTO) throws Exception {
        instancesDeleteDTO.setTicket(ticket);
        return ecsService.instanceDelete(instancesDeleteDTO);

    }

    /**
     * 重启虚拟机V2版本
     */
    @PostMapping("/restart/v2")
    public ResponseResult instanceRestart2(@RequestBody JSONObject jsonObject) {
        return ecsService.instanceRestart(jsonObject.getString("instanceId"), jsonObject.getString("restartFlag"), jsonObject.getString("regionName"));
    }

    /**
     * 查询虚拟机vnc链接V2版本
     */
    @GetMapping("/vncConsole/v2")
    public ResponseResult instanceVncConsole2(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String instanceId = request.getParameter("instance_id");
        String regionName = request.getParameter("region_name");
        return ecsService.instanceVncConsole(instanceId, regionName);
    }

    /**
     * 虚拟机挂载卷V2
     */
    @PostMapping("/attach/volume/v2")
    public ResponseResult instanceattachVolume2(HttpServletRequest request, @RequestBody AttachVolumeDTO attachVolumeDTO) throws IOException {
        return ecsService.instanceAttachVolume(attachVolumeDTO);
    }

    /**
     * 虚拟机卸载卷 v2版本
     */
    @PostMapping("/dettach/volume/v2")
    public ResponseResult instanceDettachVolume2(HttpServletRequest request, @RequestBody List<AttachVolumeDTO> attachVolumeDTOs) {
        return ecsService.instanceDettachVolume(attachVolumeDTOs);
    }

    /**
     * 虚拟机下的卷列表 v2版本
     */
    @GetMapping("/volume/list/v2")
    public ResponseResult instanceVolumeList2(HttpServletRequest request, InstancesVolumeDTO instancesVolumeDTO) {
        return ecsService.instanceVolumeList(instancesVolumeDTO);
    }

    /**
     * 获取虚拟机详情
     */
    @GetMapping("/detail/v2")
    public ResponseResult instanceDetail2(@RequestParam("instanceId") String instanceId,
                                          @RequestParam("region") String regionName) {
        return ecsService.instanceDetail(instanceId, regionName);
    }
}
