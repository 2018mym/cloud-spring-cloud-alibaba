package com.gwm.cloudecs.schedule;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.SpringUtil;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.service.EcsService;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import com.gwm.cloudecs.service.impl.EcsServiceImpl;
import com.gwm.streamsetscommon.client.DOPClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * vmware 云主机定时获取ip地址 默认情况是5分钟
 */
@Slf4j
public class ECSVmwareIpaddrTask implements TaskRunnable {


    // 标识线程名称。业务名称
    String thName;
    // vmware 账号
    String userName;
    // 标识线程名称。业务名称
    String vmwarePassword;
    // 标识线程名称。业务名称
    String vmwareHost;
    // 标识线程名称。业务名称
    String instanceId;

    // 强制关闭线程时间，此时间为间隔次数  时长为count*间隔时间
    int count = 30;

    EcsService ecsService;
    DOPClient dOPClient;

    public ECSVmwareIpaddrTask(String thName, String userName, String vmwarePassword, String vmwareHost, String instanceId, DOPClient dOPClient) {
        this.thName = thName;
        this.instanceId = instanceId;
        this.vmwareHost = vmwareHost;
        this.vmwarePassword = vmwarePassword;
        this.userName = userName;
        this.dOPClient = dOPClient;
    }

    @Override
    public String getName() {
        return thName;
    }

    {
        ecsService = (EcsServiceImpl) SpringUtil.getBean("ecsServiceImpl");
    }

    @Override
    public void run() {
        try {
            JSONObject escDatadetail = HttpClient.getEcs(String.format(GlobalVarConfig.ecsUrl, String.format("/bcp/v2/instance/detail?type=vmware&vmware_user=%s&vmware_password=%s&vmware_host=%s&instance_id=%s", userName, vmwarePassword, vmwareHost, instanceId)));
            String ipAddr = escDatadetail.getString("ipAddr");
            if (StrUtil.isNotEmpty(ipAddr)) {
                ecsService.updateInstance(instanceId, 1, ipAddr);
                log.info(String.format("创建VMware云主机定时获取ip线程--获取到ip地址:%s，线程名称为：%s,正常关闭该线程", ipAddr, thName));
                // 云主机获取到ip地址想dop平台注册
                dOPClient.registersHostToDOP(ipAddr);
                ScheduleUtil.stop(thName);
            } else {
                log.info(String.format("创建VMware云主机定时获取ip线程--未获取到ip地址，线程名称为：%s", thName));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (count-- < 0) {
                log.info(String.format("创建VMware云主机定时获取ip线程因该线程超时，则强制关闭.....线程名称为:%s", thName));
                ScheduleUtil.stop(thName);
            }
        }
    }
}
