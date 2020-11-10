package com.gwm.cloudcommon.exception;

import com.alibaba.fastjson.JSON;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.handler.QuotaHandle;
import com.gwm.cloudcommon.util.LogUtil;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    QuotaHandle quotaHandle;
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        LogUtil.error("JSON 格式错误:{}", e);
        return ResponseResult.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handleValidationException(MethodArgumentNotValidException e) {
        LogUtil.error("参数异常:{}", e);
        return ResponseResult.error(CommonEnum.BODY_NOT_MATCH);
//        return ResponseBuilder.error(ResultCode.PARAM_ERROR,"参数异常");
    }

    /**
     * 未认证异常处理
     * @return
     */
    @ResponseBody
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseResult authenticationException() {

        return ResponseResult.error(CommonEnum.UNAUTHENTIC);
    }


    /**
     * 未授权异常处理
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseResult authorizationException() {
        return ResponseResult.error(CommonEnum.UNAUTHORIZED);
    }

    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = CommonCustomException.class)
    @ResponseBody
    public ResponseResult bizExceptionHandler(HttpServletRequest req, CommonCustomException e){
        LogUtil.error(e.getErrorMsg(), e);
        LogUtil.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return ResponseResult.error(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public ResponseResult exceptionHandler(HttpServletRequest req, NullPointerException e){
        LogUtil.error(e.getMessage(), e);
        LogUtil.error("发生空指针异常！原因是:",e);
        return ResponseResult.error(CommonEnum.INTERNAL_SERVER_ERROR_NULL);
    }


    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public ResponseResult exceptionHandler(HttpServletRequest req, Exception e){
        LogUtil.error(e.getMessage(), e);
        LogUtil.error("未知异常！原因是:",e);
        return ResponseResult.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 配额问题异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =QuotaException.class)
    @ResponseBody
    public ResponseResult quotaHandle(HttpServletRequest req, QuotaException e){
        LogUtil.error(String.format("配额异常问题处理，参数:operate:%s,resourceCode:%s,count:%s，map:%s", e.operate, e.resourceCode, e.getCount(), e.getResourceCodeCount()));
        try {
            if (e.getResourceCodeCount().isEmpty()) {
                quotaHandle.putQuotas(req.getHeader("ticket"), e.operate, e.resourceCode, e.getCount());
            } else {
                quotaHandle.putQuotas(req.getHeader("ticket"), e.operate, e.getResourceCodeCount());
            }
        } catch (Exception ex) {
            LogUtil.error(ex.getMessage(), ex);
            return ResponseResult.error(ex.getMessage());
        }
        LogUtil.error(e.getErrorMsg(), e);
        return ResponseResult.error(e.getErrorCode(),e.getErrorMsg());
    }
}
