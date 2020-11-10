package com.gwm.cloudecs.config;

import cn.hutool.json.JSONUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.gwm.cloudecs.model.entity.CloudAccount;
import com.gwm.cloudecs.service.CloudAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 存放全局变量的信息
 * 此类是为了初始化vm中的账号信息，存放全局
 */
@Component
@Configurable
//@RefreshScope
public class GlobalVarConfig {

    public static Map<String, CloudAccount> cloudAccountMap = new HashMap<>();

    public static String ecsUrl;
    public static String accessKeyId;
    public static String secret;
    public static IAcsClient client;

    @Value("${ecs.url}")
    private String url;
    @Value("${aliyun.accessKeyId}")
    private String aliyunAccessKeyId;
    @Value("${aliyun.secret}")
    private String aliyunSecret;
    @Value("${aliyun.regionId}")
    private String aliyunRegionId;

    @Autowired
    CloudAccountService cloudAccountService;

    @PostConstruct
    public void iniit() {
        ecsUrl = url;
        accessKeyId = aliyunAccessKeyId;
        secret = aliyunSecret;
        List<CloudAccount> vmware = cloudAccountService.getRecordListByType("vmware", null);
        vmware.forEach(cloudAccount -> cloudAccountMap.put(String.format("%s-%s-%s", cloudAccount.getType(), JSONUtil.parseObj(cloudAccount.getZone()).entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()).get(0), cloudAccount.getRegion()), cloudAccount));
        initClient();
    }

    /**
     * 初始化阿里云的client
     */
    private void initClient() {
        // Create and initialize a DefaultAcsClient instance
        DefaultProfile profile = DefaultProfile.getProfile(
                aliyunRegionId,          // The region ID
                aliyunAccessKeyId,      // The AccessKey ID of the RAM account
                aliyunSecret); // The AccessKey Secret of the RAM account
        // 创建并初始化DefaultAcsClient实例。
        client = new DefaultAcsClient(profile);

    }

}
