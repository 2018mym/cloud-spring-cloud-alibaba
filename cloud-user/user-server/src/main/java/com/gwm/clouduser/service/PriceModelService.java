package com.gwm.clouduser.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.common.model.DTO.InstancesListDTO;
import com.gwm.clouduser.model.DTO.PriceModelListDTO;
import com.gwm.clouduser.model.entity.PriceModel;

import java.util.List;

public interface PriceModelService {

    public ResponseResult getPriceModelList(PriceModelListDTO priceModelListDTO);
    public ResponseResult getOnePriceModel(PriceModel priceModel);

    public ResponseResult createPriceModel( PriceModel priceModel);
    public ResponseResult updatePriceModel( PriceModel priceModel);

    public ResponseResult delPriceModel( List<Long> ids)  throws Exception;

    public ResponseResult  getInstcanceByfloverId(InstancesListDTO instance) ;
}
