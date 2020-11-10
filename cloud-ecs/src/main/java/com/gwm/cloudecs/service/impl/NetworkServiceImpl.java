package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.service.EcsService;
import com.gwm.cloudecs.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;

@Service
public class NetworkServiceImpl implements NetworkService {
    @Value("${ecs.url}")
    private String url;

    @Autowired
    EcsService ecsService;

    @Override
    public ResponseResult networkList(String region) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/network/list?region_name=%s", region)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));
        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult networkDetails(String region, String networkId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/network/detail?region_name=%s&network_id=%s", region, networkId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult networkTopo(String region, String networkId, String groupId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/network/topo?region_name=%s&network_id=%s", region, networkId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            Integer retCode = parse.getInteger("ret_code");
            if (200 <= retCode && retCode < 300) {
                JSONObject data = parse.getJSONObject("data");
                JSONArray instances = data.getJSONArray("instances");
                Iterator<Object> o = instances.iterator();
                while (o.hasNext()) {
                    String instanceId = (String) o.next();
                    boolean flag = ecsService.checkInstanceIpaddr(null, instanceId, groupId);
                    if (!flag) {
                        o.remove();
                    }
                }
                return new ResponseResult(retCode, parse.getString("ret_str"), data);
            } else {
                return new ResponseResult(retCode, parse.getString("ret_str"), parse.get("data"));
            }

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult subnetList(String region, String networkId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/subnet/list?region_name=%s&network_id=%s", region, networkId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult subnetDetails(String region, String subnetId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/subnet/detail?region_name=%s&subnet_id=%s", region, subnetId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult subnetListPorts(String region, String subnetId, String groupId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/subnet/list-ports?region_name=%s&subnet_id=%s", region, subnetId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            Integer retCode = parse.getInteger("ret_code");
            if (200 <= retCode && retCode < 300) {
                JSONArray data = parse.getJSONArray("data");
                Iterator<Object> o = data.iterator();
                while (o.hasNext()) {
                    JSONObject jsonObject = (JSONObject) o.next();
                    JSONArray fixedIps = jsonObject.getJSONArray("fixed_ips");
                    boolean flag = ecsService.checkInstanceIpaddr(fixedIps.getJSONObject(0).getString("ip_address"), null, groupId);
                    if (!flag) {
                        o.remove();
                    }
                }
                return new ResponseResult(retCode, parse.getString("ret_str"), data);
            } else {
                return new ResponseResult(retCode, parse.getString("ret_str"), parse.get("data"));
            }


        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult subnetListInstances(String region, String subnetId, String groupId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/subnet/list-instances?region_name=%s&subnet_id=%s", region, subnetId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            Integer retCode = parse.getInteger("ret_code");
            if (200 <= retCode && retCode < 300) {
                JSONArray data = parse.getJSONArray("data");
                Iterator<Object> o = data.iterator();
                while (o.hasNext()) {
                    JSONObject jsonObject = (JSONObject) o.next();
                    String instanceId = jsonObject.getString("id");
                    boolean flag = ecsService.checkInstanceIpaddr(null, instanceId, groupId);
                    if (!flag) {
                        o.remove();
                    }
                }
                return new ResponseResult(retCode, parse.getString("ret_str"), data);
            } else {
                return new ResponseResult(retCode, parse.getString("ret_str"), parse.get("data"));
            }

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult securityGroupList(String region) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/security_group/list?region_name=%s", region)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult securityGroupInfo(String region, String securityGroupId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/security_group/detail?region_name=%s&security_group_id=%s", region, securityGroupId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @Override
    public ResponseResult securityGroupRuleList(String region, String securityGroupId) {
        try {
            String s = HttpClient.get(String.format(url, String.format("/bcp/v2/security_group/list-rule?region_name=%s&security_group_id=%s", region, securityGroupId)));
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            return new ResponseResult(parse.getInteger("ret_code"), parse.getString("ret_str"), parse.get("data"));

        } catch (IOException e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }
}
