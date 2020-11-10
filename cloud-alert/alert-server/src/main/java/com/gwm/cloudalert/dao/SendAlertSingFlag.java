package com.gwm.cloudalert.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
@Repository
public interface SendAlertSingFlag {
        //获得锁
        @Insert("INSERT  INTO sendAlertSingFlag  VALUES('GET LOCK')")
        Integer getSingleFlag(Long sendTime);
        //释放锁
        @Delete("DELETE FROM sendAlertSingFlag")
        void deleteSingleFlag();
        //初始化锁
        @Update("CREATE TABLE IF NOT EXISTS sendAlertSingFlag (i VARCHAR(20) NOT NULL PRIMARY KEY) ENGINE = MEMORY")
        void initSingleFlag();
        @Update("drop table if exists initSendTime")
        void dropSendTime();
        //初始化及创建发送时间
        @Update("CREATE TABLE IF NOT EXISTS initSendTime (sendTime BIGINT(13) DEFAULT NULL) ENGINE = MEMORY")
        void initSendTime();
        @Insert("insert into initSendTime values(#{sendTime})")
        void initSendTimeValue(Long sendTime);
        //获取发送时间
        @Select("select sendTime from  initSendTime order by sendTime desc limit 1 ")
        List<Long> getSendTime();
        //修改发送时间
        @Select("update initSendTime set sendTime = #{sendTime}")
        List<Long> updateSendTime(Long sendTime);









    }
