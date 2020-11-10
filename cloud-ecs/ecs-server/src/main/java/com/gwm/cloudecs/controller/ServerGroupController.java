package com.gwm.cloudecs.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.model.DTO.InstanceGroupDTO;
import com.gwm.cloudecs.model.entity.InstanceGroup;
import com.gwm.cloudecs.service.ServerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 容灾组相关接口
 */
@RestController
@RequestMapping("/api/serverGroup")
public class ServerGroupController {

    @Autowired
    ServerGroupService serverGroupService;

    /**
     * 获取安全组详情
     */
    @GetMapping("/details/v2")
    public Object detailsInfo(InstanceGroup instanceGroup) {
        try {
            //Map<String,String> params = new HashMap<String,String>();
            //params.put("group_id",instanceGroup.getUuid());
            //params.put("region_name",instanceGroup.getRegion());

            String s =  HttpClient.get(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/server_group/detail?group_id="+ instanceGroup.getUuid()+"&region_name="+instanceGroup.getRegion()));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 创建容灾组
     */
    @PostMapping("/create/info")
    public Object createInstanceGroup(@RequestHeader("ticket") String ticket,
                                      @RequestHeader("groupId") String groupId,
                                      @RequestHeader("userId") String userId,
                                      @RequestBody InstanceGroup instanceGroup) {
        instanceGroup.setProjectId(groupId);
        instanceGroup.setUserId(userId);
        return serverGroupService.createServerGroup(ticket,instanceGroup);
    }

    /**
     * 删除容灾组
     */
    @GetMapping("/del/info")
    public Object delInstanceGroup(@RequestHeader("ticket") String ticket, @RequestParam("ids") List<Integer> ids) throws Exception{

        return serverGroupService.delServerGroup(ticket,ids);
    }

    /**
     * 获取容灾组列表
     */
    @GetMapping("/get/list")
    public Object getInstanceGroupList(@RequestHeader("groupId") String groupId,
                                       InstanceGroupDTO instanceGroupDTO) {
        instanceGroupDTO.setGroupId(groupId);
        return serverGroupService.getServerGroupList(instanceGroupDTO);
    }


}