package com.gwm.streamsetscommon.client;

import com.gwm.cloudcommon.constant.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cloud-streamsets", path = "/api/dop")
public interface DOPClient {

    @RequestMapping(value = "/registersHostToDOP", method = RequestMethod.GET)
    ResponseResult registersHostToDOP(@RequestParam("ip") String ip);
    @RequestMapping(value = "/deleteHostDOP", method = RequestMethod.GET)
    ResponseResult deleteHostDOP(@RequestParam("ip") String ip);

}
