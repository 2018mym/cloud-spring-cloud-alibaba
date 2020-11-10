package com.gwm.cloudecs.service;

import com.gwm.cloudecs.model.entity.Flavor;
import com.gwm.cloudecs.model.entity.Volume;

public interface FlavorService {

    Flavor getRecordByUUid(String flavorUuid);


}
