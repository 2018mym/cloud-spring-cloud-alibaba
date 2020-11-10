package com.gwm.cloudstreamsets.dao;

import com.gwm.cloudstreamsets.model.dto.StreamsetsListDTO;
import com.gwm.cloudstreamsets.model.entity.StreamSets;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreamSetsMapper { 
//	@Select("select  id,alias_name,host,address from  cloud_streamsets  where id = #{id,jdbcType=VARCHAR}")
	StreamSets selectByPrimaryKey(String uuid);
//	@Insert("insert into cloud_streamsets(id,alias_name,host,address)  "
//			+ "  values(#{id,jdbcType=VARCHAR},#{alias_name,jdbcType=VARCHAR},#{host,jdbcType=VARCHAR},#{address,jdbcType=VARCHAR})")
	int  insert(StreamSets streamSets);
	int  update(StreamSets streamSets);
    List<StreamSets> getStreamSetsList(StreamsetsListDTO streamsetsListDTO);
    List<StreamSets> getInstallStreamSetsList(@Param("installStateArray") String installStateArray);
//获得锁
    @Insert("INSERT  INTO singleFlag  VALUES('GET LOCK')")
    Integer getSingleFlag();
//释放锁
    @Delete("DELETE FROM singleFlag")
    void deleteSingleFlag();
//初始化，创建表
    @Update("CREATE TABLE IF NOT EXISTS singleFlag (i VARCHAR(20) NOT NULL PRIMARY KEY) ENGINE = MEMORY")
    void InitSingleFlag();
}
