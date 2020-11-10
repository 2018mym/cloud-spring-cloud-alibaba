package com.gwm.cloudecs.client;


import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesDeleteDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesListDTO;
import com.gwm.cloudecs.common.model.DTO.InstancesStatusDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient(name = "cloud-ecs", path = "api/instance")
public interface CloudEcsClient {

    /**
     * 创建虚拟机
     */
    @RequestMapping(value = "/create/v2", method = RequestMethod.POST)
    ResponseResult createEcsV2(@RequestHeader("ticket") String ticket,
                               @RequestHeader("groupId") String groupId,
                               @RequestHeader("userId") String userId,
                               @RequestBody InstancesDTO instancesDTO);

    /**
     * 删除虚拟机
     */
    @RequestMapping(value = "/delete/v2", method = RequestMethod.POST)
    ResponseResult instanceDelete2(@RequestHeader("ticket") String ticket, @RequestBody InstancesDeleteDTO instancesDeleteDTO);

    /**
     * 获取虚拟机详情
     */
    @RequestMapping(value = "/detail/v2", method = RequestMethod.GET)
    ResponseResult instanceDetail2(@RequestParam("instanceId") String instanceId,
                                          @RequestParam("region") String regionName);

    /**
     * 获取云主机状态v2接口
     */
    @RequestMapping(value = "/check/status/v2", method = RequestMethod.POST)
    ResponseResult instanceStatus(@RequestBody List<InstancesStatusDTO> instancesStatusList);

    /**
     * 获取云主机list  向外提供的接口
     */
    @RequestMapping(value = "/list/v2", method = RequestMethod.POST)
    ResponseResult instanceList(@RequestBody InstancesListDTO instancesListDTO);

    /**
     * 通过flavorId 查询
     */
    @RequestMapping(value = "/flavor/v2", method = RequestMethod.GET)
    ResponseResult getFlavorById(@RequestParam("flavorId") String flavorId);

}
