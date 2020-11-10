package com.gwm.cloudstreamsets.controller;

import com.gwm.alertcommon.client.AlertClient;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudstreamsets.service.StreamSetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dop")
@Slf4j
public class DOPController {

    @Autowired
    private StreamSetsService streamSetsService;
    @Autowired
    AlertClient alertClient;

    @GetMapping("/registersHostToDOP")
    public ResponseResult registersHostToDOP(@RequestParam("ip") String ip) {
        log.debug("进行dop平台注册，注册云主机ip地址为:" + ip);
        ResponseResult  result = streamSetsService.registersHostToDOP(ip);
        log.debug("result:" +result.getCode()+","+ result.getMessage()+":"+result.toString());
        return result;
    }

    @GetMapping("/deleteHostDOP")
    public ResponseResult deleteHostDOP(@RequestParam("ip") String ip) {
        log.debug("进行dop平台删除host:" + ip);
        ResponseResult  result = streamSetsService.deleteHostFromDOP(ip);
        log.debug("result:" +result.getCode()+","+ result.getMessage()+":"+result.toString());
        log.debug("清除改主机策略，解绑或者删除！");
        result = alertClient.deleteAlertStrategyByIp(ip);
        if(result.getCode() != Constant.SUCCESSCODE){
            return  result;
        }
        return ResponseResult.succ();
    }

}
