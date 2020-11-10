package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.model.entity.Image;

public interface ImageService {

    public ResponseResult getImageList(ImageListDTO imageListDTO);

    public ResponseResult updateImage(Image image);

    Image getRecordByUUid(String imageUuid);

    /**
     * 获取阿里云中的镜像列表
     * @param imageListDTO
     * @return
     */
    ResponseResult getAliImages(ImageListDTO imageListDTO);
}
