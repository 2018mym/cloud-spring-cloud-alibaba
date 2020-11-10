package com.gwm.cloudecs.schedule;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.SpringUtil;
import com.gwm.cloudecs.service.EcsService;
import com.gwm.cloudecs.service.VolumeMountService;
import com.gwm.cloudecs.service.VolumeService;
import com.gwm.cloudecs.service.impl.EcsServiceImpl;
import com.gwm.streamsetscommon.client.DOPClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ecs 创建时所需要的线程类
 */
@Slf4j
public class ECSOpenstackTask implements TaskRunnable {

    // 底层访问的url地址
    String url;
    // 标识线程名称。业务名称
    String thName;
    // 云主机创建后所生成的uuid
    String server;
    // 云主机磁盘的uuid集合,磁盘是否创建完成
    Map<String, Boolean> volumeServerMap = new HashMap<>();
    // region_name
    String regionName;
    //云主机是否创建完成
    boolean serverFlag = false;


    // 强制关闭线程时间，此时间为间隔次数  时长为count*间隔时间
    int count = 60;

    VolumeService volumeService;
    EcsService ecsService;
    VolumeMountService volumeMountService;
    DOPClient dOPClient;

    {
        volumeService = (VolumeService) SpringUtil.getBean("volumeServiceImpl");
        ecsService = (EcsServiceImpl) SpringUtil.getBean("ecsServiceImpl");
        volumeMountService = (VolumeMountService) SpringUtil.getBean("volumeMountServiceImpl");
    }

    /**
     * @param name             线程名称
     * @param server           云主机的uuid
     * @param url              访问地址的url
     * @param regionName       region 名称
     * @param volumeServerList 创建磁盘的uuid
     */
    public ECSOpenstackTask(String name, String server, List<String> volumeServerList, String url, String regionName, DOPClient dOPClient) {
        this.thName = name;
        this.server = server;
        this.url = url;
        this.regionName = regionName;
        volumeServerList.forEach(volumeServer -> {
            this.volumeServerMap.put(volumeServer, false);
        });
        this.dOPClient = dOPClient;
    }


    @Override
    public String getName() {
        return thName;
    }

    @Override
    public void run() {
        try {
            // 云主机挂载的磁盘 list
            JSONArray instanceVolumeList = new JSONArray();

            log.info(String.format("云主机磁盘列表:%s", volumeServerMap.toString()));
            // 检测云主机状态
             if (!serverFlag) {
                log.info(String.format("开始检测云主机状态......线程名称为:%s，云主机uuid:%s", thName, server));
                JSONObject escData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s", server, regionName)));
                log.info(String.format("检测云主机状态......线程名称为:%s，返回信息:%s", thName, escData.toJSONString()));
                if ("active".equals(escData.getString("status"))) {
                    serverFlag = true;
                    // TO DO 更新云主机状态
                    JSONObject escInfoData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/detail?instance_id=%s&region_name=%s", server, regionName)));
                    instanceVolumeList = escInfoData.getJSONArray("volume_info");
                    JSONObject addresses = escInfoData.getJSONObject("addresses");
                    Set<String> strings = addresses.keySet();
                    String addr = "";
                    for (String str : strings) {
                        JSONArray jsonArray = addresses.getJSONArray(str);
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        addr = jsonObject1.getString("addr");
                        if (StrUtil.isNotEmpty(addr)) {
                            break;
                        }
                    }
                    log.info(String.format("检测云主机状态......线程名称为:%s--获取到ip地址:%s", thName, addr));
                    ecsService.updateInstance(server, 1, addr);
                    // 云主机获取到ip地址想dop平台注册
                    ResponseResult responseResult = dOPClient.registersHostToDOP(addr);
                    log.info(responseResult.toString());
                } else {
                    log.info(String.format("云主机未创建成功。结束此次检测流程......线程名称为:%s", thName));
                }
            } else {
                log.info(String.format("创建云主机已完成......线程名称为:%s，云主机uuid:%s", thName, server));

            }


            // 检测磁盘状态
            //所有磁盘是否创建完成 默认成功
            boolean volumeServerFlag = true;
            int volumeCount = 0;
            for (Map.Entry<String, Boolean> entry : volumeServerMap.entrySet()) {
                if (!entry.getValue()) {
                    log.info(String.format("开始检测磁盘状态......线程名称为:%s，磁盘uuid:%s", thName, entry.getKey()));
                    JSONObject volumeData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/volume/status?volume_id=%s&region_name=%s", entry.getKey(), regionName)));
                    log.info(String.format("检测磁盘状态......线程名称为:%s，返回信息:%s", thName, volumeData.toJSONString()));
                    if ("available".equals(volumeData.getString("status"))) {
                        volumeServerMap.put(entry.getKey(), true);
                        // TO DO 更新磁盘状态
                        volumeService.updateVolumeStatus(entry.getKey(), "2");
                    } else {
                        volumeServerFlag = false;
                        log.info(String.format("该磁盘未创建成功。结束此次检测流程......线程名称为:%s，磁盘uuid:%s", thName, entry.getKey()));
                    }
                } else {
                    volumeCount++;
                    log.info(String.format("创建磁盘共计：%s已完成:%s......线程名称为:%s，磁盘uuid:%s", volumeServerMap.entrySet().size(), volumeCount, thName, entry.getKey()));
                }

            }


            // 云主机创建完成，磁盘创建完成。进行磁盘挂载, 线程关闭

            if (volumeServerFlag && serverFlag) {
                //TO DO  挂载
                JSONObject jsonObject = new JSONObject() {{
                    put("instance_id", server);
                    put("region_name", regionName);
                }};
                for (Map.Entry<String, Boolean> entry : volumeServerMap.entrySet()) {
                    if (!checkVolumeIsMount(entry.getKey(),  instanceVolumeList)) {
                        jsonObject.put("volume_id", entry.getKey());
                        JSONObject volumeData = HttpClient.postEcs(String.format(url, "/bcp/v2/instance/attach-volume"), jsonObject.toJSONString());
                        log.info(String.format("磁盘挂载状态......线程名称为:%s，返回信息:%s", thName, volumeData.toJSONString()));
                    }
                    // TO DO 更新磁盘状态
                    volumeMountService.insertVolumeMout(server, entry.getKey(), entry.getKey());
                    volumeService.updateVolumeStatus(entry.getKey(), "7");
                }
                log.info(String.format("创建云主机定时线程关闭.....线程名称为:%s", thName));
                ScheduleUtil.stop(thName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (count-- < 0) {
                log.info(String.format("创建云主机定时线程因该线程超时，则强制关闭.....线程名称为:%s", thName));
                ScheduleUtil.stop(thName);
            }
        }

    }

    /**
     * 检测该磁盘是否已挂载在云主机上
     * @param key
     * @param instanceVolumeList
     * @return 如果是  则返回true 否 则返回false
     */
    private boolean checkVolumeIsMount(String key, JSONArray instanceVolumeList) {
        List<JSONObject> jsonObjects = instanceVolumeList.toJavaList(JSONObject.class);
        Set<String> collect = jsonObjects.stream().map(jsonObject-> jsonObject.getString("id")).collect(Collectors.toSet());
        return  collect.contains(key);
    }
}
