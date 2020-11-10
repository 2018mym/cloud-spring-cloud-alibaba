package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.entity.CloudAccount;

import java.util.List;

public interface CloudAccountService {
    /**
     * @param type type类型
     * @param zone zone
     * @param region region
     * @return
     */
    CloudAccount getRecord(String type, String zone, String region);

    /**
     * 查询多个账号
     * @param type type 类型
     * @return
     */
    List<CloudAccount> getRecordListByType(String type, String region);

    /**
     * 获取zone区域
     * @param type
     * @param region
     * @return
     */
    ResponseResult getZone(String type, String region);
}
