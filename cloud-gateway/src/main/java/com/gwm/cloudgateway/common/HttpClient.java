package com.gwm.cloudgateway.common;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Description:
 * Author:     gaocl
 * Date:       2016/12/15
 * Version:     V1.0.0
 * Update:     更新说明
 */
public class HttpClient {

    public static String get(String url) throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient;
        httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {

                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity, "utf-8") : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            responseBody = httpclient.execute(httpget, responseHandler);
        } finally {
            httpclient.close();
        }

        return responseBody;
    }
    public static String get(String url, Map<String, String> header) throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient;
        httpclient = HttpClients.createDefault();
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

    public static String post(String url, List<NameValuePair> params)
            throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();

                        return entity != null ? EntityUtils.toString(entity, "utf-8") : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

            responseBody = httpclient.execute(httpPost, responseHandler);
        } finally {
            httpclient.close();
        }

        return responseBody;
    }

    public static String post(String url, String data) throws IOException {

        String responseBody = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json");

            httpPost.setEntity(new StringEntity(data));
            StringEntity s = new StringEntity(data, Charset.forName("UTF-8"));  //对参数进行编码，防止中文乱码
            s.setContentEncoding("UTF-8");
            httpPost.setEntity(s);

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response)
                        throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();

                        return entity != null ? EntityUtils.toString(entity, "utf-8") : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

            responseBody = httpclient.execute(httpPost, responseHandler);
        } finally {
            httpclient.close();
        }

        return responseBody;
    }

    public static String POSTRequest(String path, String param) {
        try {
            URL url = new URL(path.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(60000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            if (param != null && !"".equals(param)) {
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.write(param.getBytes("UTF-8"));
                out.flush();
                out.close();
            }
            byte[] data = readStream(conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream());
            String json = new String(data, "UTF-8");
            return json;
        } catch (Exception e) {
            return "";
        }
    }


    public static String GETRequest(String path) {
        try {
            URL url = new URL(path.toString());
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String l = "";
            StringBuilder sb = new StringBuilder();
            while ((l = br.readLine()) != null) {
                sb.append(l);
            }
            br.close();

            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inputStream.close();
        return outStream.toByteArray();
    }

    /**
     * @param @param  path
     * @param @param  param
     * @param @param  file
     * @param @return
     * @return String
     * @throws
     * @Title: upload
     * @Description: HttpURLConnection 上传文件
     */
    public static String upload(String path, InputStream is, String fileName) {
        String json = null;
        try {
            String BOUNDARY = "---------7d4a6d158c9"; // 定义数据分隔线
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"upload\";filename=\"" + fileName + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            byte[] data = sb.toString().getBytes();
            out.write(data);
            //DataInputStream in = new DataInputStream(new FileInputStream(file));
            DataInputStream in = new DataInputStream(is);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            out.write(end_data);
            out.flush();
            out.close();

            byte[] responseData = readStream(conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream());
            json = new String(responseData);

        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return json;
    }


}
