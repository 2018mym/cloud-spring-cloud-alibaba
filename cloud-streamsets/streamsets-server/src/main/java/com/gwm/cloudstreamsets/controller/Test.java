package com.gwm.cloudstreamsets.controller;

import com.gwm.cloudecs.client.CloudEcsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class Test {

    @Autowired
    CloudEcsClient cloudEcsClient;

//    /**
//     *
//     获取列表
//     *
//     * @return
//     */
//    @RequestMapping("/list/v2")
//    public ResponseResult getStreamSetsList(@RequestHeader("groupId") String groupId,
//                                            @RequestHeader("userId") String userId) {
//        InstancesListDTO instancesListDTO = new InstancesListDTO();
//        instancesListDTO.setEnv("release2");
//        instancesListDTO.setGroupId(groupId);
//        instancesListDTO.setUserId(userId);
//        return cloudEcsClient.listEcsV2(instancesListDTO);
//    }
}
