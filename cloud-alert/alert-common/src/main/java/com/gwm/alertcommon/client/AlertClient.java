package com.gwm.alertcommon.client;

import com.gwm.cloudcommon.constant.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cloud-alert", path = "/api/alert")
public interface AlertClient {

    @RequestMapping(value = "/deleteAlertStrategyByIp", method = RequestMethod.GET)
    ResponseResult deleteAlertStrategyByIp(@RequestParam("ip") String ip);

}
