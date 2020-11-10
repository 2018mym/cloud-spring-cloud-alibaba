package com.gwm.cloudalert.model.dto;

import com.gwm.cloudcommon.model.BaseDTO;

public class AlertDTO extends BaseDTO {
     Long start;
     Long end;
     String type;
     String step;
     String host;

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
