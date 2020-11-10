package com.gwm.cloudecs.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.model.DTO.InstanceGroupDTO;
import com.gwm.cloudecs.model.entity.InstanceGroup;
import com.gwm.cloudecs.model.entity.KeyPairs;
import com.gwm.cloudecs.service.ISSHKeyService;
import com.gwm.cloudecs.service.ServerGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 容灾组相关接口
 */
@RestController
@RequestMapping("/api/serverGroup")
public class ServerGroupController {

    @Value("${ecs.url}")
    private String httpIp;

    @Autowired
    ServerGroupService serverGroupService;

    /**
     * 获取安全组列表

    @GetMapping("/list/v1")
    public Object serverGroupList() {
        try {
            String s= HttpClient.get(String.format(httpIp, "/bcp/v1/server_group/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }
    */



    /**
     * 获取安全组详情
     */
    @GetMapping("/details/v2")
    public Object detailsInfo(InstanceGroup instanceGroup) {
        try {
            //Map<String,String> params = new HashMap<String,String>();
            //params.put("group_id",instanceGroup.getUuid());
            //params.put("region_name",instanceGroup.getRegion());

            String s =  HttpClient.get(String.format(httpIp, "/bcp/v2/server_group/detail?group_id="+ instanceGroup.getUuid()+"&region_name="+instanceGroup.getRegion()));
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
    public Object createInstanceGroup(@RequestHeader("ticket") String ticket,HttpServletRequest request,@RequestBody InstanceGroup instanceGroup) {
        String groupId = request.getParameter("groupId");
        String userId = request.getParameter("userId");
        instanceGroup.setProjectId(groupId);
        instanceGroup.setUserId(userId);
        return serverGroupService.createServerGroup(ticket,instanceGroup);
    }

    /**
     * 删除容灾组
     */
    @GetMapping("/del/info")
    public Object delInstanceGroup(@RequestHeader("ticket") String ticket,@RequestParam("ids") List<Integer> ids) throws Exception{

        return serverGroupService.delServerGroup(ticket,ids);
    }

    /**
     * 获取容灾组列表
     */
    @GetMapping("/get/list")
    public Object getInstanceGroupList(HttpServletRequest request, InstanceGroupDTO instanceGroupDTO) {
        String groupId = request.getParameter("groupId");
        //String userId = request.getParameter("userId");
        instanceGroupDTO.setGroupId(groupId);
        return serverGroupService.getServerGroupList(instanceGroupDTO);
    }


}