package com.gwm.cloudalert.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudalert.dao.CloudAlertStrategyMapper;
import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudalert.model.dto.AlertTeamDTO;
import com.gwm.cloudalert.model.dto.CloudAlertStrategyDTO;
import com.gwm.cloudalert.model.entity.AlertTeamItem;
import com.gwm.cloudalert.model.entity.CloudAlertStrategy;
import com.gwm.cloudalert.service.AlertService;
import com.gwm.cloudalert.service.DopAlertService;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/alert")
@Slf4j
public class AlertController {
    @Autowired
    AlertService alertService;
    @Autowired
    DopAlertService dopAlertService;
    @Autowired
    CloudAlertStrategyMapper CloudAlertStrategyMapper;
    @RequestMapping("/queryList")
    public ResponseResult getAlertList(@RequestBody AlertDTO alert){
        Long start = alert.getStart();
        Long end = alert.getEnd();
        Long flag = start%60000;
        alert.setStart(start-flag);
        flag = end%60000;
        alert.setEnd(end-flag);
        return alertService.queryAlertList(alert);
    }


    @RequestMapping("/getAlarms")
    public ResponseResult getAlarms(@RequestBody AlertDTO alert){
        return  alertService.getAlarms(alert);
    }


    @RequestMapping("/queryAlertTeamList")
    public ResponseResult queryAlertTeamList(@RequestHeader("groupId") Integer groupId,
                                        @RequestHeader("userId") String userId,
                                        @RequestBody AlertTeamDTO alertTeamDTO){
        alertTeamDTO.setGroupId(groupId);
        alertTeamDTO.setUserId(userId);
        return alertService.queryAlertTeamList(alertTeamDTO);
    }
    @RequestMapping("/addTeamItem")
    public ResponseResult saveTeamItem(
            @RequestHeader("groupId") Integer groupId,
            @RequestHeader("userId") String userId,
            @RequestBody AlertTeamItem alertTeamItem){
        alertTeamItem.setGroupId(groupId);
        alertTeamItem.setUserId(userId);
        alertTeamItem.setCreatedAt(new Date());
        alertTeamItem.setDeleted(0);
        return alertService.addTeamItem(alertTeamItem);
    }

    @RequestMapping("/updateTeamItem")
    public ResponseResult updateTeamItem(
            @RequestHeader("groupId") Integer groupId,
            @RequestHeader("userId") String userId,
            @RequestBody AlertTeamItem alertTeamItem){
            alertTeamItem.setGroupId(groupId);
            alertTeamItem.setUserId(userId);
            alertTeamItem.setUpdatedAt(new Date());
            alertTeamItem.setDeleted(0);
            return alertService.updateTeamItem(alertTeamItem);
    }

    @RequestMapping("/deleteTeamItem")
    public ResponseResult deleteTeamItem(
            @RequestHeader("groupId") Integer groupId,
            @RequestHeader("userId") String userId,
            @RequestBody AlertTeamItem alertTeamItem){

            alertTeamItem.setGroupId(groupId);
            alertTeamItem.setUserId(userId);
            alertTeamItem.setDeletedAt(new Date());
            alertTeamItem.setDeleted(1);
        return alertService.updateTeamItem(alertTeamItem);
    }

    /*

     *   以下字段用逗号隔开，以便支持多个指标策略
      *       method
     *  "threshold": 85,                            告警百分比(阈值)
     *   "monitorType": "cpu",                    cpu（cpu利用率）、disk、mem（内存利用率）
     *   "monitorLevel": 1,                       告警级别
     * @param groupId
     * @param userId
     * @param cloudAlertStrategy
     * @return
     */
    @RequestMapping("/addAlertStrategy")
    public ResponseResult addAlertStrategy(
            @RequestHeader("groupId") Integer groupId,
            @RequestHeader("userId") String userId,
            @RequestBody CloudAlertStrategy cloudAlertStrategy){

         if(!cloudAlertStrategy.getAlertType().equals("metric")){
             return ResponseResult.error(cloudAlertStrategy.getAlertType()+" is not support!");
         }

        cloudAlertStrategy.setGroupId(groupId);
        cloudAlertStrategy.setUserId(userId);
        cloudAlertStrategy.setCreatedAt(new Date());
        cloudAlertStrategy.setDeleted(0);
        cloudAlertStrategy.setNoticeStartTime("00:00");
        cloudAlertStrategy.setNoticeEndTime("23:59");
        return alertService.addAlertStrategy(cloudAlertStrategy);
    }

    /*
      *   以下字段用逗号隔开，以便支持多个指标策略
      * method
     *  "threshold": 85,                            告警百分比(阈值)
     *   "monitorType": "cpu",                    cpu（cpu利用率）、disk、mem（内存利用率）
     *   "monitorLevel": 1,                       告警级别
     *   alarm_strategy_id    告警策略，与DOP策略ID保持一致。
     *
     */

    @RequestMapping("/updateAlertStrategy")
    public ResponseResult updateAlertStrategy(
            @RequestHeader("groupId") Integer groupId,
            @RequestHeader("userId") String userId,
            @RequestBody CloudAlertStrategy cloudAlertStrategy){
        cloudAlertStrategy.setGroupId(groupId);
        cloudAlertStrategy.setUserId(userId);
        cloudAlertStrategy.setUpdatedAt(new Date());
        cloudAlertStrategy.setDeleted(0);
        // 修改策略客户端没有带IP地址，修改DOP平台时需要带上。
        CloudAlertStrategy  tmphost = CloudAlertStrategyMapper.selectByPrimaryKey(cloudAlertStrategy.getId());
        cloudAlertStrategy.setHost(tmphost.getHost());
        return alertService.updateAlertStrategy(cloudAlertStrategy);
    }

