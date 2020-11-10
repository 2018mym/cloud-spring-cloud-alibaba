package com.gwm.cloudstreamsets.controller;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudstreamsets.service.StreamSetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  备用
 */
@Component
@Order(value = 1)   //执行顺序控制
public class ServiceStart   implements ApplicationRunner {


	@Autowired
    StreamSetsService streamSetsService;
	@Value("${dop.url}")
	private String dopUrl;
	@Value("${dop.bk_app_code}")
	private String bk_app_code;
	@Value("${dop.bk_app_secret}")
	private String bk_app_secret;
	@Value("${dop.bk_username}")
	private String bk_username;
	@Value("${dop.streamsets_template_id}")
	private String streamsets_template_id;
	@Value("${dop.bk_biz_id}")
	private String bk_biz_id;
	@Value("${cloud.api}")
	private String cloudApi;


	@Override
	public void run(ApplicationArguments args) throws Exception {
		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(6);
		JSONObject json = new JSONObject();
		json.put("bk_app_code", bk_app_code);
		json.put("bk_app_secret", bk_app_secret);
		json.put("bk_username", bk_username);
		json.put("bk_biz_id", bk_biz_id);
		json.put("cloudApi",cloudApi);
		json.put("dopUrl",dopUrl);
		json.put("template_id", streamsets_template_id);

		try {
		// 初始化锁
			streamSetsService.deleteSingleFlag();
		}catch (Exception e){
			e.printStackTrace();
		}
		exec.scheduleWithFixedDelay(new CreateStreamSetsThread(json,streamSetsService) ,1,10, TimeUnit.SECONDS);

		}
		
	}

	


