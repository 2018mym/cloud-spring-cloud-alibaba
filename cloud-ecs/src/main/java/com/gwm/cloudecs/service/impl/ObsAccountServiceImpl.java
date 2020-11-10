package com.gwm.cloudecs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.util.DateUtil;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import com.gwm.cloudecs.dao.ObsAccountMapper;
import com.gwm.cloudecs.model.DTO.ObsAccountListDTO;
import com.gwm.cloudecs.model.entity.ObsAccount;
import com.gwm.cloudecs.service.CephService;
import com.gwm.cloudecs.service.ObsAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@Service
public class ObsAccountServiceImpl  implements ObsAccountService {

    @Autowired
    private ObsAccountMapper obsAccountMapper;
    @Resource
    private CephService cephService;

    @Value("${ecs.url}")
    private String httpIp;


    @Override
    public ResponseResult getAccountList(ObsAccountListDTO accountListDTO) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(accountListDTO.getPageNum(), accountListDTO.getPageSize());
        List<ObsAccount> list = obsAccountMapper.getObsAccountList(accountListDTO);
        PageInfo pageResult = new PageInfo(list);
        jsonObject.put("totalNum", pageResult.getTotal());
        jsonObject.put("list", pageResult.getList());
        return ResponseResult.succObj(jsonObject);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult addAccountInfo(ObsAccount account)  {
        try {
        JSONObject obsParam = new JSONObject();
        obsParam.put("display_name", account.getJobNum());
        obsParam.put("quota", account.getQuota());
        obsParam.put("enabled", true);
        obsParam.put("quota_type", "user");
         //调ceph开通账号
        //JSONObject escData = HttpClient.postEcs(String.format(httpIp,"/v1/createAK"), obsParam.toJSONString());
            JSONObject escData = HttpClient.postEcs(String.format(httpIp,"/bcp/v2/rgw/createAK"), obsParam.toJSONString());
            //String server = escData.getString("data");
        if(escData!=null) {
            account.setAccessKey(escData.getString("access_key"));
            account.setSecretKey(escData.getString("secret_key"));
            account.setQuota(escData.getInteger("quota"));
            account.setCephUser(escData.getString("user")); //cephUser

            account.setCreateAt(DateUtil.parseStr(new Date()));//创建时间
            account.setJobNum(account.getJobNum());
            account.setDeleted("0");

            obsAccountMapper.insert(account);
            return ResponseResult.succ();
        }else{
            return ResponseResult.error("调用 /v1/createAK 接口失败！");
        }

        }catch(Exception ex){
            ex.printStackTrace();
            return ResponseResult.error("创建账号失败！！！");
        }

    }

    @Override
    public ResponseResult updateCloudAccountInfo(ObsAccount account) {

        try {
            //查询出配额信息
            ObsAccount  oldEntity1 = obsAccountMapper.selectByPrimaryKey(account.getId());

            JSONObject obsParam = new JSONObject();
            obsParam.put("uid", oldEntity1.getCephUser());
            obsParam.put("update_access_key", oldEntity1.getAccessKey());
            JSONObject cephData = HttpClient.postEcs(String.format(httpIp, "/bcp/v2/rgw/updateAK"), obsParam.toJSONString());
            //String server = escData.getString("data");
            if (cephData != null) {
                oldEntity1.setUpdateAt(DateUtil.parseStr(new Date()));//更新时间
                oldEntity1.setAccessKey(cephData.getString("access_key"));
                obsAccountMapper.updateByPrimaryKey(oldEntity1);
                return ResponseResult.succ();
            } else {

                return ResponseResult.error("调用 /bcp/v2/rgw/updateAK！");
            }

        }catch(Exception ex){
                return ResponseResult.error("修改账号信息失败！");
        }
    }

