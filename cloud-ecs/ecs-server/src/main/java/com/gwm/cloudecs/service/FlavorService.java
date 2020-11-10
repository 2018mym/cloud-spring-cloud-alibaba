package com.gwm.cloudecs.service;

import com.gwm.cloudecs.model.entity.Flavor;

import java.util.List;

public interface FlavorService {

    Flavor getRecordByUUid(String flavorUuid);


    List<Flavor> getFlavorList(Flavor flavor);
}
