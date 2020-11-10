package com.gwm.cloudstreamsets.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudstreamsets.model.entity.StreamSets;
import com.gwm.cloudstreamsets.service.StreamSetsService;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateStreamSetsThread implements Runnable {
    private JSONObject json;
    private StreamSetsService streamSetsService;

public  CreateStreamSetsThread(JSONObject json, StreamSetsService streamSetsService){
    this.json = json;
    this.streamSetsService = streamSetsService;
}
public JSONObject getEcsHost(String uuid, String region,String ticket){
    String requestBack = null;
    try {
        Map<String, String> header = new HashMap<String, String>();
        header.put("ticket",ticket);
        requestBack=  HttpClient.get(String.format(json.getString("cloudApi"),"api/instance/detail/v2?region="+region+"&instanceId="+uuid),header);
        JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
        if(parse.getString("code").equals("200")){
            String regex = "\\\"addr\":(.+?),";
            Matcher matcher = Pattern.compile(regex).matcher(requestBack);
            while (matcher.find()){
                String ret = matcher.group(1).replaceAll("\\\"", "");
                if(isIp(ret)){
                    JSONObject  json = new JSONObject();
                    json.put("ip",ret);
                    String vcpus = parse.getJSONObject("data").getJSONObject("flavor_info").getString("vcpus");
                    String ram= parse.getJSONObject("data").getJSONObject("flavor_info").getString("ram");
                    String disk = parse.getJSONObject("data").getJSONObject("flavor_info").getString("disk");
                    json.put("vcpus",vcpus);
                    json.put("ram",ram);
                    json.put("disk",disk);
                    return  json;
                }
                return  null;
            }
            return  null;
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}

    public  boolean isIp(String ip) {
        boolean b1 = ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        return b1;
    }

public boolean getECSState(StreamSets streamSets)   {
    JSONArray arrays = new JSONArray();
    JSONObject  object = new JSONObject();
    object.put("uuid",streamSets.getInstancesId());
    object.put("region",streamSets.getRegion());
    arrays.add(object);
    String requestBack = null;
    Map<String, String> header = new HashMap<String, String>();
    header.put("ticket",streamSets.getTicket());
    try {
        requestBack = HttpClient.post(String.format(json.getString("cloudApi"),"api/instance/check/status/v2"), header,arrays.toJSONString());
    } catch (IOException e) {
        e.printStackTrace();
    }
    JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
    if(parse.getString("code").equals("200")){
        JSONArray  array =  parse.getJSONArray("data");
        JSONObject  JSON =  array.getJSONObject(0);
        Integer  state = JSON.getInteger("state");
        if(state.intValue() == 1){ // ecs为running状态
            return true;
        }
    }else if(parse.getString("message").indexOf("Token has expired")>-1){
        StreamSets s = new StreamSets();
        s.setUuid(streamSets.getUuid());
        s.setInstallState("EXPIRED");
        s.setUpdatedAt(new Date());
        streamSetsService.updateStreamSets(s);
    }
    return false;
    }

    public  void   installStreamSets(StreamSets  streamSets){
        json.put("name", "cloud_task"+System.currentTimeMillis());
        JSONObject constants = new  JSONObject();
        constants.put("${IP}",streamSets.getHost());
        constants.put("${ZONE}",streamSets.getAreaId());
        json.put("constants",constants);
        json.put("template_source","common");
        String taskId= null;
        JSONObject parse = null;
        String  requestBack = null;
        try {
            // 创建任务
            requestBack = HttpClient.post(String.format(json.getString("dopUrl"),"api/c/compapi/v2/sops/create_task/"), json.toJSONString());
            parse = (JSONObject) JSONObject.parse(requestBack);
            taskId = parse.getJSONObject("data").getString("task_id");
            if(!StringUtils.isEmpty(taskId)){
                json.put("name", "cloud_task_"+System.currentTimeMillis());
                json.put("task_id", taskId);
                //执行任务
                requestBack = HttpClient.post(String.format(json.getString("dopUrl"),"api/c/compapi/v2/sops/start_task/"), json.toJSONString());
                parse = (JSONObject) JSONObject.parse(requestBack);

                if(parse.getBoolean("result")){ // 执行成功
                    SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                    String  dataStr = data.format(new Date());
                    streamSets.setTaskId(Integer.parseInt(taskId));
                    streamSets.setInstallState("CREATED");// 提交完毕！
                    streamSets.setUpdatedAt(new Date());
                    streamSetsService.updateStreamSets(streamSets);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
    try {
         streamSetsService.getSingleFlag();
        }catch (Exception e){
        return;
        }

        List<StreamSets> list = null;
        try {
            list = streamSetsService.getInstallStreamSetsList("'INIT','CREATED','RUNNING'");
        }catch (Exception e) {
            e.printStackTrace();
        }
        for (StreamSets streamSets:list) {
            if(streamSets.getInstallState().equals("INIT")){ // ECS需要监控状态的
                try{
                   if(getECSState(streamSets)){  //  为running 状态，查询IP
                            JSONObject jsonObject= getEcsHost(streamSets.getInstancesId(),streamSets.getRegion(),streamSets.getTicket());
                            String ip = jsonObject.getString("ip");
                            if(null!=ip){
                                // 注册dop
                                Integer hostId =  streamSetsService.searchHostId(ip);
                                if(null == hostId){
                                    ResponseResult result =  streamSetsService.addHostToResource(ip);
                                    hostId =  streamSetsService.searchHostId(ip);
                                }else{
                                    // 删除host
                                    StreamSets  tmp = new  StreamSets();
                                    tmp.setHostId(hostId);
                                    tmp.setHost(ip);
                                    streamSetsService.transferHostToIdlemodule(tmp.getHostId());
                                    streamSetsService.transferHostToResourcemodule(tmp.getHostId());
                                    streamSetsService.deleteHost(tmp);
                                    //再增加
                                    ResponseResult result =  streamSetsService.addHostToResource(ip);
                                    hostId =  streamSetsService.searchHostId(ip);
                                }
                                streamSetsService.transferHostToIdlemodule(hostId);
                            //更新streamsets IP信息
                                streamSets.setHost(ip);
                                streamSets.setHostId(hostId);
                                streamSets.setVcpus(jsonObject.getInteger("vcpus"));
                                streamSets.setMemoryMb(jsonObject.getInteger("ram"));
                                streamSets.setUpdatedAt(new Date());
                                streamSetsService.updateStreamSets(streamSets);
                                if(streamSetsService.getAgentStatus(streamSets)) {
                                    installStreamSets(streamSets);
                                    //开始创建安装部署任务
                                }
                            }

                   }
                }catch (Exception e){
                    e.printStackTrace();
                    }




            }else if("CREATED||RUNNING".contains(streamSets.getInstallState())){  // 查询安装状态！

                json.put("task_id", streamSets.getTaskId());
                try {
                    String requestBack = com.gwm.cloudcommon.util.HttpClient.post(String.format(json.getString("dopUrl"),"/api/c/compapi/v2/sops/get_task_status/"), json.toJSONString());
                    JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
                    if(parse.getBooleanValue("result")){ // 查询成功
                        String state = parse.getJSONObject("data").getString("state");
                        if(!state.equals(streamSets.getInstallState())){
                            // update  实例状态，部署完毕
                            streamSets.setInstallState(state);
                            streamSets.setUpdatedAt(new Date());
                            if(state.equals("FINISHED")){ // 部署成功，服务为启动状态！
                                streamSets.setState(1);
                            }
                            if(state.equals("FAILED")&&streamSets.getRetry().intValue() <= 3 ){
                                streamSets.setInstallState("INIT");//重试
                                streamSets.setRetry(streamSets.getRetry()+1);
                            }
                            streamSetsService.updateStreamSets(streamSets);
                        }
                    }
                    LogUtil.info("查询task："+streamSets.getTaskId()+"状态！");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }

// 用完了释放锁
        try {
            streamSetsService.deleteSingleFlag();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    }

