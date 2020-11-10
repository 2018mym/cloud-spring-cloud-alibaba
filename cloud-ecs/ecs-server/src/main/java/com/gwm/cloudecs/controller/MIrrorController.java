package com.gwm.cloudecs.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.service.ImageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 镜像模块
 */
@RestController
@RequestMapping("/api/mirror")
public class MIrrorController {

    @Autowired
    ImageService imageService;
    /**
     * 获取镜像列表
     */
    @GetMapping("/get/list")
    public Object ImageList(ImageListDTO imageListDTO) {
        return imageService.getImageList(imageListDTO);
    }

    /**
     * 更新镜像操作系统类型
     */
    @PostMapping("/update/info")
    public Object ImageList(@RequestBody Image image) {
        return imageService.updateImage(image);
    }

    /**
     * 获取阿里云中region,images
     */
    @GetMapping("/get/images")
    @ApiOperation("获取阿里云中region,下的images")
    public ResponseResult getAliImages(ImageListDTO imageListDTO) {
        return imageService.getAliImages(imageListDTO);
    }
}
