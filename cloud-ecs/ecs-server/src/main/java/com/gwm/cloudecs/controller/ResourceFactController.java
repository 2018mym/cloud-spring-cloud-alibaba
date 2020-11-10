package com.gwm.cloudecs.controller;

import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;
import com.gwm.cloudecs.service.ResourceFactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 资源概况
 */
@RestController
@RequestMapping("/api/resource")
public class ResourceFactController {

    @Autowired
    ResourceFactService rfService;

    /**
     * 资源统计
     */
    @GetMapping("/total/list")
    public Object totalResourceList(@RequestHeader("groupId") String groupId,
                                    @RequestHeader("userId") String userId,
                                    ResourceTotalListDTO resDTO) {
        resDTO.setGroupId(groupId);

        return rfService.totalResourceList(resDTO);
    }



}
