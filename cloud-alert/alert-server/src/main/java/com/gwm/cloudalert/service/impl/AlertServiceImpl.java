package com.gwm.cloudalert.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudalert.dao.CloudAlertMapper;
import com.gwm.cloudalert.dao.CloudAlertStrategyMapper;
import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudalert.model.dto.AlertTeamDTO;
import com.gwm.cloudalert.model.dto.CloudAlertStrategyDTO;
import com.gwm.cloudalert.model.entity.AlertTeamItem;
import com.gwm.cloudalert.model.entity.CloudAlertStrategy;
import com.gwm.cloudalert.service.AlertService;
import com.gwm.cloudalert.service.DopAlertService;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.util.HttpClient;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {
    @Autowired
    private CloudAlertMapper cloudAlertMapper;
    @Autowired
    private DopAlertService dopAlertService;
    @Autowired
    private CloudAlertStrategyMapper CloudAlertStrategyMapper;
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
    @Value("${dingding.url}")
    private String dingdingUrl;
    @Value("${dingding.app_key}")
    private String appKey;
    @Value("${dingding.app_secret}")
    private String appSecret;
    @Value("${dingding.agent_id}")
    private Long agentId;


     /**
     *
     * @param dto
     * @return
     * type：
     * cpu使用率，system_cpu_detail
     * 内存使用率，
     * 磁盘读写带宽（s/MB）,
     * 磁盘iops，
     * 网卡流入流出（Bps）
     */
    @Override
    public ResponseResult queryAlertList(AlertDTO dto) {
        JSONObject json = getPubParameter();
        String sql ="";
        /*cpu*/
        if(dto.getType().equals("system_cpu_detail")){
            sql= "select Mean(usage) as usage from  {bk_biz_id}_{type} where time >{start} and  time <{end}  " +
                    "and ip='{host}' group by minute{step} limit {statLine},{pageSize}";
        }else if(dto.getType().equals("system_mem")) {
         /*mem*/
            sql = "select mean(*) from {bk_biz_id}_{type} where time >{start} and  time <{end} " +
                    "and ip='{host}'  group by minute{step} limit {statLine},{pageSize}";
        }else if (dto.getType().equals("system_io")){
         /** 磁盘读写带宽，iops**/
            sql = "select mean(*) from  {bk_biz_id}_{type}  where time >{start} and  time <{end} " +
                    " and  ip='{host}'   group by minute{step},device_name limit {statLine},{pageSize}";
        }else if(dto.getType().equals("system_net")){
            /*网络带宽*/
            sql = "select mean(*) from  {bk_biz_id}_{type}  where time >{start} and  time <{end} " +
                    " and  ip='{host}'  and device_name !='lo'  group by minute{step},device_name limit {statLine},{pageSize}";
        }else if(dto.getType().equals("system_disk")){
            /** 磁盘容量**/
            sql = "select mean(*) from  {bk_biz_id}_{type}  where time >{start} and  time <{end} " +
                    " and  ip='{host}'   group by minute{step},device_name limit {statLine},{pageSize}";
        }
        sql=sql.replace("{bk_biz_id}",bk_biz_id);
        sql=sql.replace("{type}",dto.getType());
        sql=sql.replace("{start}",dto.getStart().toString());
        sql=sql.replace("{end}",dto.getEnd().toString());
        sql=sql.replace("{step}",dto.getStep().toString());
        sql=sql.replace("{statLine}",String.valueOf((dto.getPageNum()-1)*dto.getPageSize()));
        sql=sql.replace("{pageSize}",String.valueOf(dto.getPageSize()));
        sql=sql.replace("{host}",dto.getHost());
        System.out.println(sql);

        json.put("sql",sql);
        ResponseResult state = null;
        try {
            String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/monitor/get_ts_data/"), json.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
            if (parse.getInteger("code") == 0 ) { // 查询成功
                state = ResponseResult.succ();
                state.setData(parse.getJSONObject("data"));
            }else{
                state = ResponseResult.error(parse.getString("message"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public ResponseResult getAlarms(AlertDTO dto){
        ResponseResult state = null;
        String start = null;
        String end = null;
        if(dto.getStart() == null|dto.getEnd() == null){
            Calendar c = Calendar.getInstance();
            //过去七天
            c.setTime(new Date());
            c.add(Calendar.DATE, - 7);
            Date d = c.getTime();
            start = sdf.format(d);
            end = sdf.format(new Date());
        }else {
             start = sdf.format(new Date(dto.getStart()));
             end = sdf.format(new Date(dto.getEnd()));
        }
        try {
            String  pargram = "?bk_app_code="+bk_app_code+
                    "&bk_app_secret="+bk_app_secret+
                    "&bk_biz_id="+bk_biz_id+
                    "&bk_username="+bk_username+
                    "&source_time__gt="+java.net.URLEncoder.encode(start,"utf-8")+
                    "&source_time__lte="+java.net.URLEncoder.encode(end,"utf-8")+
                    "&page="+dto.getPageNum()+
                    "&ordering=-id"+   // 排序
                    "&page_size="+dto.getPageSize();
                    if(dto.getHost() !=null){
                        pargram =pargram +"&ip="+dto.getHost();
                    }

            String requestBack = HttpClient.get(String.format(dopUrl, "/api/c/compapi/v2/monitor/get_alarms/"+pargram));
            JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
            if (parse.getInteger("code") == 0 ) { // 查询成功
                state = ResponseResult.succ();
                state.setData(parse.getJSONObject("data"));
            }else{
                state = ResponseResult.error(parse.getString("message"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return state;

    }

    public ResponseResult alarmInstance(String alarmtId){

        String  pargram = "?bk_app_code="+bk_app_code+
                "&bk_app_secret="+bk_app_secret+
                "&bk_biz_id="+bk_biz_id+
                "&bk_username="+bk_username+
                "&id="+alarmtId;
        String requestBack = null;
        try {
            requestBack = HttpClient.get(String.format(dopUrl, "/api/c/compapi/v2/monitor/alarm_instance/"+pargram));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
        if (parse.getInteger("code") == 0 ) { // 查询成功
            return ResponseResult.succObj(parse.getJSONObject("data"));
        }else{
            return  ResponseResult.error(parse.getString("message"));
        }
    }


    @Override
    public ResponseResult sendDingDingMessage(String alarmtId) {
        ResponseResult  result = this.alarmInstance(alarmtId);
        JSONObject alarmInstance = (JSONObject) result.getData();
        if(alarmInstance == null){
            return null;
        }
        JSONObject alarm_content = alarmInstance.getJSONObject("alarm_content");
        String  source_time  = alarmInstance.getString("source_time");
        //组装告警消息
        String  content =  "告警ID:".concat(alarmInstance.getString("id")).concat("\n");
        content = content.concat("业务系统:").concat(" "+alarm_content.getString("cc_biz_name")).concat("\n");
        content = content.concat("IP:").concat(alarmInstance.getString("ip")).concat("\n");
        content = content.concat("监控指标:").concat(alarm_content.getString("source_name")).concat("\n");
        content = content.concat("告警类型:").concat(alarmInstance.getString("alarm_type")).concat("\n");
        content = content.concat("告警时间:").concat(alarmInstance.getString("source_time")).concat("\n");
        content = content.concat("告警级别:").concat(alarm_content.getString("attention")).concat("\n");
        content = content.concat("告警正文:").concat(alarm_content.getString("alarm_message")).concat(alarm_content.getString("dimension")).concat("\n");
        log.debug("ding_talk send:"+content);

        //查找告警策略
        String alarm_strategy_id = alarmInstance.getString("alarm_source_id");
        CloudAlertStrategyDTO  alertStrategy = new CloudAlertStrategyDTO();
        alertStrategy.setAlarmStrategyId(alarm_strategy_id);
        List<String>  sendUserIdsList = CloudAlertStrategyMapper.querySendUserIds(alarm_strategy_id);
        if(sendUserIdsList.size() == 0){
            log.debug("根据策略ID查不到发送用户的ID");
            return ResponseResult.error("找不到发送对象！");
        }
        // 告警策略中查找消息组
        String sendUserIds = "";
        for(String str:sendUserIdsList){
               if(sendUserIds.equals("")){
                   sendUserIds = str;
               }else{
                   sendUserIds = sendUserIds.concat(",").concat(str);
               }
        }
        DefaultDingTalkClient tokenClient = new DefaultDingTalkClient(dingdingUrl+"/gettoken");
        OapiGettokenRequest tokenRequest = new OapiGettokenRequest();
        tokenRequest.setAppkey(appKey);
        tokenRequest.setAppsecret(appSecret);
        tokenRequest.setHttpMethod("GET");
        OapiGettokenResponse tokenResponse = null;
        try {
            tokenResponse = tokenClient.execute(tokenRequest);
        } catch (ApiException e1) {
            e1.printStackTrace();
        }
        String accessToken = tokenResponse.getAccessToken();
        DingTalkClient client = new DefaultDingTalkClient(dingdingUrl+"/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(sendUserIds.toUpperCase());
        request.setAgentId(agentId);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(content);
        request.setMsg(msg);
        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,accessToken);
            log.debug("ding_talk send back:"+response.getBody());
            JSONObject jsonObject = (JSONObject) JSONObject.parse(response.getBody());
            if (jsonObject.getInteger("errcode") == 0 ) {
                return ResponseResult.succ();
            }else{
                return  ResponseResult.error(response.getBody());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public ResponseResult addTeamItem(AlertTeamItem alertTeamItem) {
        Integer state = cloudAlertMapper.insert(alertTeamItem);
        if(state>0){
            return ResponseResult.succ();
        }else{
            return ResponseResult.error();
        }

    }

    @Override
    public ResponseResult updateTeamItem(AlertTeamItem alertTeamItem) {
        if(alertTeamItem.getDeleted().intValue() == 1){
           List<String>   result= CloudAlertStrategyMapper.queryStrategyByTeamId(alertTeamItem.getId());
           if(result.size()>0){
               return ResponseResult.error("请先解除消息组绑定！");
           }
        }


        Integer state = cloudAlertMapper.update(alertTeamItem);
        if(state>0){
            return ResponseResult.succ();
        }else{
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult queryAlertTeamList(AlertTeamDTO alertTeamDTO) {
        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(alertTeamDTO.getPageNum(), alertTeamDTO.getPageSize());
            List<AlertTeamItem> list = cloudAlertMapper.queryAlertTeamList(alertTeamDTO);
            PageInfo pageResult = new PageInfo(list);
            jsonObject.put("totalNum", pageResult.getTotal());
            jsonObject.put("list", pageResult.getList());
            return ResponseResult.succObj(jsonObject);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult addAlertStrategy(CloudAlertStrategy cloudAlertStrategy) {
        CloudAlertStrategy  finalStrategy = new CloudAlertStrategy();
        BeanUtils.copyProperties(cloudAlertStrategy,finalStrategy);
        ResponseResult  result  = null;
            result = dopAlertService.addAlertStrategy(cloudAlertStrategy);
        if(result.getCode() != 200){
            return  result;
        }else{
            TreeSet alarm_strategy_id = (TreeSet) result.getData();
           String str = org.apache.commons.lang.StringUtils.join(alarm_strategy_id,",");
            finalStrategy.setAlarmStrategyId(str);
        }
          int  state = CloudAlertStrategyMapper.insert(finalStrategy);
        if(state>0){
            return ResponseResult.succ();
        }else{
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult updateAlertStrategy(CloudAlertStrategy cloudAlertStrategy) {
        CloudAlertStrategy  finalStrategy = new CloudAlertStrategy();
        BeanUtils.copyProperties(cloudAlertStrategy,finalStrategy);
        if(StringUtils.isEmpty(cloudAlertStrategy.getId())){
            return  ResponseResult.error("id is empty !");
        }
        if(StringUtils.isEmpty(cloudAlertStrategy.getAlarmStrategyId())){
            return  ResponseResult.error("alarmStrategyId is empty !");
        }
        ResponseResult  result  = null;
        result = dopAlertService.addAlertStrategy(cloudAlertStrategy);
        if(result.getCode() != 200){
            return  result;
        }else{
            TreeSet  ts  = (TreeSet) result.getData();
            finalStrategy.setAlarmStrategyId(org.apache.commons.lang.StringUtils.join(ts,","));
        }

        int  state = CloudAlertStrategyMapper.updateByPrimaryKeySelective(finalStrategy);

        if(state>0){
            return ResponseResult.succ();
        }else{
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult deleteAlertStrategy(Integer id) {
        CloudAlertStrategy cloudAlertStrategy = CloudAlertStrategyMapper.selectByPrimaryKey(id);
        if(cloudAlertStrategy == null){
            return  ResponseResult.error("id "+id+"不存在！");
        }
        if(!StringUtils.isEmpty(cloudAlertStrategy.getHost())){
            return  ResponseResult.error("策略有已绑定的主机，不能删除！");
        }

        String ids = cloudAlertStrategy.getAlarmStrategyId();
        if(cloudAlertStrategy.getAlarmStrategyId() != null) {
            for (String tmp:ids.split(",")) {
                cloudAlertStrategy.setAlarmStrategyId(tmp);
                ResponseResult result = dopAlertService.deleteAlertStrategy(cloudAlertStrategy);
                if (result.getCode() != 200) {
                    return result;
                } else {
                    JSONObject json = (JSONObject) result.getData();
                    if (!json.getString("message").equals("OK")) {
                        return result;
                    }
                }
            }
        }

        int state  =  CloudAlertStrategyMapper.deleteByPrimaryKey(id);
        if(state>0){
            return ResponseResult.succ();
        }else{
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult getAlertStrategy(CloudAlertStrategy cloudAlertStrategy) {
        CloudAlertStrategy record = CloudAlertStrategyMapper.selectByPrimaryKey(cloudAlertStrategy.getId());
        return ResponseResult.succObj(record);
    }

    @Override
    public ResponseResult queryAlertStrategyList(CloudAlertStrategyDTO cloudAlertStrategyDTO) {
        try {
            JSONObject jsonObject = new JSONObject();
            PageHelper.startPage(cloudAlertStrategyDTO.getPageNum(), cloudAlertStrategyDTO.getPageSize());
            List<CloudAlertStrategy> list = CloudAlertStrategyMapper.queryAlertStrategyList(cloudAlertStrategyDTO);
            List<CloudAlertStrategy> listall=new ArrayList<>();

            for (CloudAlertStrategy strategy:list) {
                List<AlertTeamItem> teamItemsList = cloudAlertMapper.queryAlertTeamListByIds(strategy.getAlertTeamsId());
                strategy.setAlertTeamItemList(teamItemsList);
                listall.add(strategy);
            }

            PageInfo pageResult = new PageInfo(listall);
            jsonObject.put("totalNum", pageResult.getTotal());
            jsonObject.put("list", pageResult.getList());
            return ResponseResult.succObj(jsonObject);
        } catch (CommonCustomException ce) {
            log.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error();
        }
    }

    @Override
    public ResponseResult bindAlertStrategy(String ip,Integer id) {
        CloudAlertStrategy  cloudAlertStrategy = CloudAlertStrategyMapper.selectByPrimaryKey(id);
        if(cloudAlertStrategy == null){
        return ResponseResult.error("id 不存在！");
        }
        if(null == ip){
            return ResponseResult.error("ip 不能为空！");
        }
        String ips = cloudAlertStrategy.getHost();
        if(ips == null){
            ips  = ip;
        }else{
            ips=ips+","+ip;
        }
        TreeSet<String> set = new TreeSet<String>();
        for (String str:ips.split(",")) {
            set.add(str);
        }
        ips = org.apache.commons.lang.StringUtils.join(set,",");
        cloudAlertStrategy.setHost(ips);
       return this.updateAlertStrategy(cloudAlertStrategy);
    }

    @Override
    public ResponseResult unBindAlertStrategy(String ip,Integer id) {
        CloudAlertStrategy  cloudAlertStrategy = CloudAlertStrategyMapper.selectByPrimaryKey(id);
        if(cloudAlertStrategy == null){
            return ResponseResult.error("id 不存在！");
        }
        if(null == ip){
            return ResponseResult.error("ip 不能为空！");
        }
        String hosts = cloudAlertStrategy.getHost();
        TreeSet<String> set = new TreeSet<String>();
        for (String str:hosts.split(",")) {
            set.add(str);
        }
        for (String str:ip.split(",")) {
            set.remove(str);
        }
        hosts = org.apache.commons.lang.StringUtils.join(set,",");
        cloudAlertStrategy.setHost(hosts);
        return this.updateAlertStrategy(cloudAlertStrategy);
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
