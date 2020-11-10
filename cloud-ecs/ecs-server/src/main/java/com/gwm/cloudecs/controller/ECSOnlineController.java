package com.gwm.cloudecs.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.config.GlobalVarConfig;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ECSOnlineController {
        public static void main(String[] args) {
            // Create and initialize a DefaultAcsClient instance
            DefaultProfile profile = DefaultProfile.getProfile(
                    "cn-beijing",          // The region ID
                    "LTAI4GBGmkd5yuojsua4oK1K",      // The AccessKey ID of the RAM account
                    "PzSgLCPo7TSk52YXiaukJXHxXJte1W"); // The AccessKey Secret of the RAM account
            // 创建并初始化DefaultAcsClient实例。
            IAcsClient  client = new DefaultAcsClient(profile);

// 查询磁盘类型

//            try {
//                DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
//                request.setRegionId("cn-beijing");
//                request.setZoneId("cn-beijing-j");
//                request.setDestinationResource("DataDisk");
//                request.setResourceType("disk");
//                DescribeAvailableResourceResponse response = client.getAcsResponse(request);
//                System.out.println(response);
//                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
////                JSONArray availableZones = jsonObject.getJSONArray("availableZones");
////                JSONArray collect = availableZones.stream()
////                        .filter(json -> "cn-beijing-h".equals(((JSONObject) json).getString("zoneId")) ||
////                                "cn-beijing-a".equals(((JSONObject) json).getString("zoneId"))
////                                || "cn-beijing-j".equals(((JSONObject) json).getString("zoneId")))
////                        .collect(Collectors.toCollection(JSONArray::new));
////                jsonObject.put("availableZones", collect);
//                System.out.println(jsonObject);
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }

// 创建云磁盘
//            CreateDiskRequest request = new CreateDiskRequest();
//            request.setRegionId("cn-beijing");
//            request.setZoneId("cn-beijing-h");
//            request.setDiskName("testttttt55");
//            request.setSize(20);
//            request.setDiskCategory("cloud_efficiency");
//
//            try {
//                CreateDiskResponse response = client.getAcsResponse(request);
//                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }
// 获取磁盘详情
//            DescribeDisksRequest request = new DescribeDisksRequest();
//            request.setRegionId("cn-beijing");
//            request.setDiskIds("[\"d-2ze51b0olodqqy8gp0in\"]");
//
//            try {
//                DescribeDisksResponse response = client.getAcsResponse(request);
//                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }



//  删除云主机
            // 创建API请求并设置参数。
            DeleteInstanceRequest request = new DeleteInstanceRequest();
            // 设置一个地域ID。
            request.setRegionId("cn-beijing");
            // 指定一个实例ID。
            request.setInstanceId("i-2ze3ffnpp8kw7c7k4sw3");

            try {
                // 发起请求并处理应答或异常。
                DeleteInstanceResponse response = client.getAcsResponse(request);
                System.out.println(new Gson().toJson(response));
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                System.out.println("ErrCode:" + e.getErrCode());
                System.out.println("ErrMsg:" + e.getErrMsg());
                System.out.println("RequestId:" + e.getRequestId());
            }


// 规格

//                DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
//                request.setRegionId("cn-beijing");
//                request.setDestinationResource("InstanceType");
//                request.setZoneId("cn-beijing-j");

//                try {
//                    DescribeAvailableResourceResponse response = client.getAcsResponse(request);
//                    JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
//                    JSONArray availableZones = jsonObject.getJSONArray("availableZones");
//                    JSONObject availableZone = availableZones.getJSONObject(0);
//                    JSONArray availableResources = availableZone.getJSONArray("availableResources");
//                    JSONObject availableResource = availableResources.getJSONObject(0);
//                    JSONArray supportedResources = availableResource.getJSONArray("supportedResources");
//                    List<String> instanceTypesList = supportedResources.stream().filter(json -> "Available".equals(((JSONObject) json).getString("status")))
//                            .map(json -> ((JSONObject) json).getString("value"))
//                            .collect(Collectors.toList());
//
//
//
//                    JSONArray jsonArray = new JSONArray();
//                    for (int i = 0; i < instanceTypesList.size(); i=i+10) {
//                        DescribeInstanceTypesRequest request2 = new DescribeInstanceTypesRequest();
//                        request2.setRegionId("cn-beijing");
//                        List<String> strings = new ArrayList<>();
//                        if (i+10 > instanceTypesList.size()) {
//                            strings = instanceTypesList.subList(i, instanceTypesList.size());
//                        } else {
//                            strings = instanceTypesList.subList(i, i + 10);
//                        }
//
//                        request2.setInstanceTypess(strings);
//                        DescribeInstanceTypesResponse response2 = client.getAcsResponse(request2);
//                        JSONObject jsonObject2 = JSONObject.parseObject(new Gson().toJson(response2));
//                        JSONArray instanceTypes = jsonObject2.getJSONArray("instanceTypes");
////                        System.out.println(i + ":   " + strings.size()+"  :" +strings+ "    :    "+ instanceTypes);
////                        jsonArray.add(instanceTypes);
//                        jsonArray.addAll(instanceTypes);
//                    }
////                    System.out.println(jsonObject);
////                    System.out.println(instanceTypesList.size());
////                    System.out.println(instanceTypesList);
//                    System.out.println(jsonArray);
//
//
//
//
//
//
//
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }



