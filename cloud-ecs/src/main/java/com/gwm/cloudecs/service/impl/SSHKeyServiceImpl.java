package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.QuotaException;
import com.gwm.cloudcommon.handler.QuotaHandle;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.dao.KeyPairsMapper;
import com.gwm.cloudecs.model.DTO.KeyPairsListDTO;
import com.gwm.cloudecs.model.entity.KeyPairs;
import com.gwm.cloudecs.service.ISSHKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class SSHKeyServiceImpl implements ISSHKeyService {

    @Value("${ecs.url}")
    private String url;

    @Autowired
    private QuotaHandle quotaHandle;

    @Autowired
    KeyPairsMapper keyPairsMapper;

    public ResponseResult getSSHKeyList(KeyPairsListDTO keyPairsListDTO) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(keyPairsListDTO.getPageNum(), keyPairsListDTO.getPageSize());
        List<KeyPairs> list = keyPairsMapper.getKeyPairsList(keyPairsListDTO);
        PageInfo pageResult = new PageInfo(list);
        jsonObject.put("totalNum", pageResult.getTotal());
        jsonObject.put("list", pageResult.getList());
        return ResponseResult.succObj(jsonObject);
    }

    public ResponseResult getOneSSHKey(KeyPairs  keyParis) {
        KeyPairs  one  = keyPairsMapper.getOneKeyPairs(keyParis);
        return ResponseResult.succObj(one);
    }

    public ResponseResult createSshkeyInfo(String ticket,KeyPairs keyParis){

        // 检查配额
        quotaHandle.putQuotas(ticket, Constant.OCCUPY, "10006",1);
        JSONObject returnObject = new JSONObject();
        long timeStamp = System.currentTimeMillis(); //时间撮
        String keyName = keyParis.getName()+"-"+timeStamp;
        try {
            JSONObject jsonObject = new JSONObject() {{
                put("key_name", keyName);
                put("region_name", keyParis.getRegion());
                put("public_key", keyParis.getPublicKey());
            }};
           // JSONObject  sshKeyData = HttpClient.postEcs(String.format(url, "/bcp/v2/sshkey/create"), jsonObject.toJSONString());
            String s = HttpClient.post(String.format(url,"/bcp/v2/sshkey/create"), jsonObject.toJSONString());
            JSONObject parse = (JSONObject) JSONObject.parse(s);
            String data = parse.getString("data");
            JSONArray jsonArrays = JSONArray.parseArray(data);
            if(jsonArrays!=null) {
                for(int i=0;i< jsonArrays.size();i++){
                    JSONObject sshKeyData = jsonArrays.getJSONObject(i);
                    keyParis.setFingerprint(sshKeyData.getString("fingerprint"));
                    keyParis.setPublicKey(sshKeyData.getString("public_key"));
                    keyParis.setCreateAt(new Date());//创建时间
                    keyParis.setDeleted(0);
                    keyParis.setUuid(sshKeyData.getString("user_id"));
                    keyParis.setType("OpenStack");
                    keyParis.setKeyType("ssh");
                    keyParis.setName(keyName); //存放keyname

                    keyPairsMapper.insert(keyParis);
                    String privateKey = sshKeyData.getString("private_key");
                    if(privateKey==null) {
                        returnObject.put("privateKey",null);
                    }else{
                        returnObject.put("privateKey",privateKey);
                    }
                }

            }

        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            throw new QuotaException(-1, e.getMessage(), Constant.FREE, "10006",1);
        }
        return ResponseResult.succObj(returnObject);

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult delSshkeyInfo(String ticket,List<Integer> ids)  throws Exception {
        if(ids!=null&&ids.size()>0) {
            ids.forEach(id-> {
                KeyPairs keyParis  = keyPairsMapper.selectByPrimaryKey(id);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key_name", keyParis.getName());
                jsonObject.put("region_name", keyParis.getRegion());
                try {
                    String s =  HttpClient.post(String.format(url, "/bcp/v2/sshkey/delete"), jsonObject.toJSONString());
                    JSONObject parse = (JSONObject) JSONObject.parse(s);

                    if (parse != null) {
                            keyParis.setDeleted(1); //被删除了
                            keyParis.setDeletedAt(new Date());
                            keyPairsMapper.updateByPrimaryKey(keyParis);
                            quotaHandle.putQuotas(ticket, Constant.FREE, "10006",1);

                    }
                }catch(Exception ex){
                    ex.printStackTrace();;
                }

            });
        }
        return ResponseResult.succ();

    }



}
