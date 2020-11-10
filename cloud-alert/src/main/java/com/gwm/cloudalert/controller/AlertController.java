package com.gwm.cloudalert.controller;

import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudalert.service.AlertService;
import com.gwm.cloudcommon.constant.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/alert")
public class AlertController {
    @Autowired
    AlertService alertService;
    @RequestMapping("/queryList")
    public ResponseResult getAlertList(@RequestBody AlertDTO alert, HttpServletRequest request){
        return alertService.queryAlertList(alert);
    }

}
