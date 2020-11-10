package com.gwm.cloudecs.dao;

import com.gwm.cloudecs.model.DTO.ImageListDTO;
import com.gwm.cloudecs.model.DTO.ResourceTotalListDTO;
import com.gwm.cloudecs.model.VO.ImageTotalListVO;
import com.gwm.cloudecs.model.entity.Image;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Image record);

    int insertSelective(Image record);

    Image selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Image record);

    int updateByPrimaryKey(Image record);

    List<Image> getImageList(ImageListDTO listDTO);

    Image getRecordByUUid(String uuid);

    List<ImageTotalListVO> totalImageByProjectId(ResourceTotalListDTO dto);
}