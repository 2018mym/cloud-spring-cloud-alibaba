package com.gwm.cloudecs.client;


import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.common.model.DTO.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "cloud-ecs", path = "api/store")
public interface CloudStoreClient {

    /**
     * 创建卷V2
     */
    @PostMapping("/volume/create/v2")
    ResponseResult volumeCreate(@RequestHeader("ticket") String ticket,
                                       @RequestHeader("groupId") String groupId,
                                       @RequestHeader("userId") String userId,
                                       @RequestBody VolumeDTO volumeDTO);

    /**
     * 获取所有卷信息 V2
     */
    @PostMapping("/volume/list/v2")
    ResponseResult volumeList2(@RequestBody VolumeListDTO volumeListDTO);
}
