package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.ObsAccountListDTO;
import com.gwm.cloudecs.model.entity.ObsAccount;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObsAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ObsAccount record);

    int insertSelective(ObsAccount record);

    ObsAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ObsAccount record);

    int updateByPrimaryKey(ObsAccount record);

    List<ObsAccount> getObsAccountList(ObsAccountListDTO dto);
}
