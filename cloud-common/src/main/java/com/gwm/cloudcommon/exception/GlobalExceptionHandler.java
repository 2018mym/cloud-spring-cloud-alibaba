package com.gwm.cloudcommon.exception;


import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.handler.QuotaHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    QuotaHandle quotaHandle;
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("JSON 格式错误:{}", e);
        return ResponseResult.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数异常:{}", e);
        return ResponseResult.error(CommonEnum.BODY_NOT_MATCH);
//        return ResponseBuilder.error(ResultCode.PARAM_ERROR,"参数异常");
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
        log.error(e.getErrorMsg(), e);
        log.error("发生业务异常！原因是：{}",e.getErrorMsg());
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
        log.error(e.getMessage(), e);
        log.error("发生空指针异常！原因是:",e);
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
        log.error(e.getMessage(), e);
        log.error("未知异常！原因是:",e);
        return ResponseResult.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 配额问题异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = QuotaException.class)
    @ResponseBody
    public ResponseResult quotaHandle(HttpServletRequest req, QuotaException e){
        log.error(String.format("配额异常问题处理，参数:operate:%s,resourceCode:%s,count:%s，map:%s", e.operate, e.resourceCode, e.getCount(), e.getResourceCodeCount()));
        try {
            if (e.getResourceCodeCount().isEmpty()) {
                quotaHandle.putQuotas(req.getHeader("ticket"), e.operate, e.resourceCode, e.getCount());
            } else {
                quotaHandle.putQuotas(req.getHeader("ticket"), e.operate, e.getResourceCodeCount());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseResult.error(ex.getMessage());
        }
        log.error(e.getErrorMsg(), e);
        return ResponseResult.error(e.getErrorCode(),e.getErrorMsg());
    }
}
