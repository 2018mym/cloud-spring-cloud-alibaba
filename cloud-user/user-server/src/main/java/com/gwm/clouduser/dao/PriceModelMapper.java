package com.gwm.clouduser.dao;

import com.gwm.clouduser.model.DTO.PriceModelListDTO;
import com.gwm.clouduser.model.entity.PriceModel;

import java.util.List;

public interface PriceModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PriceModel record);

    int insertSelective(PriceModel record);

    PriceModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PriceModel record);

    int updateByPrimaryKey(PriceModel record);

    List<PriceModel> getPriceModelList(PriceModelListDTO listDTO);

    PriceModel getOnePriceModel(PriceModel record);
}