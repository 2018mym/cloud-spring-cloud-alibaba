package com.gwm.cloudgateway.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class UserVo {
    // 用户ID
    String userId;
    // 用户缩写
    String userName;
    // 用户真实名称
    String nickName;
    // 邮箱
    String email;
    //用户组 id
    String groupId;
    // 用户组名称
    String groupName;
    // 创建时间
    Date createdAt;
    // 更新时间
    Date updatedat;
    // 权限判断结果
    boolean haspower;


}
