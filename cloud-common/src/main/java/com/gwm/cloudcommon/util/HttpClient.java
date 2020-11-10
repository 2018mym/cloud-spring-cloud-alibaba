package com.gwm.cloudcommon.util;



import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.exception.CommonCustomException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HttpClient {

    static HttpConnectionManager httpConnectionManager = null;

    static {
        httpConnectionManager = (HttpConnectionManager) SpringUtil.getBean("httpConnectionManager");
    }


    public static String get(String url) throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient;
        httpclient = httpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpGet httpget = new HttpGet(url);
            response = httpclient.execute(httpget);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            responseBody = EntityUtils.toString(entity, "UTF-8");
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return responseBody;
    }
    public static String get(String url, Map<String, String> header) throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient;
        httpclient = httpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        try {

            HttpGet httpget = new HttpGet(url);
            if (header != null || header.size() > 0) {
                Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    httpget.setHeader(entry.getKey(), entry.getValue());
                }
            }

            response = httpclient.execute(httpget);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            responseBody = EntityUtils.toString(entity, "UTF-8");
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return responseBody;
    }
    public static JSONObject getEcs(String url) {
        try {
            String get  = get(url);
            JSONObject parse = (JSONObject) JSONObject.parse(get);
            Integer code = parse.getInteger("ret_code");
            if (200 <= code && code < 300) {
                return (JSONObject) JSONObject.parse(parse.getString("data"));
            } else {
                throw new CommonCustomException(code, parse.getString("ret_str"));
            }
        } catch (IOException e) {
           LogUtil.error(e.getMessage(), e);
            throw new CommonCustomException(-1, "IO异常！");
        }


    }
    public static String post(String url, List<NameValuePair> params)
            throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient = httpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpclient.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            responseBody = EntityUtils.toString(entity, "UTF-8");

        } finally {
            if (response != null) {
                response.close();
            }
        }

        return responseBody;
    }

    public static JSONObject postEcs(String url, String data) throws IOException {
        String post = post(url, data);
        JSONObject parse = (JSONObject) JSONObject.parse(post);
        Integer code = parse.getInteger("ret_code");
        if (200 <= code && code < 300) {
            return (JSONObject) JSONObject.parse(parse.getString("data"));
        } else {
            throw new CommonCustomException(code, parse.getString("ret_str"));
        }

    }
    public static String post(String url, String data) throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient = httpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");

            httpPost.setEntity(new StringEntity(data));
            StringEntity s = new StringEntity(data, Charset.forName("UTF-8"));  //对参数进行编码，防止中文乱码
            s.setContentEncoding("UTF-8");
            httpPost.setEntity(s);
            response = httpclient.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            responseBody = EntityUtils.toString(entity, "UTF-8");

        } finally {
            if (response != null) {
                response.close();
            }
        }

        return responseBody;
    }


    public static String post(String url, Map<String, String> header, String data)
            throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient = httpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            if (header != null || header.size() > 0) {
                Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");

            httpPost.setEntity(new StringEntity(data));
            StringEntity s = new StringEntity(data, Charset.forName("UTF-8"));  //对参数进行编码，防止中文乱码
            s.setContentEncoding("UTF-8");
            httpPost.setEntity(s);
            response = httpclient.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            responseBody = EntityUtils.toString(entity, "UTF-8");
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return responseBody;
    }

    public static String post(String url, Map<String, String> header)
            throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient = httpConnectionManager.getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);

            if (header != null || header.size() > 0) {
                Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }
//            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpclient.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            responseBody = EntityUtils.toString(entity, "UTF-8");
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return responseBody;
    }

    public static void main(String[] args) {
        try {
         UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(null, "UTF-8");
         System.out.println(111);
//            String s = get("http://10.255.128.100:8080/bcp/v1/instance/list");
//            String s = "{\"uuid\":\"017fe4b1-d5c6-413f-9954-bcc1ea91b6bf\",\"restart_flag\":\"SOFT\"}";
//            String post = post("http://10.255.128.100:8080/bcp/v1/instance/restart", s);
//            System.out.println(post);
            /*
            String httpIp =  "http://10.251.13.9:5000/v1/createAK";
            Map<String, String> map = new HashMap<String, String>();
            map.put("display_name", "gw00179593");
            map.put("quota", 3+"");
            map.put("enabled", "true");
            map.put("quota_type", "user");

            String  s1 =  " {\"display_name\":\"test03\", \"quota\":10240, \"enabled\": true, \"quota_type\": \"user\"}";
            String s = post(httpIp,s1);
             System.out.println(s);
            */


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
