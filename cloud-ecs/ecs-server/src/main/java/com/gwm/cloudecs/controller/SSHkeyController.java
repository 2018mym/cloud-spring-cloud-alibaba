package com.gwm.cloudecs.controller;

import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudecs.model.DTO.KeyPairsListDTO;
import com.gwm.cloudecs.model.entity.KeyPairs;
import com.gwm.cloudecs.service.ISSHKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 密钥对
 */
@RestController
@RequestMapping("/api/sshkey")
public class SSHkeyController {

    @Autowired
    ISSHKeyService sSHKeyService;

    /**
     * 获取密钥对列表
     */
    @GetMapping("/get/list")
    public Object sshkeyList(@RequestHeader("groupId") String groupId, KeyPairsListDTO keyPairsListDTO) {
        keyPairsListDTO.setGroupId(groupId);

        return sSHKeyService.getSSHKeyList(keyPairsListDTO);
    }
    /**
     * 获取单个密钥对
     */
    @GetMapping("/get/info")
    public Object sshkeyInfo(@RequestHeader("groupId") String groupId,
                             @RequestHeader("userId") String userId,
                             KeyPairs keyPairs) {
        keyPairs.setProjectId(groupId);
        keyPairs.setUserId(userId);
        return sSHKeyService.getOneSSHKey(keyPairs);
    }

    /**
     * 创建秘钥对
     */
    @PostMapping("/add/info")
    public Object createSshkeyInfo(@RequestHeader("ticket") String ticket,
                                   @RequestHeader("groupId") String groupId,
                                   @RequestHeader("userId") String userId,
                                   @RequestBody KeyPairs keyPairs) {
        keyPairs.setProjectId(groupId);
        keyPairs.setUserId(userId);
        return sSHKeyService.createSshkeyInfo(ticket,keyPairs);
    }

    //删除密钥对
    @GetMapping("/del/info")
    public ResponseResult delSshkeyInfo(@RequestHeader("ticket") String ticket, @RequestParam("ids") List<Integer> ids) throws Exception{
        return sSHKeyService.delSshkeyInfo(ticket,ids);
    }


}
