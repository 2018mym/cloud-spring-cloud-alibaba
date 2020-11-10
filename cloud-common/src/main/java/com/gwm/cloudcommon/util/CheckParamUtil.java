package com.gwm.cloudcommon.util;

import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.exception.CommonEnum;
import org.springframework.util.StringUtils;

public class CheckParamUtil {


    /**
     * 检测如果是openstack类型，则region参数比传
     *
     * @param type   type 所属平台，1-VMware，2-OpenStack，3-alicloud，4-hwcloud，5-txcloud
     * @param region region 信息
     */
    public static void checkTypeRegion(String type, String region) {
        if ("2".equals(type)) {
            if (StringUtils.isEmpty(region)) {
                throw new CommonCustomException(CommonEnum.REIONNOTNULLERROR);
            }
        }
    }
}
