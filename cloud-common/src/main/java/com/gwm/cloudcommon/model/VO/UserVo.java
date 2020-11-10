package com.gwm.cloudcommon.model.VO;

import java.util.Date;
import java.util.Map;

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


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(Date updatedat) {
        this.updatedat = updatedat;
    }

    public boolean isHaspower() {
        return haspower;
    }

    public void setHaspower(boolean haspower) {
        this.haspower = haspower;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedat=" + updatedat +
                ", haspower=" + haspower +
                '}';
    }
}
