package com.gwm.cloudecs.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.handler.QuotaHandle;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.SpringUtil;
import com.gwm.cloudecs.common.model.DTO.InstancesDTO;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.dao.InstancesMapper;
import com.gwm.cloudecs.model.entity.CloudAccount;
import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.model.entity.Image;
import com.gwm.cloudecs.service.*;
import com.gwm.cloudecs.service.impl.EcsServiceImpl;
import com.gwm.streamsetscommon.client.DOPClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ecs 创建时所需要的线程类
 */
@Slf4j
public class ECSVmwareTask implements TaskRunnable {

    // 底层访问的url地址
    String url;
    // 标识线程名称。业务名称
    String thName;
    // 创建云主机参数
    InstancesDTO instancesDTO;
    // 创建云主机的配置参数
    Flavor flavor;
    // vmware账号表
    CloudAccount cloudAccount;
    // 镜像表
    Image image;
    DOPClient dOPClient;
    QuotaHandle quotaHandle;


    EcsService ecsService;
    InstancesMapper instancesMapper;

    public ECSVmwareTask(String threadName, Flavor flavor, InstancesDTO instancesDTO, String url, CloudAccount cloudAccount, Image image, DOPClient dOPClient) {
        this.thName = threadName;
        this.flavor = flavor;
        this.instancesDTO = instancesDTO;
        this.url = url;
        this.image = image;
        this.cloudAccount = cloudAccount;
        this.dOPClient = dOPClient;
    }

    {
        ecsService = (EcsServiceImpl) SpringUtil.getBean("ecsServiceImpl");
        instancesMapper = (InstancesMapper) SpringUtil.getBean("instancesMapper");
        quotaHandle = (QuotaHandle) SpringUtil.getBean("quotaHandle");
    }


    @Override
    public String getName() {
        return thName;
    }


    @Override
    public void run() {

        try {
            log.info(String.format("创建vmware云主机线程启动.....线程名称为:%s", thName));
            // 重新组织参数  命名规范不同需要重新组织参数
            JSONObject ecsParam = new JSONObject();
            ecsParam.put("template", image.getName());
            ecsParam.put("vm_name", instancesDTO.getName() + "-" +RandomStringUtils.randomAlphanumeric(6));
            ecsParam.put("datacenter_name", cloudAccount.getDatacenter());
            ecsParam.put("datastore_name", cloudAccount.getDatastore());
            ecsParam.put("cluster_name", cloudAccount.getCluster());
            ecsParam.put("network_name", cloudAccount.getNetwork());
            ecsParam.put("memoryMB", flavor.getMemoryMb());
            ecsParam.put("disk_size", flavor.getRootGb());
            ecsParam.put("power_on", true);
            ecsParam.put("vmware_user", cloudAccount.getUserName());
            ecsParam.put("numCPUs", flavor.getVcpus());
            ecsParam.put("vmware_password", cloudAccount.getPassword());
            ecsParam.put("vmware_host", cloudAccount.getUrl());
            ecsParam.put("type", instancesDTO.getType());
            instancesDTO.setState(2);
            instancesDTO.setDeleted(0);
            instancesDTO.setCreateAt(new Date());
            instancesDTO.setNetwork(cloudAccount.getNetwork());
            instancesDTO.setRootGb(flavor.getRootGb());
            instancesDTO.setMemoryMb(flavor.getMemoryMb());
            instancesDTO.setVcpus(flavor.getVcpus());
            instancesDTO.setOsType(image.getOsType());
            instancesMapper.insert(instancesDTO);
            JSONObject  escData = HttpClient.postEcs(String.format(url, "/bcp/v2/instance/create"), ecsParam.toJSONString());
            String server = escData.getString("server");
            instancesDTO.setUuid(server);
            instancesMapper.updateByPrimaryKey(instancesDTO);

            log.info(String.format("创建VMware云主机定时获取ip该线程开启.....线程名称为:%s", String.format("ecs-ipaddr-%s", server)));
            ScheduleUtil.stard(new ECSVmwareIpaddrTask(String.format("ecs-ipaddr-%s", server), cloudAccount.getUserName(), cloudAccount.getPassword(), cloudAccount.getUrl(), server, dOPClient),  10, 5, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info(String.format("创建VMware云主机,异常清除数据,释放配额,云主机名称为：%s", instancesDTO.getName()));
            instancesMapper.deleteByPrimaryKey(instancesDTO.getId());
            quotaHandle.putQuotas(instancesDTO.getTicket(), Constant.FREE, instancesDTO.getHashMap());
        }

    }
}
