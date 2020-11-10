package com.gwm.cloudecs.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.model.entity.CloudAccount;
import com.gwm.cloudecs.service.CloudAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/cloud-account")
@Api(tags = "CloudAccountController", description = "vmwarezone相关接口")
public class CloudAccountController {

    @Autowired
    CloudAccountService cloudAccountService;

    /**
     * 创建云主机接口v2版本
     */
    @GetMapping("/getZone/v2")
    @ApiOperation("获取区域")
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", dataType = "String", name = "region", value = "region参数", required = true) ,
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "type类型参数", required = true) })
    public ResponseResult getZone(@RequestParam("region") String  region, @RequestParam("type") String  type ){
        return cloudAccountService.getZone(type,  region);
    }

}
