package com.gwm.cloudalert.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.gwm.cloudalert.dao.SendAlertSingFlag;
import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudalert.service.AlertService;
import com.gwm.cloudalert.service.DopAlertService;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Slf4j
public class SendAlertThread implements Runnable {
    private SendAlertSingFlag sendAlertSingFlag;
    private AlertService alertService;
    public  SendAlertThread(AlertService  alertService,SendAlertSingFlag sendAlertSingFlag){
        this.sendAlertSingFlag = sendAlertSingFlag;
        this.alertService = alertService;
    }



    @Override
    public void run() {
        Long lastSendTime = null;
        Long nowSendTime = null;
        try {
            List<Long> list= sendAlertSingFlag.getSendTime();
            nowSendTime =System.currentTimeMillis();
            if(list.size()>0){
                lastSendTime = list.get(0);
            }
            if(lastSendTime == null){
                lastSendTime  = nowSendTime;
            }
            //获得锁
            sendAlertSingFlag.getSingleFlag(nowSendTime);
            AlertDTO alertDTO = new AlertDTO();
            alertDTO.setStart(lastSendTime);
            alertDTO.setEnd(nowSendTime);
            alertDTO.setPageNum(1);
            alertDTO.setPageSize(1000);
            ResponseResult result = alertService.getAlarms(alertDTO);
            log.debug("lastSendTime:"+String.valueOf(lastSendTime));
            log.debug("nowSendTime:"+String.valueOf(nowSendTime));
            log.debug(result.getData().toString());
            JSONObject jSONObject = (JSONObject) result.getData();
            JSONArray  alerms =  jSONObject.getJSONArray("result");
            for (Object alerm: alerms) {
                JSONObject  tmp = (JSONObject) alerm;
                String  alarmtId = tmp.getString("id");
                String   source_time= tmp.getString("source_time");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date   sourceTime = df.parse(source_time);
                if(sourceTime.getTime()>lastSendTime){
                    lastSendTime = sourceTime.getTime();
                }
                alertService.sendDingDingMessage(alarmtId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sendAlertSingFlag.updateSendTime(lastSendTime);
            sendAlertSingFlag.deleteSingleFlag();
        }

    }
}

