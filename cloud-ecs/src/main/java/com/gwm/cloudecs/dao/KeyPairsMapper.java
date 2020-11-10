package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.KeyPairsListDTO;
import com.gwm.cloudecs.model.entity.KeyPairs;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeyPairsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyPairs record);

    int insertSelective(KeyPairs record);

    KeyPairs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(KeyPairs record);

    int updateByPrimaryKey(KeyPairs record);

    List<KeyPairs> getKeyPairsList(KeyPairsListDTO listDTO);

    KeyPairs  getOneKeyPairs(KeyPairs record);

}