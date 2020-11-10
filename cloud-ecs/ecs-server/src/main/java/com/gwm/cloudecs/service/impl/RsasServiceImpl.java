package com.gwm.cloudecs.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.SSLClient;
import com.gwm.cloudecs.common.model.entity.Instances;
import com.gwm.cloudecs.dao.CloudRsasMapper;
import com.gwm.cloudecs.dao.InstancesMapper;
import com.gwm.cloudecs.model.entity.CloudRsas;
import com.gwm.cloudecs.schedule.ECSRsasTask;
import com.gwm.cloudecs.schedule.ScheduleUtil;
import com.gwm.cloudecs.service.RsasService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RsasServiceImpl implements RsasService {

    @Autowired
    CloudRsasMapper cloudRsasMapper;

    @Autowired
    InstancesMapper instancesMapper;

    @Value("${rsas.url}")
    private String rsasUrl;

    @Value("${rsas.username}")
    private String rsasUserName;

    @Value("${rsas.password}")
    private String rsasPassword;

    @Value("${rsas.report.generate}")
    private String rsasreportGenerate;

    @Value("${rsas.report.download}")
    private String rsasreportDownload;

    @Value("${rsas.report.status}")
    private String rsasreportStatus;

    @Value("${rsas.task.create}")
    private String rsasTaskCreate;

    @Value("${rsas.task.status}")
    private String rsasTaskStatus;


    @Override
    public synchronized ResponseResult scanInstance(String instanceId) {
        CloudRsas cloudRsas = cloudRsasMapper.selectByInstanceId(instanceId);
        try {
            // 是否正在生成中
            if (null != cloudRsas && cloudRsas.getTaskId() != null) {
                // 任务状态
                String entiy1 = HttpClient.httpsGet(String.format(rsasUrl + rsasTaskStatus + cloudRsas.getTaskId() + "?username=%s&password=%s", rsasUserName, rsasPassword));
                JSONObject parse1 = (JSONObject) JSONObject.parse(entiy1);
                Integer code1 = parse1.getInteger("ret_code");
                if (code1 != 0) {
                    log.info(String.format("系统异常（获取失败）,任务id为:%s,云主机id为:%s", cloudRsas.getTaskId(), instanceId));
                    throw new CommonCustomException(-1, "系统异常（获取失败）");
                }
                JSONObject data1 = parse1.getJSONObject("data");
                // 如果任务状态是扫描失败，则进行此次扫描
                if (data1.getInteger("status") != 8) {
                    if (0 == data1.getInteger("status") ||
                            2 == data1.getInteger("status") ||
                            data1.getInteger("status") == 5) {
                        return ResponseResult.error(-1, "该云主机正在进行扫描，请耐心等待。");
                    }
                    // 如果状态为4但是report_id为空则也进行重新扫描
                    if (cloudRsas.getReportId() != null && cloudRsas.getReportId() != 0) {
                        // 报表状态
                        String entiy = HttpClient.httpsGet(String.format(rsasUrl + rsasreportStatus + cloudRsas.getReportId() + "?username=%s&password=%s", rsasUserName, rsasPassword));
                        JSONObject parse = (JSONObject) JSONObject.parse(entiy);
                        Integer code = parse.getInteger("ret_code");
//                        if (code != 0) {
//                            log.info(String.format("没有相关报表信息,报表id为:%s,云主机id为:%s", cloudRsas.getReportId(), instanceId));
//                            throw new CommonCustomException(-1, "没有相关报表信息");
//                        }
                        JSONObject data = parse.getJSONObject("data");
                        if (0 == data.getInteger("status") && code == 0) {
                            return ResponseResult.error(-1, "该云主机扫描报表正在生成中，请耐心等待");
                        }
                    }

                }
            }

            Instances instances = instancesMapper.selectByUuid(instanceId);
            // 准备进行扫描
            String yyyyMMddHHmmss = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("name", String.format("%s-%s", instances.getIpAddr(), yyyyMMddHHmmss)));
            nvps.add(new BasicNameValuePair("targets", instances.getIpAddr()));
            nvps.add(new BasicNameValuePair("template", "1"));
            String entiy1 = HttpClient.httpsPost(String.format(rsasUrl + rsasTaskCreate + "?username=%s&password=%s", rsasUserName, rsasPassword), nvps);
            JSONObject parse1 = (JSONObject) JSONObject.parse(entiy1);
            Integer code1 = parse1.getInteger("ret_code");
            if (code1 != 0) {
                log.info(String.format("创建扫描任务失败,云主机id为:%s", instanceId));
                throw new CommonCustomException(-1, "创建扫描任务失败");
            }
            JSONObject data1 = parse1.getJSONObject("data");
            Integer taskId = data1.getInteger("task_id");
            if (cloudRsas == null) {
                cloudRsasMapper.insert(new CloudRsas() {{
                    setInstanceId(instanceId);
                    setTaskId(taskId);
                }});
            } else {
                cloudRsas.setTaskId(taskId);
                cloudRsas.setReportId(null);
                cloudRsasMapper.updateByPrimaryKey(cloudRsas);
            }
            // 启动线程定时监控，并获取实时状态。
            String rsasTaskStatus1 = String.format(rsasUrl + rsasTaskStatus + taskId + "?username=%s&password=%s", rsasUserName, rsasPassword);
            String rsasreportGenerate1 = String.format(rsasUrl + rsasreportGenerate + "?username=%s&password=%s", rsasUserName, rsasPassword);
            log.info(String.format("云主机扫描漏洞定时线，开始执行该任务，线程id：%S", String.format("ecs-rsas-%s", instanceId)));
            ScheduleUtil.stard(new ECSRsasTask(String.format("ecs-rsas-%s", instanceId), instanceId, rsasTaskStatus1, rsasreportGenerate1, taskId, cloudRsasMapper), 10, 20, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonCustomException(-1, e.getMessage());
        }
        return ResponseResult.succ();
    }

    @Override
    public void reportInstance(String instanceId, HttpServletResponse response) throws IOException {
        CloudRsas cloudRsas = cloudRsasMapper.selectByInstanceId(instanceId);
        CloseableHttpResponse httpResponse = null;
        try {
            if (null == cloudRsas || cloudRsas.getTaskId() == null) {
                render(response, -1, "该云主机没有漏洞扫描报告。请先进行漏洞扫描报告", null);
                return;
            }
            String entiy1 = HttpClient.httpsGet(String.format(rsasUrl + rsasTaskStatus + cloudRsas.getTaskId() + "?username=%s&password=%s", rsasUserName, rsasPassword));
            JSONObject parse1 = (JSONObject) JSONObject.parse(entiy1);
            Integer code1 = parse1.getInteger("ret_code");
            if (code1 != 0) {
                log.info(String.format("系统异常（获取失败）,任务id为:%s,云主机id为:%s", cloudRsas.getTaskId(), instanceId));
                throw new CommonCustomException(-1, "系统异常（获取失败）");
            }
            JSONObject data1 = parse1.getJSONObject("data");
            if (8 == data1.getInteger("status")) {
                throw new CommonCustomException(-1, "该云主机扫描异常停止，请重新扫描");
            }
            if (0 == data1.getInteger("status") ||
                    2 == data1.getInteger("status") ||
                    data1.getInteger("status") == 5) {
                render(response, -1, "该云主机扫描中，请耐心等待", null);
                return;
            }
            if (cloudRsas.getReportId() == null) {
                render(response, -1, "该云主机扫描报表正在生成中，请耐心等待", null);
                return;
            }
            if (cloudRsas.getReportId() == 0) {
                render(response, -1, "上次报表生成异常，请重新扫描", null);
                return;
            }
            String entiy = HttpClient.httpsGet(String.format(rsasUrl + rsasreportStatus + cloudRsas.getReportId() + "?username=%s&password=%s", rsasUserName, rsasPassword));
            JSONObject parse = (JSONObject) JSONObject.parse(entiy);
            Integer code = parse.getInteger("ret_code");
            if (code != 0) {
                log.info(String.format("没有相关报表信息,报表id为:%s,云主机id为:%s", cloudRsas.getReportId(), instanceId));
                throw new CommonCustomException(-1, "没有相关报表信息");
            }
            JSONObject data = parse.getJSONObject("data");
            if (0 == data.getInteger("status")) {
                render(response, -1, "该云主机扫描报表正在生成中，请耐心等待", null);
            }
            // https
            CloseableHttpClient httpclient = new SSLClient();
            HttpGet httpget = new HttpGet((String.format(rsasUrl + rsasreportDownload + "?username=%s&password=%s", cloudRsas.getReportId(), "html", rsasUserName, rsasPassword)));
            httpResponse = httpclient.execute(httpget);
            // 获得响应的实体对象
            HttpEntity entity = httpResponse.getEntity();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + "报表下载.zip");
            InputStream is = entity.getContent();
            OutputStream out = response.getOutputStream();
            byte[] buffer = new byte[10 * 1024];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                out.write(buffer, 0, ch);
            }
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CommonCustomException(-1, e.getMessage());
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
        }
    }

    private void render(HttpServletResponse response, Integer code, String codeMsg, String data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");
        String str = JSON.toJSONString(new ResponseResult(code, codeMsg, data));
        out.write(str.getBytes("UTF-8"));
        out.flush();
    }
}
