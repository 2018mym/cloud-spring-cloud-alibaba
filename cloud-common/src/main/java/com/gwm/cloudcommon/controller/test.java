package com.gwm.cloudcommon.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.exception.QuotaException;
import com.gwm.cloudcommon.model.VO.UserVo;
import com.gwm.cloudcommon.redis.RedisUtil;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
public class test {

    @Autowired
    RedisUtil redisUtil;

    /**
     * 关闭虚拟机
     */
    @PostMapping("/api/test")
    public Object instanceRestart(HttpServletRequest request, HttpServletResponse response) {
        String auth = request.getHeader("ticket");
        System.out.println(auth);
        UserVo userVo = new UserVo();
        userVo.setUserId("111");
        userVo.setUserName("2222342");
        userVo.setHaspower("123".equals(auth));
//        return userVo;
        LogUtil.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        return ResponseResult.succObj(userVo);
        //return  ResponseResult.succ();
 //       return aaaa.succ();
//        return new ResponseResult(1, "zhende sadfasdfa", userVo);
    }
    /**
     * 关闭虚拟机
     */
    @GetMapping("/api/test2")
    public ResponseResult aaa(HttpServletRequest request) {
        String auth1 = request.getParameter("userName");
        request.getParameterMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
//        String[] userIds = parameterMap.get("userId");
        return ResponseResult.succObj(new HashMap<String, String>() {{
            put("userName", auth1);
//            put("userId", userIds.toString());
            put("aaaa", "aaasdfg");
        }});
    }
    /**

     * 关闭虚拟机
     */
    @PostMapping("/api/test3")
    public ResponseResult aa3a(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
        String userName = request.getParameter("userName");
        return ResponseResult.succObj(new HashMap<String, String>() {{
            put("userName", userName);
//            put("userId", userIds.toString());
            put("aaaa", "aaasdfg");
            put("bbb", jsonObject.getString("bbb"));
        }});
    }
    /**
      * 关闭虚拟机
     */
    @GetMapping("/api/test4/create/test4")
    public ResponseResult aa34() {
        return ResponseResult.succ();
    }
    /**
      * 关闭虚拟机
     */
    @GetMapping("/api/test9")
    public ResponseResult aad34() {
        redisUtil.set("a", "aaaa");
        return ResponseResult.succ();
    } /**
      * 关闭虚拟机
     */
    @GetMapping("/api/test8")
    public ResponseResult aad3d4() {
        LogUtil.debug("debug");
        LogUtil.info("info");
        Object a = redisUtil.get("a");
        return ResponseResult.succ((String) a);
    }
    /**
      * 关闭虚拟机
     */
    @GetMapping("/api/test4/create/test5")
    public ResponseResult aadss34() {
        throw new QuotaException(-1, "cess", "free", "10002", 4);
    }
    /**
      * 关闭虚拟机
     */
    @GetMapping("/api/test4/delete/test5")
    public ResponseResult aads3s34() {

        throw new QuotaException(-1, "csdfds", "occupy", "10002", 2);
    }
 /**
      * 关闭虚拟机
     */
    @GetMapping("/api/test5")
    public ResponseResult a3ads3s34() {

        JSONObject escInfoData = HttpClient.getEcs(String.format("http://10.255.128.191:8080:%s", String.format("/bcp/v2/instance/detail?instance_id=%s&region_name=%s", "abebf694-89a6-4ef7-9d8b-38dad79519a4", "Region_BDDC")));

        JSONObject addresses = escInfoData.getJSONObject("addresses");
        Set<String> strings = addresses.keySet();
        String addr = "";
        for (String str : strings) {
            JSONArray jsonArray = addresses.getJSONArray(str);
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            addr = jsonObject1.getString("addr");
            if (!addr.isEmpty()) {
                break;
            }
        }
        return ResponseResult.succObj(addr);
    }


}