    public ResponseResult updateCloudAccountQuota(ObsAccount account) {
        try {

            //查询出配额信息
            ObsAccount  oldEntity1 = obsAccountMapper.selectByPrimaryKey(account.getId());
            if(oldEntity1.getQuota()>account.getQuota()){
                return ResponseResult.error("修改配额只能扩大，不能缩小!");
            }else {

                JSONObject obsParam = new JSONObject();
                obsParam.put("uid", oldEntity1.getCephUser());
                obsParam.put("quota", account.getQuota());
                obsParam.put("enabled",true);
                obsParam.put("quota_type", "user");

                JSONObject cephData = HttpClient.postEcs(String.format(httpIp, "/bcp/v2/rgw/set_quota"), obsParam.toJSONString());

                if (cephData != null) {
                    oldEntity1.setUpdateAt(DateUtil.parseStr(new Date()));//更新时间
                    oldEntity1.setQuota(account.getQuota());
                    obsAccountMapper.updateByPrimaryKey(oldEntity1);

                }else{
                    return ResponseResult.error("调用 /bcp/v2/rgw/set_quota接口失败！");
                }
                return ResponseResult.succ();
            }
        }catch(Exception ex){
            return ResponseResult.error("修改配额失败!");
        }
    }




    @Override
    public ResponseResult delInfo(List<Integer> ids) {
        Integer  id1 = new Integer("0");
        try {
            if(ids!=null&&ids.size()>0) {
                for(Integer id:ids){
                    id1 = id;
                    ObsAccount  account1 = obsAccountMapper.selectByPrimaryKey(id);
                    JSONObject obsParam = new JSONObject();
                    obsParam.put("uid", account1.getCephUser());

                    JSONObject cephData = HttpClient.postEcs(String.format(httpIp, "/bcp/v2/rgw/delAK"), obsParam.toJSONString());

                    ObsAccount account = obsAccountMapper.selectByPrimaryKey(id1);
                    account.setDeleted("1"); //被删除了
                    account.setDeletedAt(DateUtil.parseStr(new Date()));
                    obsAccountMapper.updateByPrimaryKey(account);
                }
            }
        }catch(IOException ce) {
            LogUtil.error(ce.getMessage(), ce);
            return ResponseResult.error(ce.getMessage());
        } catch(CommonCustomException ce) {
            LogUtil.error(ce.getMessage(), ce);
            if(ce.getMessage().indexOf("NoSuchKey")!=-1) {
                ObsAccount account = obsAccountMapper.selectByPrimaryKey(id1);
                account.setDeleted("1"); //被删除了
                account.setDeletedAt(DateUtil.parseStr(new Date()));
                obsAccountMapper.updateByPrimaryKey(account);
                return ResponseResult.succ();
            }else{
                return ResponseResult.error(ce.getErrorCode(), ce.getMessage());
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            return ResponseResult.error();
        }
        return ResponseResult.succ();

    }

    public  ResponseResult getBucket(ObsAccount account){
        //查询出配额信息
        JSONObject jsonObject = new JSONObject();
        ObsAccount  oldEntity1 = obsAccountMapper.selectByPrimaryKey(account.getId());
        try {
            String s = HttpClient.get(String.format(httpIp, "/bcp/v2/rgw/bucket_size?uid="+oldEntity1.getCephUser() ));

            JSONObject parse = (JSONObject) JSONObject.parse(s);
            String data = parse.getString("data");
            long used_size_kb = 0L;
            JSONArray jsonArrays = JSONArray.parseArray(data);
            if(jsonArrays!=null) {
                for (int i = 0; i < jsonArrays.size(); i++) {
                    JSONObject sshKeyData = jsonArrays.getJSONObject(i);
                    String usedStr = sshKeyData.getString("used");
                    if(usedStr!=null){
                        used_size_kb = used_size_kb +Long.parseLong(usedStr);
                    }
                }
            }
            jsonObject.put("backets", data);

            String s1 = HttpClient.get(String.format(httpIp, "/bcp/v2/rgw/get_quota?uid="+oldEntity1.getCephUser()+"&quota_type=user"));
            JSONObject parse1 = (JSONObject) JSONObject.parse(s1);
            String data1 = parse1.getString("data");

            jsonObject.put("total", data1);
            jsonObject.put("used_size_kb", used_size_kb+"");
            return ResponseResult.succObj(jsonObject);
        }catch (Exception ex){
            ex.printStackTrace();
            return ResponseResult.error("调用 /bcp/v2/rgw/get_quota接口失败！");
        }

    }


}
