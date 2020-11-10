package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.entity.HibernateSequences;

public interface HibernateSequencesMapper {
    int deleteByPrimaryKey(String sequenceName);

    int insert(HibernateSequences record);

    int insertSelective(HibernateSequences record);

    HibernateSequences selectByPrimaryKey(String sequenceName);

    int updateByPrimaryKeySelective(HibernateSequences record);

    int updateByPrimaryKey(HibernateSequences record);
}