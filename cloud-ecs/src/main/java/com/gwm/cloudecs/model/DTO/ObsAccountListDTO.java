package com.gwm.cloudecs.model.DTO;

import com.gwm.cloudcommon.model.BaseDTO;

public class ObsAccountListDTO  extends BaseDTO {
    private String jobNum;

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }
}
