package com.gwm.cloudgateway.common;






import java.io.Serializable;

public class ResponseResult implements Serializable {
    private int code;
    private String message;
    private Object data;

    private static final long serialVersionUID = 1L;

    public ResponseResult(int code, String message, Object data) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static ResponseResult succ(String message) {
        return new ResponseResult(Constant.SUCCESSCODE, message, null);
    }

    public static ResponseResult succ() {
        return new ResponseResult(Constant.SUCCESSCODE, Constant.SUCCESSMSG, null);
    }

    public static ResponseResult succ(CommonEnum commonEnum) {
        return new ResponseResult(commonEnum.getResultCode(), commonEnum.getResultMsg(), null);
    }

    public static ResponseResult succObj(Object message) {
        return new ResponseResult(Constant.SUCCESSCODE, Constant.SUCCESSMSG, message);
    }

    public static ResponseResult succObj(CommonEnum commonEnum, Object message) {
        return new ResponseResult(commonEnum.getResultCode(), commonEnum.getResultMsg(), message);
    }

    public static ResponseResult error(String message) {
        return new ResponseResult(Constant.FAILEDCODE, message, null);
    }

    public static ResponseResult error(CommonEnum commonEnum) {
        return new ResponseResult(commonEnum.getResultCode(), commonEnum.getResultMsg(), null);
    }

    public static ResponseResult error() {
        return new ResponseResult(Constant.FAILEDCODE, Constant.FAILEDMSG, null);
    }

    public static ResponseResult error(int code, String message) {
        return new ResponseResult(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
