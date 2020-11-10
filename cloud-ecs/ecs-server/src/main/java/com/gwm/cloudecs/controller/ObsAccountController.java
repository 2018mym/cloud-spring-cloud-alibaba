package com.gwm.cloudecs.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.ObsAccountListDTO;
import com.gwm.cloudecs.model.entity.ObsAccount;
import com.gwm.cloudecs.service.ObsAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obsAccount")
public class ObsAccountController {

    @Autowired
    ObsAccountService obsAccountService;

    //删除账号
    @GetMapping("/del/info")
    public ResponseResult delAccountInfo(@RequestParam("ids") List<Integer> ids) throws Exception{
        return obsAccountService.delInfo(ids);
    }

    @GetMapping("/get/list")
    public ResponseResult getAccounttList(ObsAccountListDTO obsAccountListDTO) {
        return obsAccountService.getAccountList(obsAccountListDTO);
    }

    //创建账号
    @PostMapping("/add/info")
    public ResponseResult addAccountInfo(@RequestBody ObsAccount account) {
        return obsAccountService.addAccountInfo(account);

    }

    //更新用户AK
    @PostMapping("/update/info")
    public ResponseResult updateAccountInfo(@RequestBody ObsAccount account)
    {
        return obsAccountService.updateCloudAccountInfo(account);
    }

    //更新用户配额
    @PostMapping("/update/accountQuota")
    public ResponseResult updateAccountQuota(@RequestBody ObsAccount account)
    {
        return obsAccountService.updateCloudAccountQuota(account);
    }

    //查询单个用户bucket使用容量
    @GetMapping("/get/bucket")
    public ResponseResult getBucket(ObsAccount account)
    {
        return obsAccountService.getBucket(account);
    }



}