//            // 创建API请求并设置参数。
//            DeleteInstanceRequest request = new DeleteInstanceRequest();
//            // 设置一个地域ID。
//            request.setRegionId("cn-beijing");
//            // 指定一个实例ID。
//            request.setInstanceId("i-2ze67c9xwcmn1r4n3d61");
//
//            try {
//                // 发起请求并处理应答或异常。
//                DeleteInstanceResponse response = client.getAcsResponse(request);
//                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }

//            // 详情
//            DescribeInstancesRequest request = new DescribeInstancesRequest();
//            request.setRegionId("cn-beijing");
//            request.setInstanceIds("[\"i-2zegptd6bqakpiz7ar2x\"]");
//
//            try {
//                DescribeInstancesResponse response = client.getAcsResponse(request);
//                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }

 // 云主机状态
//            DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
//            request.setRegionId("cn-beijing");
//
//            List<String> instanceIdList = new ArrayList<String>();
//            instanceIdList.add("i-2ze5a6x6saikaj2lbx4i");
//            request.setInstanceIds(instanceIdList);
//
//            try {
//                DescribeInstanceStatusResponse response = client.getAcsResponse(request);
//                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }
            // ecs create
//
            // 创建API请求，并设置参数。
//            RunInstancesRequest request = new RunInstancesRequest();
//            request.setRegionId("cn-beijing");
//            request.setImageId("win2016_1607_x64_dtc_zh-cn_40G_alibase_20201015.vhd");
//            request.setInstanceType("ecs.g6.6xlarge");
//            request.setSecurityGroupId("sg-2zefshqau3s97db3y89h");
//            request.setVSwitchId("vsw-2zen586krtgholu99pczy");
//            request.setInstanceName("MyFirstEcsInstance");
//            request.setDescription("MyFirstEcsInstance");
////            request.setInternetMaxBandwidthOut(2);
//            request.setInternetChargeType("PayByTraffic");
//            request.setClientToken(UUID.randomUUID().toString());
////            request.setZoneId("cn-beijing-g");
//            // 添加一块数据盘，数据盘类型为SSD云盘，容量为100GiB，并开启了随ECS实例释放。
//            List<RunInstancesRequest.DataDisk> dataDiskList = new ArrayList<RunInstancesRequest.DataDisk>();
//
//            RunInstancesRequest.DataDisk dataDisk1 = new RunInstancesRequest.DataDisk();
//            dataDisk1.setSize(50);
//            dataDisk1.setCategory("cloud_efficiency");
//            dataDisk1.setDeleteWithInstance(true);
//            dataDiskList.add(dataDisk1);
//            request.setDataDisks(dataDiskList);
//            // 批量创建五台ECS实例，如果不设置该参数，默认创建一台ECS实例。
//            // request.setAmount(5);
//            // 如果缺少库存可以接受的最低创建数量。
//            // request.setMinAmount(2);
//
//            List<RunInstancesRequest.Tag> tagList = new ArrayList<RunInstancesRequest.Tag>();
//
//            RunInstancesRequest.Tag tag1 = new RunInstancesRequest.Tag();
////            tag1.setKey("EcsProduct");
////            tag1.setValue("DocumentationDemo");
////            tagList.add(tag1);
////            request.setTags(tagList);
//            // 打开预检参数功能，不会实际创建ECS实例，只检查参数正确性、用户权限或者ECS库存等问题。
//            // 实际情况下，设置了DryRun参数后，Amount必须为1，MinAmount必须为空，您可以根据实际需求修改代码。
////             request.setDryRun(true);
//            request.setInstanceChargeType("PostPaid");
//
//            // 发起请求并处理返回或异常。
//            RunInstancesResponse response;
//            try {
//                response = client.getAcsResponse(request);
//
//                System.out.println(JSON.toJSONString(response));
//
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }
//



