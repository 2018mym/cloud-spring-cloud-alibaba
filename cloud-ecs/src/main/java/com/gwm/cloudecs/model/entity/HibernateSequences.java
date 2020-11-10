package com.gwm.cloudecs.model.entity;

public class HibernateSequences {
    private String sequenceName;

    private Long sequenceNextHiValue;

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName == null ? null : sequenceName.trim();
    }

    public Long getSequenceNextHiValue() {
        return sequenceNextHiValue;
    }

    public void setSequenceNextHiValue(Long sequenceNextHiValue) {
        this.sequenceNextHiValue = sequenceNextHiValue;
    }
}