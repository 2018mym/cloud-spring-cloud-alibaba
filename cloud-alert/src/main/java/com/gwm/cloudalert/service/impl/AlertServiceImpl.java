package com.gwm.cloudalert.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudalert.model.dto.AlertDTO;
import com.gwm.cloudalert.service.AlertService;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AlertServiceImpl implements AlertService {
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

    public JSONObject  getPubParameter(){
        JSONObject json = new  JSONObject();
        json.put("bk_app_code", bk_app_code);
        json.put("bk_app_secret", bk_app_secret);
        json.put("bk_username", bk_username);
        json.put("bk_biz_id", bk_biz_id);
        return  json;
    }
}
