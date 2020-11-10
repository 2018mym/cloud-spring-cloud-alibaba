package com.gwm.cloudecs.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.entity.CloudOrder;

public interface WorkService {
    public ResponseResult submit(CloudOrder cloudOrder);

    public ResponseResult audit(CloudOrder cloudOrder);
}