//   获取zone
//            try {
//
//                DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
//                request.setRegionId("cn-beijing");
//                request.setDestinationResource("Zone");
//                DescribeAvailableResourceResponse response =client.getAcsResponse(request);
//                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
//                JSONArray availableZones = jsonObject.getJSONArray("availableZones");
//                JSONArray collect = availableZones.stream()
//                        .filter(json -> "cn-beijing-h".equals(((JSONObject) json).getString("zoneId")) ||
//                       "cn-beijing-a".equals(((JSONObject) json).getString("zoneId"))
//                        || "cn-beijing-j".equals(((JSONObject) json).getString("zoneId")))
//                        .collect(Collectors.toCollection(JSONArray::new));
////                System.out.println(collect);
//                jsonObject.put("availableZones", collect);
////                List<String> instanceTypesList = supportedResources.stream()
////                .filter(json -> "Available".equals(((JSONObject) json).getString("status")))
//////                            .map(json -> ((JSONObject) json).getString("value"))
//////                            .collect(Collectors.toList());
//                System.out.println(jsonObject);
////                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }



            // ----------- 获取regionId

//            IAcsClient client = new DefaultAcsClient(profile);
//            DescribeRegionsRequest request = new DescribeRegionsRequest();
////            request.setRegionId("cn-hangzhou");
//            try {
//                DescribeRegionsResponse response = client.getAcsResponse(request);
//                JSONObject jsonObject = JSONObject.parseObject(new Gson().toJson(response));
//                JSONArray collect = jsonObject.getJSONArray("regions").stream()
//                        .filter(json -> "cn-beijing".equals(((JSONObject) json).getString("regionId")) || "cn-hangzhou".equals(((JSONObject) json).getString("regionId")))
//                        .collect(Collectors.toCollection(JSONArray::new));
//                jsonObject.put("regions", collect);
//                System.out.println(jsonObject);
////                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }

            ///------------获取云主机列表
//            // Create an API request and set parameters
//            DescribeInstancesRequest request = new DescribeInstancesRequest();
//            request.setPageSize(10);
//            // Initiate the request and handle the response or exceptions
//            DescribeInstancesResponse response;
//            try {
//                response = client.getAcsResponse(request);
//                for (DescribeInstancesResponse.Instance instance:response.getInstances()) {
//                    System.out.println(instance);
//                }
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                e.printStackTrace();
//            }


//            // 获取云主机disk
//            DescribeDisksRequest request = new DescribeDisksRequest();
//            request.setRegionId("cn-beijing");
//            request.setInstanceId("i-2zea8fay6vky9018rwn0");
//
//            try {
//                DescribeDisksResponse response = client.getAcsResponse(request);
//                System.out.println(new Gson().toJson(response));
//            } catch (ServerException e) {
//                e.printStackTrace();
//            } catch (ClientException e) {
//                System.out.println("ErrCode:" + e.getErrCode());
//                System.out.println("ErrMsg:" + e.getErrMsg());
//                System.out.println("RequestId:" + e.getRequestId());
//            }
        }





}
