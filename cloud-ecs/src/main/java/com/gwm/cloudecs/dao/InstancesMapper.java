package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.InstancesListDTO;
import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;
import com.gwm.cloudecs.model.VO.InstanceTotalListVO;
import com.gwm.cloudecs.model.entity.Instances;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstancesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Instances record);

    int insertSelective(Instances record);

    Instances selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Instances record);

    int updateByPrimaryKey(Instances record);

    void updateInstanceStatus(@Param("uuid") String server,
                              @Param("state") Integer status);

    Instances selectByUuid(String instanceId);

    List<Instances> getInstancesList(InstancesListDTO instancesListDTO);

    List<InstanceTotalListVO> totalInstanceByProjectId(ResourceTotalListDTO dto);

    Integer selectByIpaddr(@Param("ipaddr") String ipaddr,
                        @Param("uuid") String instanceId,
                        @Param("projectId") String groupId);
}