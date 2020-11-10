package com.gwm.cloudalert.service;

import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudcommon.constant.ResponseResult;

public interface AlertService {
    ResponseResult  queryAlertList(AlertDTO dto);
}
