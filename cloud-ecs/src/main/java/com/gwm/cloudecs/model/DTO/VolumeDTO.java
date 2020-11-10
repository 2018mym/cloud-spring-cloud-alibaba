package com.gwm.cloudecs.model.DTO;

import com.alibaba.fastjson.JSONObject;
import com.gwm.cloudecs.model.entity.Volume;

import java.util.Date;
import java.util.List;

/**
 * 创建卷的DTO
 */
public class VolumeDTO extends Volume {
//    //卷的名称
//    String volumeName;
//    // 卷的大小，单位GB
//    Integer size;
//    // 卷的所在zone
//    String availabilityZone;
//    // 卷所在的region
//    String regionName;
    // 快照的id，选填参数 不能和hint参数同时使用
    String snapshot;
    String ticket;
//    // 卷调度相关的，参数，在创建虚拟机时，如果选择了卷，则需要传入该参数，不能和snapshot 参数同时使用
    JSONObject hint;
    // 标记使用hint参数还是snapshot参数
    String flag;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public JSONObject getHint() {
        return hint;
    }

    public void setHint(JSONObject hint) {
        this.hint = hint;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

}