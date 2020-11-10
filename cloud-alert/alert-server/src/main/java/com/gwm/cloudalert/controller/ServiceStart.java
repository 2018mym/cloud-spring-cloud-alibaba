package com.gwm.cloudalert.controller;

import com.gwm.cloudalert.dao.SendAlertSingFlag;
import com.gwm.cloudalert.service.AlertService;
import com.gwm.cloudalert.service.DopAlertService;
import org.springframework.beans.factory.annotation.Autowired;
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
	private AlertService alertService;
	@Autowired
	private  SendAlertSingFlag  sendAlertSingFlag;
	@Override
	public void run(ApplicationArguments args) throws Exception {
		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(6);
		try {
		// 初始化锁
			sendAlertSingFlag.initSingleFlag();
			sendAlertSingFlag.dropSendTime();
			sendAlertSingFlag.initSendTime();
			sendAlertSingFlag.initSendTimeValue(System.currentTimeMillis());

			exec.scheduleWithFixedDelay(new SendAlertThread(alertService,sendAlertSingFlag),
					1,10, TimeUnit.SECONDS);

		}catch (Exception e){
			e.printStackTrace();
		}

		}
		
	}

	


