package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.QuotaException;
import com.gwm.cloudcommon.handler.QuotaHandle;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudecs.config.GlobalVarConfig;
import com.gwm.cloudecs.dao.InstanceGroupMapper;
import com.gwm.cloudecs.model.DTO.InstanceGroupDTO;
import com.gwm.cloudecs.model.entity.InstanceGroup;
import com.gwm.cloudecs.service.ServerGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ServerGroupServiceImpl  implements ServerGroupService {
    

    @Autowired
    InstanceGroupMapper instanceGroupMapper;
    @Autowired
    private QuotaHandle quotaHandle;


    public ResponseResult createServerGroup(String ticket, InstanceGroup instanceGroup){
        // 检查配额
        quotaHandle.putQuotas(ticket, Constant.OCCUPY, "10015",1);

        JSONObject returnObject = new JSONObject();
        //long timeStamp = System.currentTimeMillis(); //时间撮
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("name", instanceGroup.getName());
                put("region_name", instanceGroup.getRegion());
                put("policy", instanceGroup.getPolicy());
            }};
            // JSONObject  sshKeyData = HttpClient.postEcs(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/sshkey/create"), jsonObject.toJSONString());
            String s = HttpClient.post(String.format(GlobalVarConfig.ecsUrl,"/bcp/v2/server_group/create"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            String data = parse.getString("data");
            JSONObject obj =  (JSONObject) JSONObject.parse(data);
            if(obj!=null) {
                    instanceGroup.setCreateAt(new Date());//创建时间
                    instanceGroup.setDeleted(0);
                    instanceGroup.setUuid(obj.getString("id"));
                    instanceGroup.setType("OpenStack");
                    instanceGroup.setName(instanceGroup.getName()); //存放name

                    instanceGroupMapper.insert(instanceGroup);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //return ResponseResult.error();
            throw new QuotaException(-1, e.getMessage(), Constant.FREE, "10015",1);
        }
        return ResponseResult.succ();

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delServerGroup(String ticket, List<Integer> ids)  throws Exception {
        if(ids!=null&&ids.size()>0) {
            ids.forEach(id-> {
                InstanceGroup instanceGroup  = instanceGroupMapper.selectByPrimaryKey(id);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("group_id", instanceGroup.getUuid());
                jsonObject.put("region_name", instanceGroup.getRegion());
                try {
                    String s =  HttpClient.post(String.format(GlobalVarConfig.ecsUrl, "/bcp/v2/server_group/delete"), jsonObject.toJSONString());
                    JSONObject parse = (JSONObject) JSONObject.parse(s);

                    if (parse != null) {
                        instanceGroup.setDeleted(1); //被删除了
                        instanceGroup.setDeletedAt(new Date());
                        instanceGroupMapper.updateByPrimaryKey(instanceGroup);
                        quotaHandle.putQuotas(ticket, Constant.FREE, "10015",1);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();;
                }

            });
        }
        return ResponseResult.succ();

    }

    public ResponseResult getServerGroupList(InstanceGroupDTO instanceGroupDTO){
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(instanceGroupDTO.getPageNum(), instanceGroupDTO.getPageSize());
        List<InstanceGroup> list = instanceGroupMapper.getInstanceGroupList(instanceGroupDTO);
        PageInfo pageResult = new PageInfo(list);
        jsonObject.put("totalNum", pageResult.getTotal());
        jsonObject.put("list", pageResult.getList());
        return ResponseResult.succObj(jsonObject);
    }


}
