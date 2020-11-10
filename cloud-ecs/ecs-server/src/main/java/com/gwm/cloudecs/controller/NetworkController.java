package com.gwm.cloudecs.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.service.NetworkService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 网络相关接口
 */
@RestController
@RequestMapping("/api/network")
public class NetworkController {

    @Autowired
    NetworkService networkService;

    /**
     * 获取vpc列表
     */
    @GetMapping("/list/v2")
    public ResponseResult networkList(@RequestParam("region") String region) {
        return networkService.networkList(region);
    }

    /**
     * 获取vpc网络详情
     */
    @GetMapping("/details/v2")
    public ResponseResult networkDetails(@RequestParam("region") String region, @RequestParam("networkId") String networkId) {
        return networkService.networkDetails(region, networkId);

    }

    /**
     * 获取vpc网络拓扑
     */
    @GetMapping("/topo/v2")
    public ResponseResult networkTopo(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("networkId") String networkId) {
        String groupId = request.getParameter("groupId");
        return networkService.networkTopo(region, networkId, groupId);
    }

    /**
     * 获取子网列表（指定vpc网络） V2
     */
    @GetMapping("/subnet/list/v2")
    public ResponseResult subnetList(@RequestParam("region") String region, @RequestParam("networkId") String networkId) {
        return networkService.subnetList(region, networkId);
    }

    /**
     * 获取子网详情 V2
     */
    @GetMapping("/subnet/details/v2")
    public ResponseResult subnetDetails(@RequestParam("region") String region, @RequestParam("subnetId") String subnetId) {
        return networkService.subnetDetails(region, subnetId);
    }

    /**
     * 获取子网的端口列表 V2
     */
    @GetMapping("/subnet/list/ports/v2")
    public ResponseResult subnetListPorts(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("subnetId") String subnetId) {
        String groupId = request.getParameter("groupId");
        return networkService.subnetListPorts(region, subnetId, groupId);
    }

    /**
     * 获取子网的虚拟机列表
     */
    @GetMapping("/subnet/list/instances/v2")
    public ResponseResult subnetListInstances(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("subnetId") String subnetId) {
        String groupId = request.getParameter("groupId");
        return networkService.subnetListInstances(region, subnetId, groupId);
    }

    /**
     * 获取安全组列表
     */
    @GetMapping("/securityGroup/list/v2")
    public ResponseResult securityGroupList(@RequestParam("region") String region) {
        return networkService.securityGroupList(region);
    }


    /**
     * 获取安全组详情v2
     */
    @GetMapping("/securityGroup/info/v2")
    public ResponseResult securityGroupInfo(@RequestParam("region") String region,
                                            @RequestParam("securityGroupId") String securityGroupId) {
        return networkService.securityGroupInfo(region, securityGroupId);
    }

    /**
     * 获取安全组规则 V2
     */

    @GetMapping("/securityGroup/rule/list/v2")
    public ResponseResult securityGroupRuleList(@RequestParam("region") String region,
                                                @RequestParam("securityGroupId") String securityGroupId) {
        return networkService.securityGroupRuleList(region, securityGroupId);
    }



    /**
     * 获取阿里云中region,SecurityGroup
     */
    @GetMapping("/get/security/group")
    @ApiOperation("获取阿里云中region下的SecurityGroup")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region", required = true)})
    public ResponseResult getSecurityGroup(@RequestParam(value = "region") String region, @RequestParam(value = "vpcId") String vpcId) {
        return networkService.getSecurityGroup(region, vpcId);
    }

    /**
     * 获取阿里云中region,VSwitches
     */
    @GetMapping("/get/vswitches")
    @ApiOperation("获取阿里云中region下的VSwitches")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region", required = true)})
    public ResponseResult getVSwitches(@RequestParam(value = "region") String region, @RequestParam(value = "vpcId") String vpcId) {
        return networkService.getVSwitches(region,  vpcId);
    }

    /**
     * 获取阿里云中region,vpc网络
     */
    @GetMapping("/get/vpcs")
    @ApiOperation("获取阿里云中region下的vpc网络")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region", required = true)})
    public ResponseResult getVpcs(@RequestParam(value = "region") String region) {
        return networkService.getVpcs(region);
    }
}
