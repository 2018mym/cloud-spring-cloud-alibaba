package com.gwm.cloudalert.service;

import com.gwm.cloudalert.model.entity.CloudAlertStrategy;
import com.gwm.cloudcommon.constant.ResponseResult;

import java.io.IOException;

public interface DopAlertService {

    ResponseResult addAlertStrategy(CloudAlertStrategy cloudAlertStrategy);
    ResponseResult updateAlertStrategy(CloudAlertStrategy cloudAlertStrategy);
    ResponseResult deleteAlertStrategy(CloudAlertStrategy cloudAlertStrategy);
    ResponseResult getAlertStrategy(CloudAlertStrategy cloudAlertStrategy);


}
