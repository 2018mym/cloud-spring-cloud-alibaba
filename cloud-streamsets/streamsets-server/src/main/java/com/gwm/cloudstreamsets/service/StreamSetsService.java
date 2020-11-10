package com.gwm.cloudstreamsets.service;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudstreamsets.model.dto.StreamsetsListDTO;
import com.gwm.cloudstreamsets.model.entity.StreamSets;

import java.util.List;

public interface StreamSetsService {
	StreamSets getStreamSetsByUuid(String uuid);
	Integer addStreamSets(StreamSets streamSets);
	Integer updateStreamSets(StreamSets streamSets);
	Integer getSingleFlag();
	void deleteSingleFlag();
	ResponseResult getStreamSetsList(StreamsetsListDTO streamsetsListDTO);
	List<StreamSets> getInstallStreamSetsList(String installStateArray);
	Integer searchHostId(String Host);
	JSONObject searchHost(String Host);
	Boolean getAgentStatus(StreamSets streamSets);
	ResponseResult transferHostToIdlemodule(Integer hostId);
	ResponseResult transferHostToResourcemodule(Integer hostId);
	ResponseResult transferHostToIdlemodule(Integer bizId,Integer hostId);
	ResponseResult transferHostToResourcemodule(Integer bizId,Integer hostId);
	ResponseResult addHostToResource(String ip);
	ResponseResult deleteHost(StreamSets streamSets);
	ResponseResult deleteHost(StreamSets streamSets,String bkBizId);
	ResponseResult deleteInstances(StreamSets streamSets);
	ResponseResult registersHostToDOP(String ip);
	ResponseResult  deleteHostFromDOP(String ip);
}
