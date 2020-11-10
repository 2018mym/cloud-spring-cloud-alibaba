package com.gwm.cloudecs.controller;


import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.CheckParamUtil;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesDeleteDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesListDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesStatusDTO;
import com.gwm.cloudecs.model.DTO.AttachVolumeDTO;
import com.gwm.cloudecs.model.DTO.InstancesRebuildDTO;
import com.gwm.cloudecs.model.DTO.InstancesUpdateDTO;
import com.gwm.cloudecs.model.DTO.InstancesVolumeDTO;
import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.service.EcsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/instance")
@Api(tags = "ecsController", description = "云主机相关接口")
public class ECSController {

    @Autowired
    EcsService ecsService;

    /**
     * 获取云主机类型接口v2
     */
    @GetMapping("/flavor/list/v2")
    public Object flavorList2(String region) {
        Flavor flavor = new Flavor();
        flavor.setRegion(region);
        return ecsService.getFlavorList(flavor);

    }
    /**
     * 通过flavorId 查询
     */
    @GetMapping("/flavor/v2")
    public ResponseResult getFlavorById(@RequestParam("flavorId") String flavorId) {
        return ecsService.getFlavorById(flavorId);

    }

    /**
     * 获取云主机list
     */
    @GetMapping("/list/v2")
    public ResponseResult instanceList(@RequestHeader("groupId") String groupId,
                                       @RequestHeader("userId") String userId,
                                       InstancesListDTO instancesListDTO) {
        CheckParamUtil.checkTypeRegion(instancesListDTO.getType(), instancesListDTO.getRegion());
        instancesListDTO.setGroupId(groupId);
        instancesListDTO.setUserId(userId);
        return ecsService.instanceList(instancesListDTO);

    }

    /**
     * 获取云主机list  向外提供的接口
     */
    @PostMapping("/list/v2")
    public ResponseResult instanceList(@RequestBody InstancesListDTO instancesListDTO) {
        return ecsService.instanceList2(instancesListDTO);

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
    @ApiOperation("创建云主机接口")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "header", dataType = "String", name = "ticket", value = "ticket信息，用户携带的认证信息", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "groupId", value = "用户组Id，此参数为gateway中传输", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "userId", value = "用户Id，此参数为gateway中传输", required = true)})
    public ResponseResult createInfo(@RequestHeader("ticket") String ticket,
                                     @RequestHeader("groupId") String groupId,
                                     @RequestHeader("userId") String userId,
                                     @RequestBody InstancesDTO instancesDTO) throws IOException {
        instancesDTO.setTicket(ticket);
        instancesDTO.setUserId(userId);
        instancesDTO.setProjectId(groupId);
        return ecsService.createInstances(instancesDTO);
    }

    /**
     * 重建虚拟机V2版本
     */
    @PostMapping("/rebuild/v2")
    @ApiOperation("重建云主机接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "groupId", value = "用户组Id，此参数为gateway中传输", required = true),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "userId", value = "用户Id，此参数为gateway中传输", required = true)})

    public ResponseResult instanceRebuild2(@RequestHeader("groupId") String groupId,
                                           @RequestHeader("userId") String userId,
                                           @RequestBody InstancesRebuildDTO instancesRebuildDTO) {
        return ecsService.instanceRebuild(instancesRebuildDTO, userId, groupId);
    }

    /**
     * 更新云主机 v2版本
     */
    @PostMapping("/udpate/v2")
    public ResponseResult udpateInfo(@RequestHeader("groupId") String groupId,
                                     @RequestHeader("userId") String userId,
                                     @RequestBody InstancesUpdateDTO instancesUpdateDTO) {
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
    public ResponseResult instanceDelete2(@RequestHeader("ticket") String ticket, @RequestBody InstancesDeleteDTO instancesDeleteDTO) throws Exception {
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
        String instanceId = request.getParameter("instance_id");
        String regionName = request.getParameter("region_name");
        return ecsService.instanceVncConsole(instanceId, regionName);
    }

    /**
     * 虚拟机挂载卷V2
     */
    @PostMapping("/attach/volume/v2")
    public ResponseResult instanceattachVolume2(@RequestBody AttachVolumeDTO attachVolumeDTO) throws IOException, ClientException {
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


    //   ---------------------   阿里云

    /**
     * 获取regionId
     */
    @GetMapping("/get/region")
    @ApiOperation("获取阿里云中region")
    public ResponseResult getAliRegion() {
        return ecsService.getAliRegion();
    }

    /**
     * 获取阿里云中region,下的zone
     */
    @GetMapping("/get/zone")
    @ApiOperation("获取阿里云中region,下的zone,  该接口暂时不进行使用")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region", required = true)})
    public ResponseResult getAliZone(@RequestParam(value = "region") String region) {
        return ecsService.getAliZone(region);
    }


    /**
     * 获取阿里云中region,下的规格大小
     */
    @GetMapping("/get/types")
    @ApiOperation("获取该region下的所有云主机规格")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "zone", value = "zone", required = false)
    })
    public ResponseResult getAliInstanceTypes(@RequestParam(value = "region") String region,
                                              @RequestParam(value = "zone", required = false) String zone) {
        return ecsService.getAliInstanceTypes(region, zone);
    }


}
