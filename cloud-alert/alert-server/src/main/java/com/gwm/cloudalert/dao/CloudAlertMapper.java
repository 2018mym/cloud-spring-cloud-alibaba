package com.gwm.cloudalert.dao;

import com.gwm.cloudalert.model.dto.AlertTeamDTO;
import com.gwm.cloudalert.model.entity.AlertTeamItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CloudAlertMapper {
    int  insert(AlertTeamItem alertTeamItem);
    int  update(AlertTeamItem alertTeamItem);
    List<AlertTeamItem> queryAlertTeamList(AlertTeamDTO alertTeamDTO);
    List<AlertTeamItem> queryAlertTeamListByIds(@Param("ids") String ids);

}
