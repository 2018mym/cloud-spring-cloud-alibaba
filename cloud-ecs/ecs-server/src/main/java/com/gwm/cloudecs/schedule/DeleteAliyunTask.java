package com.gwm.cloudecs.schedule;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.google.gson.Gson;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.dao.InstancesMapper;
import com.gwm.cloudecs.service.EcsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DeleteAliyunTask {


    @Autowired
    InstancesMapper instancesMapper;

    @Autowired
    EcsService ecsService;

    @Scheduled(cron = "0 0 19 * * ?")
//    @PostConstruct
    public void deleteInstance() {
        log.info("开始执行删除阿里云主机");
        List<Instances> instances = instancesMapper.selectBydeleteInstance();
        log.info(instances.toString());
        for (int i = 0; i < instances.size(); i++) {
            Instances instance = instances.get(i);
            try {
                DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
                request.setRegionId(instance.getRegion());
                List<String> instanceIdList = new ArrayList<String>();
                instanceIdList.add(instance.getUuid());
                request.setInstanceIds(instanceIdList);
                DescribeInstanceStatusResponse response = GlobalVarConfig.client.getAcsResponse(request);
                JSONObject responseJson = JSONObject.parseObject(new Gson().toJson(response));
                JSONArray instanceStatuses = responseJson.getJSONArray("instanceStatuses");
                String status = instanceStatuses.getJSONObject(0).getString("status");
                log.info(String.format("该云主机id:%s状态为：%s", instance.getUuid(), status));
                if ("Running".equals(status)) {
                    StopInstanceRequest request2 = new StopInstanceRequest();
                    request2.setInstanceId(instance.getUuid());
                    request2.setRegionId(instance.getRegion());
                    GlobalVarConfig.client.getAcsResponse(request2);
                    Thread.sleep(20000);
                }
                if ("Starting".equals(status)) {
                    Thread.sleep(10000);
                    StopInstanceRequest request2 = new StopInstanceRequest();
                    request2.setInstanceId(instance.getUuid());
                    request2.setRegionId(instance.getRegion());
                    GlobalVarConfig.client.getAcsResponse(request2);
                    Thread.sleep(20000);
                }

                if ("Stopping".equals(status)) {
                    Thread.sleep(20000);
                }

                // 创建API请求并设置参数。
                DeleteInstanceRequest request4 = new DeleteInstanceRequest();
                // 设置一个地域ID。
                request4.setRegionId(instance.getRegion());
                // 指定一个实例ID。
                request4.setInstanceId(instance.getUuid());

                // 发起请求并处理应答或异常。
                DeleteInstanceResponse response4 = GlobalVarConfig.client.getAcsResponse(request4);
                JSONObject jsonObject1 = JSONObject.parseObject(new Gson().toJson(response4));

                // 删除失败
                if (StrUtil.isNotEmpty(jsonObject1.getString("code"))) {
                    log.error("定时任务删除失败  " + instance.getUuid());
                    log.error(jsonObject1.getString("message"));
                } else {
                    log.error("删除云主机云主机id" + instance.getUuid());
                    instance.setState(9);
                    instance.setDeleted(1);
                    instance.setDeletedAt(new Date());
                    instancesMapper.updateByPrimaryKey(instance);
                }

            } catch (ClientException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }

        }


    }
}