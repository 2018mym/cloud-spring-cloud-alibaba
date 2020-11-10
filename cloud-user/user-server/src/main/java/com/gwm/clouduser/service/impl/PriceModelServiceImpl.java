package com.gwm.clouduser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.client.CloudEcsClient;
import com.gwm.cloudecs.common.model.DTO.InstancesListDTO;
import com.gwm.clouduser.dao.PriceModelMapper;
import com.gwm.clouduser.model.DTO.PriceModelListDTO;
import com.gwm.clouduser.model.entity.Flavor;
import com.gwm.clouduser.model.entity.PriceModel;
import com.gwm.clouduser.model.vo.PriceModelVO;
import com.gwm.clouduser.service.PriceModelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PriceModelServiceImpl implements PriceModelService {
    @Autowired
    private PriceModelMapper priceModelMapper;
    @Autowired
    CloudEcsClient cloudEcsClient;

    @Override
    public ResponseResult getPriceModelList(PriceModelListDTO priceModelListDTO) {
        JSONObject jsonObject = new JSONObject();
        List<PriceModelVO> volist = new ArrayList<PriceModelVO>();
        List<PriceModel> list = priceModelMapper.getPriceModelList(priceModelListDTO);
        if(list!=null&&list.size()>0) {
            for(PriceModel p:list) {
                PriceModelVO vo = new PriceModelVO();
                BeanUtils.copyProperties(p,vo);
                String floverId = p.getFlavorId();
                ResponseResult result = cloudEcsClient.getFlavorById(floverId);
                if(result.getCode()==200){
                   //String value=  result.getData();
                    JSONObject js1 =  (JSONObject) JSONObject.parse(result.getData().toString());
                    Flavor flavor = JSONObject.toJavaObject(js1,Flavor.class);
                    vo.setFlavor(flavor);
                }
                volist.add(vo);
            }
        }
        PageInfo pageResult = new PageInfo(volist);
        PageHelper.startPage(priceModelListDTO.getPageNum(), priceModelListDTO.getPageSize());
        jsonObject.put("totalNum", pageResult.getTotal());
        jsonObject.put("list", pageResult.getList());
        return ResponseResult.succObj(jsonObject);
    }

    @Override
    public ResponseResult getOnePriceModel(PriceModel priceModel) {
        PriceModel one  = priceModelMapper.selectByPrimaryKey(priceModel.getId());
        return ResponseResult.succObj(one);
    }

    @Override
    public ResponseResult createPriceModel(PriceModel priceModel) {
        priceModel.setDeleted(0); //未删除
        priceModel.setCreatedAt(new Date());
        PriceModelListDTO dto = new PriceModelListDTO();
        //如果存在相同的价格模型。不允许插入
        dto.setCloudType(priceModel.getCloudType());
        dto.setRegion(priceModel.getRegion());
        dto.setZone(priceModel.getZone());
        dto.setType(priceModel.getType());
        //如果是云主机的话需要传flavorId,磁盘不需要判断flavorId
        if(priceModel.getType().equalsIgnoreCase("instance")){
            dto.setFlavorId(priceModel.getFlavorId());
        }

        List<PriceModel> list = priceModelMapper.getPriceModelList(dto);
        if(list!=null&&list.size()>0){
            return ResponseResult.error("存在相同的价格模型，不允许插入！");
        }else{
            priceModelMapper.insert(priceModel);
            return ResponseResult.succ();
        }

    }

    public ResponseResult updatePriceModel( PriceModel priceModel){
        PriceModel oldEntity = priceModelMapper.selectByPrimaryKey(priceModel.getId());
        oldEntity.setUpdatedAt(new Date());
        oldEntity.setRegion( priceModel.getRegion());
        oldEntity.setZone( priceModel.getZone());
        oldEntity.setType( priceModel.getType());
        oldEntity.setPrice( priceModel.getPrice());
        oldEntity.setDiskType( priceModel.getDiskType());
        oldEntity.setInstanceType( priceModel.getInstanceType());
        oldEntity.setCloudType( priceModel.getCloudType());
        priceModelMapper.updateByPrimaryKeySelective(oldEntity);
        return ResponseResult.succ();

    }


    @Override
    public ResponseResult delPriceModel(List<Long> ids) throws Exception {
        if(ids!=null&&ids.size()>0) {
            ids.forEach(id-> {
                PriceModel priceModel  = priceModelMapper.selectByPrimaryKey(id);
                priceModel.setDeleted(1); //被删除了
                priceModel.setDeletedAt(new Date());
                priceModelMapper.updateByPrimaryKey(priceModel);
            });
        }
        return ResponseResult.succ();
    }

    public ResponseResult  getInstcanceByfloverId(InstancesListDTO instance) {
        ResponseResult result = cloudEcsClient.instanceList(instance);
        return result;
    }
}
