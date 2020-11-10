package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;

public interface NetworkService {
    /**
     * @param region
     * @return
     */
    ResponseResult networkList(String region);

    ResponseResult networkDetails(String region, String networkId);

    ResponseResult networkTopo(String region, String networkId, String groupId);

    ResponseResult subnetList(String region, String networkId);

    ResponseResult subnetDetails(String region, String subnetId);

    ResponseResult subnetListPorts(String region, String subnetId, String groupId);

    ResponseResult subnetListInstances(String region, String subnetId, String groupId);

    ResponseResult securityGroupList(String region);

    ResponseResult securityGroupInfo(String region, String securityGroupId);

    ResponseResult securityGroupRuleList(String region, String securityGroupId);

}
