package com.gwm.cloudstreamsets.service.impl; 

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudstreamsets.model.dto.StreamsetsListDTO;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gwm.cloudstreamsets.dao.StreamSetsMapper;
import com.gwm.cloudstreamsets.model.entity.StreamSets;
import com.gwm.cloudstreamsets.service.StreamSetsService;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
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
	public Integer addStreamSets(StreamSets  streamSets) {
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
			LogUtil.error(ce.getMessage(), ce);
			return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
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
		JSONArray  array= new JSONArray();
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
				JSONArray  info  =  parse.getJSONObject("data").getJSONArray("info");
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
	public Boolean getAgentStatus(StreamSets  streamSets) {
		JSONObject host = new JSONObject();
		host.put("ip",streamSets.getHost());
		host.put("bk_cloud_id",0);
		JSONObject json = getPubParameter();
		 json.put("bk_supplier_account","0");
		JSONArray  array= new JSONArray();
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
			JSONArray  array= new JSONArray();
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
	public ResponseResult transferHostToResourcemodule(Integer hostId) {
		JSONObject json = getPubParameter();
		JSONArray  array= new JSONArray();
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
	public ResponseResult addHostToResource(String ip) {
		JSONObject json = getPubParameter();
		Map array = new HashMap();
		JSONObject  ipObject = new JSONObject();
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

	@Override
	public ResponseResult deleteInstances(StreamSets streamSets) {
		String requestBack = null;
		try {
			Map<String, String> header = new HashMap<String, String>();
			header.put("ticket",streamSets.getTicket());
			JSONObject  json = new JSONObject();
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

	public JSONObject  getPubParameter(){
		JSONObject json = new  JSONObject();
		json.put("bk_app_code", bk_app_code);
		json.put("bk_app_secret", bk_app_secret);
		json.put("bk_username", bk_username);
		json.put("bk_biz_id", bk_biz_id);
		return  json;
	}

}
