package com.gwm.cloudecs.service.impl;

import com.gwm.cloudecs.dao.FlavorMapper;
import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.service.FlavorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlavorServiceImpl implements FlavorService {
    @Autowired
    FlavorMapper flavorMapper;


    @Override
    public Flavor getRecordByUUid(String flavorUuid) {
        return flavorMapper.getRecordByUUid(flavorUuid);
    }

    @Override
    public List<Flavor> getFlavorList(Flavor flavor) {
        return flavorMapper.getFlavorList(flavor);
    }


}
