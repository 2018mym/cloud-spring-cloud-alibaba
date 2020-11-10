package com.gwm.cloudstreamsets.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudstreamsets.dao.StreamSetsMapper;
import com.gwm.cloudstreamsets.model.dto.StreamsetsListDTO;
import com.gwm.cloudstreamsets.model.entity.StreamSets;
import com.gwm.cloudstreamsets.service.StreamSetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class StreamSetsServiceImpl implements StreamSetsService {
	@Autowired
	private StreamSetsMapper streamSetsMapper;
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


	@Override
	public StreamSets getStreamSetsByUuid(String uuid) {
		return streamSetsMapper.selectByPrimaryKey(uuid);
	}

	@Override
	public Integer addStreamSets(StreamSets streamSets) {
		return streamSetsMapper.insert(streamSets);
	}

	@Override
	public Integer updateStreamSets(StreamSets streamSets) {
		return streamSetsMapper.update(streamSets);
	}

	@Override
	public Integer getSingleFlag() {
		       streamSetsMapper.InitSingleFlag();
		return streamSetsMapper.getSingleFlag();
	}

	@Override
	public void deleteSingleFlag() {
		 streamSetsMapper.deleteSingleFlag();
	}

	@Override
	public ResponseResult getStreamSetsList(StreamsetsListDTO streamsetsListDTO) {
		try {
			JSONObject jsonObject = new JSONObject();
			PageHelper.startPage(streamsetsListDTO.getPageNum(), streamsetsListDTO.getPageSize());
			List<StreamSets> list = streamSetsMapper.getStreamSetsList(streamsetsListDTO);
			for (StreamSets streamSets : list) {
				//JSONObject escData = HttpClient.getEcs(String.format(url, String.format("/bcp/v2/instance/status?instance_id=%s&region_name=%s", instances.getUuid(), instances.getRegion())));
				//streamSets.setState(escData.getString("status"));
			}
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
	public List<StreamSets> getInstallStreamSetsList(String installStateArray) {
		return streamSetsMapper.getInstallStreamSetsList(installStateArray);
	}

	@Override
	public Integer searchHostId(String host) {
		JSONObject ip = new JSONObject();
		JSONArray array= new JSONArray();
		array.add(host);
		ip.put("data",array);
		ip.put("exact",1);
		ip.put("flag","bk_host_innerip");
		JSONObject json = getPubParameter();
		json.put("ip",ip);
		Integer bkHostId = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/search_host/"), json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				JSONArray info  =  parse.getJSONObject("data").getJSONArray("info");
				if(null!=info && !info.isEmpty()) {
					bkHostId = info.getJSONObject(0).getJSONObject("host").getInteger("bk_host_id");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return bkHostId;
	}

	@Override
	public Boolean getAgentStatus(StreamSets streamSets) {
		JSONObject host = new JSONObject();
		host.put("ip",streamSets.getHost());
		host.put("bk_cloud_id",0);
		JSONObject json = getPubParameter();
		 json.put("bk_supplier_account","0");
		JSONArray array= new JSONArray();
		array.add(host);
		json.put("hosts",array);
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/gse/get_agent_status/"), json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				String regex = "\\\"bk_agent_alive\":(.+?)\\}";
				Matcher matcher = Pattern.compile(regex).matcher(requestBack);
				while (matcher.find()) {
					String ret = matcher.group(1);
					if (Integer.parseInt(ret.trim()) == 1) {
						return true;
					}
					return false;
				}
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public ResponseResult transferHostToIdlemodule(Integer hostId) {
		JSONObject json = getPubParameter();
			JSONArray array= new JSONArray();
			array.add(hostId);
		json.put("bk_host_id",array);
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/transfer_host_to_idlemodule/"), json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public ResponseResult transferHostToIdlemodule(Integer bizId,Integer hostId) {
		JSONObject json = getPubParameter();
		json.put("bk_biz_id",String.valueOf(bizId));
		JSONArray array= new JSONArray();
		array.add(hostId);
		json.put("bk_host_id",array);
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/transfer_host_to_idlemodule/"), json.toJSONString());
			log.debug(requestBack);
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public ResponseResult transferHostToResourcemodule(Integer hostId) {
		JSONObject json = getPubParameter();
		JSONArray array= new JSONArray();
		array.add(hostId);
		json.put("bk_host_id",array);
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/transfer_host_to_resourcemodule/"), json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public ResponseResult transferHostToResourcemodule(Integer bizId,Integer hostId) {
		JSONObject json = getPubParameter();
		json.put("bk_biz_id",String.valueOf(bizId));
		JSONArray array= new JSONArray();
		array.add(hostId);
		json.put("bk_host_id",array);
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/transfer_host_to_resourcemodule/"), json.toJSONString());
			log.debug(requestBack);
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}


	@Override
	public ResponseResult addHostToResource(String ip) {
		JSONObject json = getPubParameter();
		Map array = new HashMap();
		JSONObject ipObject = new JSONObject();
		ipObject.put("bk_host_innerip",ip);
		ipObject.put("bk_cloud_id",0);
		array.put("0",ipObject);
		json.put("host_info",array);
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/add_host_to_resource/"), json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public ResponseResult deleteHost(StreamSets streamSets) {

		Integer  hostId = searchHostId(streamSets.getHost());
		if(null == hostId){
			return  ResponseResult.succ();
		}

		JSONObject json = getPubParameter();
		json.put("bk_host_id",streamSets.getHostId());
		ResponseResult state = null;
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/delete_host/"), json.toJSONString());
			log.debug(requestBack);
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
				//逻辑删除
				streamSetsMapper.update(streamSets);
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	public ResponseResult deleteHost(StreamSets streamSets,String bk_biz_id) {
		JSONObject json = getPubParameter();
		json.put("bk_biz_id",bk_biz_id);
		json.put("bk_host_id",streamSets.getHostId());
		ResponseResult state = null;
		log.debug(json.toJSONString());
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/delete_host/"), json.toJSONString());
			log.debug(requestBack);
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				state = ResponseResult.succ();
			}else{
				state = ResponseResult.error(parse.getString("message"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public ResponseResult deleteInstances(StreamSets streamSets) {
		String requestBack = null;
		try {
			Map<String, String> header = new HashMap<String, String>();
			header.put("ticket",streamSets.getTicket());
			JSONObject json = new JSONObject();
			json.put("instanceId",streamSets.getInstancesId());
			json.put("region",streamSets.getRegion());
			requestBack=  HttpClient.post(String.format(cloudApi,"api/instance/delete/v2"),header,json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if(parse.getString("code").equals("200")){
				return  ResponseResult.succ();
			}else{
				return ResponseResult.error(parse.getString("message"));
			}

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseResult.error(e.getMessage());
		}
	}

	@Override
	public JSONObject searchHost(String host) {
		JSONObject  hosts= new  JSONObject();
		JSONObject ip = new JSONObject();
		JSONArray array= new JSONArray();
		array.add(host);
		ip.put("data",array);
		ip.put("exact",1);
		ip.put("flag","bk_host_innerip");
		JSONObject json = getPubParameter();
		json.remove("bk_biz_id");
		json.put("ip",ip);

		JSONObject condition  = new JSONObject();
		condition.put("bk_obj_id","biz");
		condition.put("fields",new ArrayList());
		condition.put("condition",new ArrayList());
		JSONArray conditionArray= new JSONArray();
		conditionArray.add(condition);
		json.put("condition",conditionArray);
	log.debug(json.toJSONString());
		try {
			String requestBack = HttpClient.post(String.format(dopUrl, "/api/c/compapi/v2/cc/search_host/"), json.toJSONString());
			JSONObject parse = (JSONObject) JSONObject.parse(requestBack);
			if (parse.getBooleanValue("result")) { // 查询成功
				JSONArray info  =  parse.getJSONObject("data").getJSONArray("info");
				if(null!=info && !info.isEmpty()) {
					Integer bkHostId = info.getJSONObject(0).getJSONObject("host").getInteger("bk_host_id");
					Integer bkBizId =  info.getJSONObject(0).getJSONArray("biz").getJSONObject(0).getInteger("bk_biz_id");
					hosts.put("bkHostId",bkHostId);
					hosts.put("bkBizId",bkBizId);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return hosts;
	}
	@Override
	public ResponseResult deleteHostFromDOP(String ip){
			JSONObject host = this.searchHost(ip);
			Integer hostId =  host.getInteger("bkHostId");
			if(hostId == null){
				return  ResponseResult.succ("主机不存在！");
			}
			// 删除host
			StreamSets tmp = new StreamSets();
			tmp.setHostId(hostId);
			tmp.setHost(ip);
			this.transferHostToIdlemodule(host.getInteger("bkBizId"),tmp.getHostId());
			this.transferHostToResourcemodule(host.getInteger("bkBizId"),tmp.getHostId());
			return  this.deleteHost(tmp,host.getString("bkBizId"));
	}

	@Override
	public ResponseResult registersHostToDOP(String ip) {
		JSONObject host = this.searchHost(ip);
		Integer hostId =  host.getInteger("bkHostId");
		if(null == hostId){
			ResponseResult result =  this.addHostToResource(ip);
			hostId =  this.searchHostId(ip);
		}else{
			// 删除host
			StreamSets tmp = new StreamSets();
			tmp.setHostId(hostId);
			tmp.setHost(ip);
			this.transferHostToIdlemodule(host.getInteger("bkBizId"),tmp.getHostId());
			this.transferHostToResourcemodule(host.getInteger("bkBizId"),tmp.getHostId());
			this.deleteHost(tmp,host.getString("bkBizId"));
			//再增加
			ResponseResult result =  this.addHostToResource(ip);
			hostId =  this.searchHostId(ip);
		}
		return 	this.transferHostToIdlemodule(hostId);
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
