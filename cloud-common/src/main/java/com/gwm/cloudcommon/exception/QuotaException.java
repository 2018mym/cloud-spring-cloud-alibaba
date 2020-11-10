package com.gwm.cloudcommon.exception;

import java.util.Map;

public class QuotaException extends RuntimeException {
    /**
     * 错误码
     */
    protected int errorCode;
    /**
     * 错误信息
     */
    protected String errorMsg;

    /**
     * 释放：free| 占用：occupy
     */
    protected String operate;

    /**
     * 配额编号
     */
    protected String resourceCode;
    /**
     * 具体占用或者释放的额度
     */
    protected Integer count;

    protected Map<String, Integer> resourceCodeCount;

    public Map<String, Integer> getResourceCodeCount() {
        return resourceCodeCount;
    }

    public void setResourceCodeCount(Map<String, Integer> resourceCodeCount) {
        this.resourceCodeCount = resourceCodeCount;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public QuotaException(BaseErrorInfoInterface errorInfoInterface, String operate, String resourceCode, Integer count) {
        super(errorInfoInterface.getResultMsg());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
        this.operate =operate;
        this.resourceCode = resourceCode;
        this.count = count;
    }
    public QuotaException(int errorCode, String errorMsg, String operate, String resourceCode, Integer count) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.operate =operate;
        this.resourceCode = resourceCode;
        this.count = count;
    }
    public QuotaException(int errorCode, String errorMsg, String operate, Map<String, Integer> resourceCodeCount) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.operate =operate;
        this.resourceCodeCount = resourceCodeCount;
    }
}
