package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.dao.ImageMapper;
import com.gwm.cloudecs.dao.InstancesMapper;
import com.gwm.cloudecs.dao.VolumeMapper;
import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;
import com.gwm.cloudecs.model.VO.ImageTotalListVO;
import com.gwm.cloudecs.model.VO.InstanceTotalListVO;
import com.gwm.cloudecs.model.VO.VolumeTotalListVO;
import com.gwm.cloudecs.service.ResourceFactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceFactServiceImpl implements ResourceFactService {

    @Autowired
    private InstancesMapper instancesMapper;
    @Autowired
    private VolumeMapper volumeMapper;
    @Autowired
    private ImageMapper imageMapper;


    public ResponseResult totalResourceList(ResourceTotalListDTO resDTO){
        JSONObject jsonObject = new JSONObject();
        //主机信息汇总
        List<InstanceTotalListVO>  instanceList = instancesMapper.totalInstanceByProjectId(resDTO);
        jsonObject.put("instance",instanceList);

        //卷信息汇总
        List<VolumeTotalListVO>  volumeList = volumeMapper.totalVolumeByProjectId(resDTO);
        jsonObject.put("volume",volumeList);

        //镜像
        List<ImageTotalListVO> imageList = imageMapper.totalImageByProjectId(resDTO);
        jsonObject.put("image",imageList);
        return ResponseResult.succObj(jsonObject);
    }
}
