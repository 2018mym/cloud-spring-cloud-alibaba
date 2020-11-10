package com.gwm.cloudstreamsets.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.CheckParamUtil;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudstreamsets.model.dto.StreamsetsListDTO;
import com.gwm.cloudstreamsets.model.entity.StreamSets;
import com.gwm.cloudstreamsets.service.StreamSetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/streamSets")
@Slf4j
public class StreamSetsController { 

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
    @Value("${streamsets.imageUuid}")
    private String imageUuid;
    @Value("${streamsets.network}")
    private String network;

    /**
     *
     获取列表
     *
     * @return
     */
    @RequestMapping("/list/v2")
    public ResponseResult getStreamSetsList(StreamsetsListDTO streamsetsListDTO, HttpServletRequest request) {

        try {
            CheckParamUtil.checkTypeRegion(streamsetsListDTO.getType(), streamsetsListDTO.getRegion());
            String userId = request.getHeader("userId");
            String groupId = request.getHeader("groupId");
            streamsetsListDTO.setGroupId(groupId);
           // streamsetsListDTO.setUserId(userId);
            return streamSetsService.getStreamSetsList(streamsetsListDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseResult.error(e.getMessage());
        }
    }


    @RequestMapping("/delete/v2")
    public ResponseResult deleteStreamSet(@RequestParam("uuid") String uuid, HttpServletRequest request) {
        StreamSets streamSets =  streamSetsService.getStreamSetsByUuid(uuid);

        if(null == streamSets){
            return ResponseResult.error("没找到uuid为"+uuid+"的服务");
        }
        String ticket = request.getHeader("ticket");
        streamSets.setTicket(ticket);
        // 查询DOP平台是否还有
        Integer  hostid = streamSetsService.searchHostId(streamSets.getHost());
        if(null != hostid) {
            ResponseResult resultIdleModule = streamSetsService.transferHostToIdlemodule(streamSets.getHostId());
           /* if (resultIdleModule.getCode() != 200) {
                resultIdleModule.setMessage("transferHostToIdlemodule:" + resultIdleModule.getMessage());
                return resultIdleModule;
           }*/
            ResponseResult resultResourceModule = streamSetsService.transferHostToResourcemodule(streamSets.getHostId());
         /*   if (resultResourceModule.getCode() != 200) {
                resultResourceModule.setMessage("transferHostToResourcemodule:" + resultResourceModule.getMessage());
                return resultResourceModule;
            }

          */
        }
        ResponseResult resultDeleteHost  = streamSetsService.deleteHost(streamSets);
        if(resultDeleteHost.getCode() == Constant.SUCCESSCODE){
            streamSets.setDeletedAt(new Date());
            streamSets.setDeleted(1);
            streamSetsService.updateStreamSets(streamSets);
            ResponseResult result = streamSetsService.deleteInstances(streamSets);
           /* if(result.getCode() != 200){
                result.setMessage("deleteInstances:"+result.getMessage());
                return result;
            }
            */
        }
        return resultDeleteHost;
    }


    /**
     * 获取详情,和安装状态。
     */
    @RequestMapping("/details/v2")
    public StreamSets streamSetsDetails(@RequestParam("uuid") String uuid) {
        StreamSets streamSets =  streamSetsService.getStreamSetsByUuid(uuid);
    	return  streamSets;
    }


    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    public static String generateShortUUID() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    
    /**
     * 创建streamSets服务实例
     *
     */
    @RequestMapping("/create/v2")
    public ResponseResult create(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws IOException {
        String userId = request.getHeader("userId");
        String groupId = request.getHeader("groupId");
        String requestBack =  null;
        // 创建虚拟机
            JSONObject json = new JSONObject();
            json.put("name", jsonObject.getString("name"));//用户名称+8位串
            json.put("imageUuid", imageUuid);  //固定写死  或者配置
        Integer flavorId = jsonObject.getInteger("flavorId");
            if(flavorId == 0){
                flavorId = 3;
            }else if(flavorId == 1){
                flavorId = 4;
            }else if(flavorId == 2){
                flavorId = 5;
            }
            json.put("flavorId", String.valueOf(flavorId));//  0小型（3），1中型（4），2大型（5）
            json.put("region", jsonObject.getString("region"));// 前端给
            JSONArray array = new JSONArray();
            array.add("default");
            json.put("securityGroups", array);
            json.put("adminPass", "");
            json.put("keyName", "");
            json.put("zone", "lvm");
            json.put("network", network);
            json.put("serverGroup", "");
            json.put("env",jsonObject.getString("env"));// 前端给
            json.put("count", 1);//  创建多少个虚拟机
            json.put("volumes",new JSONArray());

        Map<String, String> header = new HashMap<String, String>();
        header.put("ticket",request.getHeader("ticket"));
            requestBack = HttpClient.post(String.format(cloudApi, "api/instance/create/v2"),header, json.toJSONString());
            JSONObject parse   = (JSONObject) JSONObject.parse(requestBack);
            String code = parse.getString("code");
            if(!code.equals("200")){
                return ResponseResult.error(parse.getString("message"));
            }else {
                //保存入库
                String ecsId = parse.getJSONObject("data").getJSONArray("serverList").getString(0);
                StreamSets streamSets = new StreamSets();
                String uuid = UUID.randomUUID().toString();
                streamSets.setUuid(uuid);
                streamSets.setInstancesId(ecsId);
                SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String dataStr = data.format(new Date());
                streamSets.setName(jsonObject.getString("name")+"_"+generateShortUUID().toLowerCase());
                streamSets.setAreaId(jsonObject.getInteger("areaId"));
                streamSets.setPort("18630");
                streamSets.setInstallState("INIT");// 提交完毕！
                streamSets.setType("OpenStack");
                streamSets.setAreaId(0);// 直连区域 默认是0
                streamSets.setEnv(jsonObject.getString("env"));
                streamSets.setRegion(jsonObject.getString("region"));
                streamSets.setZone(json.getString("zone"));
                streamSets.setUserId(userId);
                streamSets.setGroupId(groupId);
                streamSets.setTicket(request.getHeader("ticket"));
                streamSets.setDeleted(0);
                streamSets.setRetry(0);
                streamSets.setCreatedAt(new Date());
                streamSetsService.addStreamSets(streamSets);
                JSONObject JSON = new JSONObject();
                JSON.put("uuid",uuid);
                return ResponseResult.succObj(JSON);
                }



}}