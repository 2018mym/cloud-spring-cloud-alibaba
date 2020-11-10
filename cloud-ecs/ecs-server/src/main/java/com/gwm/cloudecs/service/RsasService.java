package com.gwm.cloudecs.service;


import com.gwm.cloudcommon.constant.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RsasService {
    /**
     * 云主机扫描检测
     */
    ResponseResult scanInstance(String instanceId);


    /**
     * 云主机报表下载
     */
    void reportInstance(String instanceId, HttpServletResponse response) throws IOException;
}
