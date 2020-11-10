package com.gwm.cloudcommon.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QuotaHandle {

    @Value("${user.url}")
    private String url;

    @Value("${user.quotas}")
    private String quotaUrl;


    /**
     * @param ticket token
     * @param operate 释放：free| 占用：occupy
     * @param resourceCode 资源编号
     * @param count 数量
     * @throws Exception
     */
    public void putQuotas(String ticket, String operate, String resourceCode, Integer count)  {
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("operate", operate);
                put("resource_code", resourceCode);
                put("number", count);
            }};
            String responseBody = HttpClient.post(url + quotaUrl, new HashMap<String, String>() {{
                put("ticket", ticket);
            }}, new JSONObject(){{put("resources", new ArrayList<JSONObject>(){{add(jsonObject);}});}}.toString());
            ResponseResult responseResult = JSON.parseObject(responseBody, ResponseResult.class);
            if (200 <= responseResult.getCode() && responseResult.getCode() < 300) {
                LogUtil.info(String.format(String.format("QuotaHandle中处理配额完成，配额参数：operate:%s,resourceCode:%s,count:%s", operate, resourceCode, count)));
            } else {
                LogUtil.error(String.format(String.format("QuotaHandle中处理配额异常，配额参数：operate:%s,resourceCode:%s,count:%s", operate, resourceCode, count)));
                throw new CommonCustomException(responseResult.getCode(), responseResult.getMessage());
            }
        } catch (Exception e) {
            throw new CommonCustomException(-1, e.getMessage());
        }

    }
 /**
     * @param ticket token
     * @param operate 释放：free| 占用：occupy
     * @param resourceCodeCount 资源编号和个数
     * @throws Exception
     */
    public void putQuotas(String ticket, String operate, Map<String, Integer> resourceCodeCount)  {
        try {
            List<JSONObject> objects = new ArrayList<>();
            for(Map.Entry<String, Integer> entry : resourceCodeCount.entrySet()){
                JSONObject jsonObject = new JSONObject() {{
                    put("operate", operate);
                    put("resource_code", entry.getKey());
                    put("number",  entry.getValue());
                }};
                objects.add(jsonObject);
            }
            String responseBody = HttpClient.post(url + quotaUrl, new HashMap<String, String>() {{
                put("ticket", ticket);
            }}, new JSONObject(){{put("resources", objects);}}.toString());
            ResponseResult responseResult = JSON.parseObject(responseBody, ResponseResult.class);
            if (200 <= responseResult.getCode() && responseResult.getCode() < 300) {
                LogUtil.info(String.format(String.format("QuotaHandle中处理配额完成，配额参数：operate:%s,resourceCodeCount:%s", operate, resourceCodeCount.toString())));
            } else {
                LogUtil.error(String.format(String.format("QuotaHandle中处理配额异常，配额参数：operate:%sresourceCodeCount:%s", operate, resourceCodeCount.toString())));
                throw new CommonCustomException(responseResult.getCode(), responseResult.getMessage());
            }
        } catch (Exception e) {
            LogUtil.error(String.format(String.format("QuotaHandle---try---catch---异常，配额参数：operate:%sresourceCodeCount:%s", operate, resourceCodeCount.toString())));
            LogUtil.error(e.getMessage(), e);
            throw new CommonCustomException(-1, e.getMessage());
        }

    }

}
