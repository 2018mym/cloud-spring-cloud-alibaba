package com.gwm.clouduser.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.clouduser.model.DTO.PriceModelListDTO;
import com.gwm.clouduser.model.entity.PriceModel;
import com.gwm.clouduser.service.PriceModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 计费模型
 */
@RestController
@RequestMapping("/api/priceModel")
public class PriceModelController {

    @Autowired
    PriceModelService priceModelService;
    /**
     * 获取列表
     */
    @GetMapping("/get/list")
    public Object priceModelList(PriceModelListDTO priceModelListDTO) {

        return priceModelService.getPriceModelList(priceModelListDTO);
    }


    /**
     * 获取单个
     */
    @GetMapping("/get/info")
    public Object getPriceModel(PriceModel piceModel) {

        return priceModelService.getOnePriceModel(piceModel);
    }

    /**
     * 创建
     */
    @PostMapping("/add/info")
    public Object createPriceModel(@RequestBody PriceModel piceModel) {

        return priceModelService.createPriceModel(piceModel);
    }
    /**
     * 修改
     */
    @PostMapping("/update/info")
    public Object updatePriceModel(@RequestBody PriceModel piceModel) {

        return priceModelService.updatePriceModel(piceModel);
    }

    /**
     *删除
     */
    @GetMapping("/del/info")
    public ResponseResult delPriceModel( @RequestParam("ids") List<Long> ids) throws Exception{
        return priceModelService.delPriceModel(ids);
    }


}
