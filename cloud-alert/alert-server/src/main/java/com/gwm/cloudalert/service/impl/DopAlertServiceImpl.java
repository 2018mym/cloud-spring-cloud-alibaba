package com.gwm.cloudalert.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gw.bpm.flowable.StringUtil;
import com.gwm.cloudalert.model.entity.CloudAlertStrategy;
import com.gwm.cloudalert.service.DopAlertService;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

@Service
@Slf4j
public class DopAlertServiceImpl implements DopAlertService {
    @Value("${dop.url}")
    private String dopUrl;
    @Value("${dop.bk_app_code}")
    private String bk_app_code;
    @Value("${dop.bk_app_secret}")
    private String bk_app_secret;
    @Value("${dop.bk_username}")
    private String bk_username;
    @Value("${dop.bk_biz_id}")
    private String bk_biz_id;

public  JSONObject  getAlarmStrategyJSON(CloudAlertStrategy strategy){
    JSONObject  cpuRequestJson = null;
    if(strategy.getMonitorType().equals("cpu")){
        cpuRequestJson = this.getRequestJSON("/cpu_alarm_strategy.json");
        cpuRequestJson.put("hostindex_id",7);
    }else if(strategy.getMonitorType().equals("mem")){
        cpuRequestJson = this.getRequestJSON("/mem_alarm_strategy.json");
        cpuRequestJson.put("hostindex_id",64);
    }else if(strategy.getMonitorType().equals("disk")){
        cpuRequestJson = this.getRequestJSON("/disk_alarm_strategy.json");
        cpuRequestJson.put("hostindex_id",81);
    }else if(strategy.getMonitorType().equals("base_alarm")){
        strategy.setMonitorLevel("2");
        strategy.setThreshold(null);
        strategy.setMethod("gte");
        cpuRequestJson = this.getRequestJSON("/base_alarm_strategy.json");
        cpuRequestJson.put("hostindex_id",10002);
    }else{
        return null;
    }
    cpuRequestJson.put("bk_app_code", bk_app_code);
    cpuRequestJson.put("bk_app_secret", bk_app_secret);
    cpuRequestJson.put("bk_username", bk_username);
    cpuRequestJson.put("bk_biz_id", bk_biz_id);

    JSONObject  alarmLevelConfig = new JSONObject();
    JSONObject  alert = new JSONObject();
    alert.put("monitor_level",strategy.getMonitorLevel());
    alert.put("phone_receiver",new JSONArray());
    JSONArray  notyfyWay = new JSONArray();
    notyfyWay.add("weixin");
    alert.put("notify_way",notyfyWay);
    alert.put("is_recovery",false);
    JSONArray  roleList = new JSONArray();
    roleList.add("Operator");
    alert.put("role_list",roleList);
    alert.put("responsible",new JSONArray());
    alert.put("notice_start_time",strategy.getNoticeStartTime());
    alert.put("notice_end_time",strategy.getNoticeEndTime());
    JSONArray detectAlgorithm = new JSONArray();
    JSONObject config = new JSONObject();
    config.put("threshold",strategy.getThreshold());
    config.put("message","");
    config.put("method",strategy.getMethod());
    JSONObject object = new JSONObject();
    object.put("config",config);
    object.put("display","当前值"+strategy.getMethod()+"阈值:"+strategy.getThreshold()+"%");
    object.put("name","静态阈值");
    object.put("algorithm_id",1000);
    detectAlgorithm.add(object);
    alert.put("detect_algorithm",detectAlgorithm);
    alarmLevelConfig.put(strategy.getMonitorLevel().toString(),alert);
    cpuRequestJson.put("alarm_level_config",alarmLevelConfig);
    // 判断是创建还是更新
    if(StringUtils.isEmpty(strategy.getAlarmStrategyId())) {
        cpuRequestJson.put("alarm_strategy_id", 0); //创建
    }else{
        cpuRequestJson.put("alarm_strategy_id", strategy.getAlarmStrategyId()); //更新
    }
    // rules
    JSONObject   rules = new JSONObject();
    rules.put("count",strategy.getCount());
    rules.put("alarm_window",strategy.getAlarmWindow());
    rules.put("check_window",strategy.getCheckWindow());
    cpuRequestJson.put("rules",rules);
    // hosts
    if(!StringUtils.isEmpty(strategy.getHost())) {
        JSONArray ip = new JSONArray();
        String[] hosts = strategy.getHost().split(",");
        for (String host : hosts) {
            JSONObject hostJson = new JSONObject();
            hostJson.put("ip", host);
            hostJson.put("bk_cloud_id", "0");
            ip.add(hostJson);
        }
        cpuRequestJson.put("ip", ip);
        cpuRequestJson.put("is_enabled",true);
    }else {
        cpuRequestJson.put("is_enabled",false);// 没有绑定IP，先保持关闭
    }


    return cpuRequestJson;
}
    @Override
    public ResponseResult addAlertStrategy(CloudAlertStrategy strategy) {
        String[] monitor_type = strategy.getMonitorType().split(",");
        String[] monitor_level = strategy.getMonitorLevel().split(",");
        String[] threshold = strategy.getThreshold().split(",");
        String[] method = strategy.getMethod().split(",");
        String[] alarm_strategy_Id = null;

        if(monitor_type.length==monitor_level.length&&monitor_level.length==threshold.length && monitor_level.length==method.length){
            log.debug("符合规则");
        }else{
            return ResponseResult.error("请检查 monitorType monitorLevel  threshold method 格式不匹配（为逗号分隔的数组）");
        }

        if(strategy.getAlarmStrategyId()!=null) {
            alarm_strategy_Id = strategy.getAlarmStrategyId().split(",");
            if(alarm_strategy_Id.length != monitor_type.length){
                return ResponseResult.error("请检查 alarmStrategyId 格式！");
            }
        }
        TreeSet<String> alarm_strategy_id = new TreeSet<String>();
        for(int i=0;i<monitor_type.length;i++) {
            strategy.setMonitorType(monitor_type[i]);
            strategy.setMonitorLevel(monitor_level[i]);
            strategy.setThreshold(threshold[i]);
            strategy.setMethod(method[i]);
            if(alarm_strategy_Id != null){
                strategy.setAlarmStrategyId(alarm_strategy_Id[i]);
            }
            JSONObject requestJson = this.getAlarmStrategyJSON(strategy);
            if (requestJson == null) {
                return ResponseResult.error("MonitorType is not support");
            }
            String requestBack = null;
            try {
                requestBack = HttpClient.post(String.format(dopUrl, "api/c/compapi/v2/monitor/save_alarm_strategy"),
                        requestJson.toJSONString());
                log.debug("save_alarm_strategy:"+requestJson.toJSONString());
                log.debug("save_alarm_strategy:"+requestBack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
            if(!parse.getString("message").equals("OK")){
                return ResponseResult.error(parse.getString("message"));
            }else{
                String tmpid  = parse.getJSONObject("data").getString("alarm_strategy_id");
                alarm_strategy_id.add(tmpid);
            }

        }
        return ResponseResult.succObj(alarm_strategy_id);
    }

    @Override
    public ResponseResult updateAlertStrategy(CloudAlertStrategy cloudAlertStrategy) {

        if(StringUtils.isEmpty(cloudAlertStrategy.getAlarmStrategyId())) {
            return ResponseResult.error("AlarmStrategyId isEmpty! ");
        }
        return this.addAlertStrategy(cloudAlertStrategy);
    }


    /**
     * {
     *     "bk_app_code": "bk_nodeman",
     *     "bk_app_secret": "9701ccf9-6c39-4f99-8127-d08cd3517692",
     *     "bk_username": "admin",
     *     "bk_biz_id":"9",
     *     "alarm_strategy_id":"3751"
     * }
     * @param cloudAlertStrategy
     * @return
     */
    @Override
    public ResponseResult deleteAlertStrategy(CloudAlertStrategy cloudAlertStrategy) {
        if(cloudAlertStrategy.getAlarmStrategyId() == null){
            return ResponseResult.error("AlarmStrategyId is  null!");
        }
        JSONObject json = getPubParameter();
        json.put("alarm_strategy_id",cloudAlertStrategy.getAlarmStrategyId());
        String requestBack = null;
        try {
            requestBack = HttpClient.post(String.format(dopUrl, "api/c/compapi/v2/monitor/delete_alarm_strategy"),
                    json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
        return  ResponseResult.succObj(parse);
    }

    @Override
    public ResponseResult getAlertStrategy(CloudAlertStrategy cloudAlertStrategy) {
        if(cloudAlertStrategy.getAlarmStrategyId() == null){
            return ResponseResult.error("AlarmStrategyId is  null!");
        }
        JSONObject json = getPubParameter();
        json.put("alarm_strategy_id",cloudAlertStrategy.getAlarmStrategyId());
        String requestBack = null;
        try {
            requestBack = HttpClient.post(String.format(dopUrl, "api/c/compapi/v2/monitor/get_alarm_strategy"),
                    json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
        return  ResponseResult.succObj(parse);
    }


    public JSONObject getRequestJSON(String path){
        JSONObject json = null;
        InputStream config = getClass().getResourceAsStream(path);
        if (config == null) {
            throw new RuntimeException("读取文件失败");
        } else {
            try {
                json = JSONObject.parseObject(config, JSONObject.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  json;
    }


    public JSONObject getPubParameter(){
        JSONObject json = new JSONObject();
        json.put("bk_app_code", bk_app_code);
        json.put("bk_app_secret", bk_app_secret);
        json.put("bk_username", bk_username);
        json.put("bk_biz_id", bk_biz_id);
        return  json;
    }

}
