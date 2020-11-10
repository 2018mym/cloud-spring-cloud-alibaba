package com.gwm.clouduser.job;

import com.gwm.clouduser.service.BillFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class BillingTask {
    @Autowired
    BillFlowService billFlowService;


   // @Scheduled(cron = "*/3 * * * * ?")
    private void billingTask() throws Exception {
        System.out.println("账单任务开始.......");
        billFlowService.createBillFlow();
    }
}
