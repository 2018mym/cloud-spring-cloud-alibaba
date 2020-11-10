package com.gwm.cloudecs.service;

import com.gwm.cloudecs.model.entity.ObsAccount;

import java.util.Map;

public interface CephService {
    //对象存储创建AK用户
    public ObsAccount createAK(String url, Map<String, String> map)  throws Exception;
    //对象存储删除AK用户
    public String delAK(String url, Map<String, String> map)   throws Exception;
    //对象存储修改AK用户
    public ObsAccount setAK(String url, Map<String, String> map)  throws Exception;
}
