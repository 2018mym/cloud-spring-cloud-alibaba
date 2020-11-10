package com.gwm.cloudecs.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 网络相关接口
 */
@RestController
@RequestMapping("/api/network")
public class NetworkController {

    @Value("${ecs.url.old}")
    private String httpIp;

    @Autowired
    NetworkService networkService;

    /**
     * 获取vpc列表
     */
    @GetMapping("/list/v1")
    public Object networkList() {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/network/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取vpc网络详情
     */
    @GetMapping("/details/v1")
    public Object networkDetails(@RequestParam("vpcId") String vpcId) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/network/details/" + vpcId));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取子网列表
     */
    @GetMapping("/subnet/list/v1")
    public Object subnetList() {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/subnet/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取子网列表（指定vpc网络）
     */
    @GetMapping("/subnet/info/v1")
    public Object subnetInfo(@RequestParam("networkUuid") String networkUuid) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/subnet/list?network_uuid=" + networkUuid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取子网详情
     */
    @GetMapping("/subnet/details/v1")
    public Object subnetDetails(@RequestParam("networkUuid") String networkUuid) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/subnet/details/" + networkUuid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取子网的端口列表
     */
    @GetMapping("/subnet/list/ports/v1")
    public Object subnetListPorts(@RequestParam("networkUuid") String networkUuid) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/subnet/list-ports/" + networkUuid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取子网的虚拟机列表
     */
    @GetMapping("/subnet/list/instances/v1")
    public Object subnetListInstances(@RequestParam("networkUuid") String networkUuid) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/subnet/list-instances/" + networkUuid));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取防火墙列表
     */
    @GetMapping("/firewall/list/v1")
    public Object firewallList() {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/firewall/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取安全组列表
     */
    @GetMapping("/securityGroup/list/v1")
    public Object securityGroupList() {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/security_group/list"));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 获取安全组规则
     */
    @GetMapping("/securityGroup/rule/list/v1")
    public Object securityGroupRuleList(@RequestParam("groupId") String groupId) {
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v1/security_group/list_rule?security_group_id=" + groupId));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.error(e.getMessage());
        }
    }
    // -----------------------------

    /**
     * 获取vpc列表
     */
    @GetMapping("/list/v2")
    public ResponseResult networkList(@RequestParam("region") String region) {
        return networkService.networkList(region);
    }

    /**
     * 获取vpc网络详情
     */
    @GetMapping("/details/v2")
    public ResponseResult networkDetails(@RequestParam("region") String region, @RequestParam("networkId") String networkId) {
        return networkService.networkDetails(region, networkId);

    }

    /**
     * 获取vpc网络拓扑
     */
    @GetMapping("/topo/v2")
    public ResponseResult networkTopo(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("networkId") String networkId) {
        String groupId = request.getParameter("groupId");
        return networkService.networkTopo(region, networkId, groupId);
    }

    /**
     * 获取子网列表（指定vpc网络） V2
     */
    @GetMapping("/subnet/list/v2")
    public ResponseResult subnetList(@RequestParam("region") String region, @RequestParam("networkId") String networkId) {
        return networkService.subnetList(region, networkId);
    }

    /**
     * 获取子网详情 V2
     */
    @GetMapping("/subnet/details/v2")
    public ResponseResult subnetDetails(@RequestParam("region") String region, @RequestParam("subnetId") String subnetId) {
        return networkService.subnetDetails(region, subnetId);
    }

    /**
     * 获取子网的端口列表 V2
     */
    @GetMapping("/subnet/list/ports/v2")
    public ResponseResult subnetListPorts(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("subnetId") String subnetId) {
        String groupId = request.getParameter("groupId");
        return networkService.subnetListPorts(region, subnetId, groupId);
    }

    /**
     * 获取子网的虚拟机列表
     */
    @GetMapping("/subnet/list/instances/v2")
    public ResponseResult subnetListInstances(HttpServletRequest request, @RequestParam("region") String region, @RequestParam("subnetId") String subnetId) {
        String groupId = request.getParameter("groupId");
        return networkService.subnetListInstances(region, subnetId, groupId);
    }

    /**
     * 获取安全组列表
     */
    @GetMapping("/securityGroup/list/v2")
    public ResponseResult securityGroupList(@RequestParam("region") String region) {
        return networkService.securityGroupList(region);
    }


    /**
     * 获取安全组详情v2
     */
    @GetMapping("/securityGroup/info/v2")
    public ResponseResult securityGroupInfo(@RequestParam("region") String region,
                                            @RequestParam("securityGroupId") String securityGroupId) {
        return networkService.securityGroupInfo(region, securityGroupId);
    }

    /**
     * 获取安全组规则 V2
     */

    @GetMapping("/securityGroup/rule/list/v2")
    public ResponseResult securityGroupRuleList(@RequestParam("region") String region,
                                                @RequestParam("securityGroupId") String securityGroupId) {
        return networkService.securityGroupRuleList(region, securityGroupId);
    }

}
