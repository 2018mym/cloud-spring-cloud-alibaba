package com.gwm.cloudcommon.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@Component
public class HttpConnectionManager {

    PoolingHttpClientConnectionManager cm = null;

    @PostConstruct
    public void init() {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        /**
         * socket配置（默认配置 和 某个host的配置）
         */
//        SocketConfig socketConfig = SocketConfig.custom()
//                .setTcpNoDelay(true)     //是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
//                .setSoReuseAddress(true) //是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
//                .setSoTimeout(500)       //接收数据的等待超时时间，单位ms
//                .setSoLinger(60)         //关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
//                .setSoKeepAlive(true)    //开启监视TCP连接是否有效
//                .build();
//        cm.setDefaultSocketConfig(socketConfig);
        //最大连接数
        cm.setMaxTotal(200);
        //默认的每个路由的最大连接数
        cm.setDefaultMaxPerRoute(20);
    }

    public CloseableHttpClient getHttpClient() {
        /**
         * request请求相关配置
         */
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(30 * 1000)         //连接超时时间
                .setSocketTimeout(30 * 1000)          //读超时时间（等待数据超时时间）
                .setConnectionRequestTimeout(500)    //从池中获取连接超时时间
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(defaultRequestConfig) //默认请求配置
                .build();

        /*CloseableHttpClient httpClient = HttpClients.createDefault();//如果不采用连接池就是这种方式获取连接*/
        return httpClient;
    }
}