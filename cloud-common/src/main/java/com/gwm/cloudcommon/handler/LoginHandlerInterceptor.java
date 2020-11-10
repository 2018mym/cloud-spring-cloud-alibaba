package com.gwm.cloudcommon.handler;

import com.alibaba.fastjson.JSON;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.util.LogUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * <p>
 * <p>@description 接口拦截器</p>
 **/
@Component
public class LoginHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            String auth = request.getHeader("hasPower");
            if (auth == null) return true;
            if (Boolean.valueOf(auth)) {
                LogUtil.info(String.format("用户：%s,有该权限访问该接口:%s", request.getParameter("userId"), request.getRequestURI()));
                return true;
            } else {
                LogUtil.info(String.format("用户：%s,没有该权限访问该接口:%s", request.getParameter("userId"), request.getRequestURI()));
                render(response, CommonEnum.UNAUTHORIZED);
                return false;
            }
        }
        return true;
    }

//    private User Auth(HttpServletRequest request, HttpServletResponse response) {
//        //根据系统不同，系统用来管理session的方式也不相同，所以此处获取user信息的方法就不具体展开实现了
//        //本人是采用token+redis存储的方式来实现管理的，所以此处是通过token去redis中getUser即可
//    }

    private void render(HttpServletResponse response, CommonEnum codeMsg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(ResponseResult.error(codeMsg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}