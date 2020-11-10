package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class HibernateSequences {
    private String sequenceName;

    private Long sequenceNextHiValue;

}