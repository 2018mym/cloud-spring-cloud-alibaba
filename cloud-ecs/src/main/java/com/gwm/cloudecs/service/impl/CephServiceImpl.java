package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.model.entity.ObsAccount;
import com.gwm.cloudecs.service.CephService;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CephServiceImpl implements CephService {


    public ObsAccount createAK(String url, Map<String, String> map) throws Exception{

        String path = url+"/v1/createAK";
        String s =  HttpClient.post(path,map);
        JSONObject jsonResult = (JSONObject) JSONObject.parse(s);

        System.out.println("jsonResult:"+jsonResult);

        //取出返回状态
        String status = (String)jsonResult.get("status").toString();

        if("false".equalsIgnoreCase(status)) {
            return null;

        }else { //ceph账号创建成功
            String value = (String)jsonResult.get("data").toString();
            JSONObject js1 =  (JSONObject) JSONObject.parse(value);
            ObsAccount entity = new ObsAccount();
            entity.setCephUser((String)js1.get("username").toString());
            String quotaStr = (String)js1.get("quota").toString();
            if(quotaStr!=null) {
                entity.setQuota(Integer.parseInt(quotaStr));
            }
            entity.setAccessKey((String)js1.get("access_key").toString());
            entity.setSecretKey((String)js1.get("secret_key").toString());

            return entity;
        }

    }

    @Override
    public String  delAK(String url, Map<String, String> map)  throws Exception{
        //取出返回状态
        String s = HttpClient.get(String.format(url, "/v1/delAK"));
        JSONObject jsonResult = (JSONObject) JSONObject.parse(s);
        String status = (String)jsonResult.get("status").toString();

        if("false".equalsIgnoreCase(status)) {
            return "false";
        }else { //ceph账号删除成功
            return "true";
        }

    }

    @Override
    public ObsAccount setAK(String url, Map<String, String> map) throws Exception {
        String s = HttpClient.get(String.format(url, "/v1/createAK"));
        JSONObject jsonResult = (JSONObject) JSONObject.parse(s);
        //取出返回状态
        String status = (String)jsonResult.get("status").toString();

        if("false".equalsIgnoreCase(status)) {
            return null;
        }else { //ceph账号创建成功
            String value = (String)jsonResult.get("data").toString();
            JSONObject js1 = (JSONObject) JSONObject.parse(value);
            ObsAccount entity = new ObsAccount();
            entity.setCephUser((String)js1.get("username").toString());
            String quotaStr = (String)js1.get("quota").toString();
            if(quotaStr!=null) {
                entity.setQuota(Integer.parseInt(quotaStr));
            }

            return entity;
        }

    }
}
