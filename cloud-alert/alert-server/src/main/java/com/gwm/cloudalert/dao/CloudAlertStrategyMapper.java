package com.gwm.cloudalert.dao;

import com.gwm.cloudalert.model.dto.CloudAlertStrategyDTO;
import com.gwm.cloudalert.model.entity.AlertTeamItem;
import com.gwm.cloudalert.model.entity.CloudAlertStrategy;

import java.util.List;

public interface CloudAlertStrategyMapper {
    int deleteByPrimaryKey(Integer id);
    int insert(CloudAlertStrategy record);
    CloudAlertStrategy selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(CloudAlertStrategy record);
    List<CloudAlertStrategy> queryAlertStrategyList(CloudAlertStrategyDTO cloudAlertStrategyDTO);
    List<String> querySendUserIds(String alarmStrategyId);
    List<String> queryStrategyByTeamId(Integer alertTeamsId);



}