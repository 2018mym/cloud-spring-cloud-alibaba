package com.gwm.cloudecs.schedule;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.dao.CloudRsasMapper;
import com.gwm.cloudecs.model.entity.CloudRsas;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * ecs 创建时所需要的线程类
 */
@Slf4j
public class ECSRsasTask implements TaskRunnable {

    // rsas访问的url地址 任务状态
    String rsasTaskStatus;
    // rsas访问的url地址 生成报告
    String rsasreportGenerate;
    // rsas访问的taskId
    Integer taskId;
    // 标识线程名称。业务名称
    String thName;
    // 云主机创建后所生成的uuid
    String server;

    // 强制关闭线程时间，此时间为间隔次数  时长为count*间隔时间
    int count = 30;

    CloudRsasMapper cloudRsasMapper;


    /**
     * @param name               线程名称
     * @param server             云主机的uuid
     * @param rsasTaskStatus     访问地址的url
     * @param rsasreportGenerate 访问地址的url
     * @param taskId             任务id
     */
    public ECSRsasTask(String name, String server, String rsasTaskStatus, String rsasreportGenerate, Integer taskId, CloudRsasMapper cloudRsasMapper) {
        this.thName = name;
        this.server = server;
        this.rsasTaskStatus = rsasTaskStatus;
        this.rsasreportGenerate = rsasreportGenerate;
        this.taskId = taskId;
        this.cloudRsasMapper = cloudRsasMapper;
    }


    @Override
    public String getName() {
        return thName;
    }

    @Override
    public void run() {
        try {
            String entiy1 = HttpClient.httpsGet(rsasTaskStatus);
            JSONObject parse1 = (JSONObject) JSONObject.parse(entiy1);
            Integer code1 = parse1.getInteger("ret_code");
            if (code1 != 0) {
                log.info(String.format("系统异常（获取失败）,任务id为:%s,云主机id为:%s", taskId, server));
                return;
            }
            JSONObject data1 = parse1.getJSONObject("data");
            if (8 == data1.getInteger("status")) {
                log.info(String.format("云主机扫描漏洞定时线，扫描异常停止，关闭该线程：%S", thName));
                ScheduleUtil.stop(thName);
            }
            if (4 != data1.getInteger("status")) {
                log.info(String.format("云主机扫描漏洞定时线，正在扫描中。扫描的状态为，该线程：%s状态为：%s", thName, data1.getInteger("status")));
                return;
            }
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("task_id", taskId.toString()));
            nvps.add(new BasicNameValuePair("report_type", "html"));
            String entity = HttpClient.httpsPost(rsasreportGenerate, nvps);
            JSONObject parse = (JSONObject) JSONObject.parse(entity);
            Integer code = parse.getInteger("ret_code");
            if (code != 0) {
                log.info(String.format("云主机扫描漏洞定时线，生成报表失败，该线程：%s,该taskId:%s", server, taskId));
                ScheduleUtil.stop(thName);
            }
            // 生成报表成功过。则更新库。关闭该线程
            JSONObject data = parse.getJSONObject("data");
            CloudRsas cloudRsas = cloudRsasMapper.selectByInstanceId(server);
            cloudRsas.setReportId(data.getInteger("report_id"));
            cloudRsasMapper.updateByPrimaryKey(cloudRsas);
            log.info(String.format("云主机扫描漏洞定时线，执行该任务结束，关闭该线程，线程id：%S", thName));
            ScheduleUtil.stop(thName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (count-- < 0) {
                log.info(String.format("云主机扫描漏洞定时线程因该线程超时，则强制关闭.....线程名称为:%s", thName));
                ScheduleUtil.stop(thName);
            }
        }

    }
}
