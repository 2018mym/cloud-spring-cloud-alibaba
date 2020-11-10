package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeAvailableResourceResponse;
import com.aliyuncs.ecs.model.v20140526.DescribeImagesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeImagesResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.dao.ImageMapper;
import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Autowired
    ImageMapper imageMapper;
    @Override
    public ResponseResult getImageList(ImageListDTO imageListDTO) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(imageListDTO.getPageNum(), imageListDTO.getPageSize());
        List<Image> list = imageMapper.getImageList(imageListDTO);
        PageInfo pageResult = new PageInfo(list);
        jsonObject.put("totalNum", pageResult.getTotal());
        jsonObject.put("list", pageResult.getList());
        return ResponseResult.succObj(jsonObject);
    }

    @Override
    public ResponseResult updateImage(Image image) {
        try {

            Image entity = imageMapper.selectByPrimaryKey(image.getId());
            entity.setUpdateAt(new Date());
            entity.setOsType(image.getOsType());

            imageMapper.updateByPrimaryKey(entity);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ();
    }

    @Override
    public Image getRecordByUUid(String imageUuid) {
       return  imageMapper.getRecordByUUid(imageUuid);
    }

    @Override
    public ResponseResult getAliImages(ImageListDTO imageListDTO) {
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.setRegionId(imageListDTO.getRegion());

        request.setPageNumber(imageListDTO.getPageNum());
        request.setPageSize(imageListDTO.getPageSize());
        request.setInstanceType(imageListDTO.getInstanceType());
        if (null!=imageListDTO.getOsType() && "".equals(imageListDTO.getOsType())) {
            request.setOSType("0".equals(imageListDTO.getOsType())? "windows":"1".equals(imageListDTO.getOsType()) ? "linux" : imageListDTO.getOsType());
        }
        try {
            DescribeImagesResponse response = GlobalVarConfig.client.getAcsResponse(request);
            return ResponseResult.succObj(response);
        } catch (ServerException e) {
            log.error(e.getErrMsg(), e);
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }
        return ResponseResult.error();
    }
}