    /**
     *    需要循环删除DOP 多个策略
     * @param groupId
     * @param userId
     * @param cloudAlertStrategy
     * @return
     */
    @RequestMapping("/deleteAlertStrategy")
    public ResponseResult deleteAlertStrategy(
            @RequestHeader("groupId") Integer groupId,
            @RequestHeader("userId") String userId,
            @RequestBody JSONObject jsonObject){
        String ids = jsonObject.getString("id");

        for (String id:ids.split(",")){
            ResponseResult  result = alertService.deleteAlertStrategy(Integer.parseInt(id));
            if(result.getCode() != 200){
                return  result;
            }
        }
        return ResponseResult.succ();
    }




    /**
     *    需要循环删除DOP 多个策略
     * @param groupId
     * @param userId
     * @param ip
     * @return
     */
    @RequestMapping("/deleteAlertStrategyByIp")
    public ResponseResult deleteAlertStrategyByIp(@RequestParam(value = "ip")String ip){
        CloudAlertStrategyDTO cloudAlertStrategy = new CloudAlertStrategyDTO();
        cloudAlertStrategy.setHost(ip);
        cloudAlertStrategy.setPageNum(1);
        cloudAlertStrategy.setPageNum(1000);
        List<CloudAlertStrategy> cloudAlertStrategyList = CloudAlertStrategyMapper.queryAlertStrategyList(cloudAlertStrategy);
        for(CloudAlertStrategy tmp:cloudAlertStrategyList){
            if(tmp.getAlarmStrategyId() == null){
                break;
            }
                    if(tmp.getHost().equals(ip)){  // 只有一个当前需要删除的ip,可以删除策略。
                        String[]  stratrgyId = tmp.getAlarmStrategyId().split(",");
                        // 解绑
                        ResponseResult unBindResult = alertService.unBindAlertStrategy(ip,tmp.getId());
                        if(unBindResult.getCode()!=Constant.SUCCESSCODE){
                            if(unBindResult.getMessage().indexOf("list index out of range")>-1){
                                log.debug("dop策略已被删除！");
                                break;
                            }
                            return  unBindResult;
                        }

                        // 删除DOP策略
                        for(String stratrgy:stratrgyId){
                            CloudAlertStrategy  stratrgyDTO= new CloudAlertStrategy();
                            stratrgyDTO.setAlarmStrategyId(stratrgy);
                            ResponseResult    result =  dopAlertService.deleteAlertStrategy(stratrgyDTO);
                            if(result.getCode() != Constant.SUCCESSCODE){
                                return  result;
                            }
                        }

                        // 删除策略
                        ResponseResult    result   =    alertService.deleteAlertStrategy(tmp.getId());
                        if(result.getCode()!=Constant.SUCCESSCODE){
                            return  result;
                        }

                    }else{  // 策略还有其它绑定的IP，则解绑当前IP
                        ResponseResult unBindResult = alertService.unBindAlertStrategy(ip,tmp.getId());
                        if(unBindResult.getCode()!=Constant.SUCCESSCODE){
                            return  unBindResult;
                        }
                    }
        }

        return ResponseResult.succ();
    }




    @RequestMapping("/queryAlertStrategyList")
    public ResponseResult queryAlertStrategyList(@RequestHeader("groupId") Integer groupId,
                                                 @RequestHeader("userId") String userId,
                                                 @RequestBody CloudAlertStrategyDTO cloudAlertStrategy){
        cloudAlertStrategy.setGroupId(groupId);
        cloudAlertStrategy.setUserId(userId);
        cloudAlertStrategy.setDeleted(0);
        return alertService.queryAlertStrategyList(cloudAlertStrategy);
    }

    @RequestMapping("/bindAlertStrategy")
    public ResponseResult bindAlertStrategy(@RequestBody JSONObject object){
        String host = object.getString("host");
        if(StringUtils.isEmpty(host)){
            return ResponseResult.error("host is is empty!");
        }else{
            // TODO: 2020/9/2  校验是否为当前业务下的主机！
        }
        boolean  state = isIp(host);
        if(!state){
            return ResponseResult.error("ip 地址校验不通过！");
        }
        String  id = object.getString("id");
        if(StringUtils.isEmpty(id)){
            return ResponseResult.error("id is is empty!");
        }
        return alertService.bindAlertStrategy(host,Integer.parseInt(id));
    }

    @RequestMapping("/unBindAlertStrategy")
    public ResponseResult unBindAlertStrategy( @RequestBody JSONObject object){
        String host = object.getString("host");
        if(StringUtils.isEmpty(host)){
            return ResponseResult.error("host is is empty!");
        }else{
            // TODO: 2020/9/2  校验是否为当前业务下的主机！
        }
        boolean  state = isIp(host);
        if(!state){
            return ResponseResult.error("ip 地址校验不通过！");
        }
        String  id = object.getString("id");
        if(StringUtils.isEmpty(id)){
            return ResponseResult.error("id is is empty!");
        }
        return alertService.unBindAlertStrategy(host,Integer.parseInt(id));
    }

    public static boolean isIp(String ip) {
        boolean b1 = ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
        return b1;
    }
}
