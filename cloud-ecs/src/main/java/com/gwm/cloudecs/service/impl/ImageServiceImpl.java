package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.dao.ImageMapper;
import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
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
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ();
    }

    @Override
    public Image getRecordByUUid(String imageUuid) {
       return  imageMapper.getRecordByUUid(imageUuid);
    }
}
