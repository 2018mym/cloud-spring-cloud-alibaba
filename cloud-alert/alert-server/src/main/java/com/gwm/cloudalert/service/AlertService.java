package com.gwm.cloudalert.service;

import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudalert.model.dto.AlertTeamDTO;
import com.gwm.cloudalert.model.dto.CloudAlertStrategyDTO;
import com.gwm.cloudalert.model.entity.AlertTeamItem;
import com.gwm.cloudalert.model.entity.CloudAlertStrategy;
import com.gwm.cloudcommon.constant.ResponseResult;

public interface AlertService {
    ResponseResult queryAlertList(AlertDTO dto);
    ResponseResult getAlarms(AlertDTO dto);
    ResponseResult sendDingDingMessage(String content);
    ResponseResult addTeamItem(AlertTeamItem alertTeamItem);
    ResponseResult updateTeamItem(AlertTeamItem alertTeamItem);
    ResponseResult  queryAlertTeamList(AlertTeamDTO alertTeamDTO);
    ResponseResult addAlertStrategy(CloudAlertStrategy cloudAlertStrategy);
    ResponseResult updateAlertStrategy(CloudAlertStrategy cloudAlertStrategy);
    ResponseResult deleteAlertStrategy(Integer id);
    ResponseResult getAlertStrategy(CloudAlertStrategy cloudAlertStrategy);
    ResponseResult queryAlertStrategyList(CloudAlertStrategyDTO cloudAlertStrategyDTO);
    ResponseResult bindAlertStrategy(String ip,Integer id);
    ResponseResult unBindAlertStrategy(String ip,Integer id);
}
