package com.gwm.cloudecs.controller;

import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.model.DTO.KeyPairsListDTO;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.service.ISSHKeyService;
import com.gwm.cloudecs.service.ImageService;
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
    public Object ImageList(@RequestBody  Image image) {
        return imageService.updateImage(image);
    }


}
