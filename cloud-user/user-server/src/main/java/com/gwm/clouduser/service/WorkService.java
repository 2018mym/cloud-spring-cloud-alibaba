package com.gwm.clouduser.service;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.clouduser.model.DTO.OrderListDTO;
import com.gwm.clouduser.model.entity.CloudOrder;


public interface WorkService {
    public ResponseResult submit(CloudOrder cloudOrder);

    public ResponseResult reSubmit(CloudOrder cloudOrder);

    public ResponseResult audit(String ticket,
                                String groupId,
                                String userId,
                                CloudOrder cloudOrder);

    public ResponseResult getPersonList(OrderListDTO orderListDTO);

    public ResponseResult detail(String id);

    public ResponseResult reject(CloudOrder cloudOrder);


}
