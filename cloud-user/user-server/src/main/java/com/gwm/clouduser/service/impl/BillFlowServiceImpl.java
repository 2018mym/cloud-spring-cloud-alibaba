package com.gwm.clouduser.service.impl;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.DateUtil;
import com.gwm.cloudecs.client.CloudEcsClient;
import com.gwm.cloudecs.client.CloudStoreClient;
import com.gwm.cloudecs.common.model.DTO.InstancesListDTO;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.clouduser.dao.BillFlowMapper;
import com.gwm.clouduser.model.entity.BillFlow;
import com.gwm.clouduser.service.BillFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BillFlowServiceImpl  implements BillFlowService {

    @Autowired
    CloudEcsClient cloudEcsClient;
    @Autowired
    CloudStoreClient cloudStoreClient;
    /*
    @Autowired
    InstancesMapper instancesMapper;
    @Autowired
    VolumeMapper volumeMapper;
    @Autowired
    BillFlowMapper billFlowMapper;
    */
    public ResponseResult createBillFlow() {
        InstancesListDTO instance = new InstancesListDTO();
        //List<Instances> instanceList = instancesMapper.getInstancesList(instance);
        List<String> instanceTypes = new ArrayList<String>();
        instanceTypes.add("openstack");
        instanceTypes.add("vmware");
        InstancesListDTO dtoList = new InstancesListDTO();
        dtoList.setInstanceTypes(instanceTypes);
        ResponseResult result = cloudEcsClient.instanceList(dtoList);
        System.out.println("result:"+result);
        BillFlow billFlow = null;
        /*
        if(instanceList!=null&&instanceList.size()>0)
        for(Instances ins:instanceList){
            billFlow = new  BillFlow();
            billFlow.setResourceId(ins.getUuid()); //资源id
            billFlow.setResourceType("instance");
            billFlow.setCloudType(ins.getType());//镜像所属平台
            billFlow.setZone(ins.getZone());
            billFlow.setRegion(ins.getRegion());
            billFlow.setBillDay(new Date());  //计费时间
            Date createAt = ins.getCreateAt();
            System.out.println("====="+DateUtil.getDiffHours(new Date(),createAt));
        }
        */
        return ResponseResult.succ();

    }
}
