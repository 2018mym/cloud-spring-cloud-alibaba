package com.gwm.cloudecs.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;
import com.gwm.cloudecs.service.RsasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 云主机相关的漏洞扫描接口
 */

@RestController
@RequestMapping("/api/rsas")
public class RSASController {

    @Autowired
    RsasService rsasService;

    /**
     * 云主机扫描检测
     */
    @GetMapping("/scan/instance")
    public ResponseResult scanInstance(@RequestParam("instanceId") String instanceId) {
        return rsasService.scanInstance(instanceId);
    }

    /**
     * 云主机报表下载
     */
    @GetMapping("/report/instance")
    public void reportInstance(@RequestParam("instanceId") String instanceId, HttpServletResponse response) throws IOException {
        rsasService.reportInstance(instanceId, response);
    }

}
