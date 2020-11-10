package com.gwm.cloudcommon.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gwm.cloudcommon.constant.Constant;
import com.gwm.cloudcommon.constant.ResponseResult;
import com.gwm.cloudcommon.exception.CommonCustomException;
import com.gwm.cloudcommon.exception.CommonEnum;
import com.gwm.cloudcommon.model.VO.UserVo;
import com.gwm.cloudcommon.redis.RedisUtil;
import com.gwm.cloudcommon.util.HttpClient;
import com.gwm.cloudcommon.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@WebFilter(filterName = "filter0_auth", urlPatterns = "/*")
public class ServletFilterAuth implements Filter {
    @Value("${user.url}")
    private String url;

    @Value("${user.check}")
    private String userCheck;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    QuotaHandle quotaHandle;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        LogUtil.info(String.format("requestURI:%s", requestURI));
        if ("/".equals(requestURI)) {
            filterChain.doFilter(request, servletResponse);
        } else {
            try {
                String auth = request.getHeader("ticket");
                String responseBody = HttpClient.get(url + userCheck, new HashMap<String, String>() {{
                    put("ticket", auth);
                }});
//            String responseBody = "{\"code\":200,\n" +
//                    "\"message\":\"\",\n" +
//                    "\"data\":{\"hasPower\":true, \"userId\":\"1\",\"userName\":\"\",\"nickName\":\"test\",\"email\":\"\",\"groupId\":\"1\",\"groupName\":\"test\"}\n" +
//                    "}";
                ResponseResult responseResult = JSON.parseObject(responseBody, ResponseResult.class);
                if (200 <= responseResult.getCode() && responseResult.getCode() < 300) {
                    LogUtil.info(String.format("ServletFilter过滤器访问认证权限系统完成。。。。。"));
                    UserVo entity = JSON.parseObject(responseResult.getData().toString(), UserVo.class);
                    HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest) request) {
                        @Override
                        public String getHeader(String name) {
                            String superHeader = super.getHeader(name);
                            if ("hasPower".equals(name)) {
                                return Boolean.valueOf(entity.isHaspower()).toString();
                            }
                            return superHeader;
                        }

                        @Override
                        public String getParameter(String name) {
                            if ("userId".equals(name)) {
                                return entity.getUserId();
                            }
                            if ("userName".equals(name)) {
                                return entity.getUserName();
                            }
                            if ("groupId".equals(name)) {
                                return entity.getGroupId();
                            }
                            if ("groupName".equals(name)) {
                                return entity.getGroupName();
                            }
                            return super.getParameter(name);
                        }
                    };
                    // 有权限 请求是否是创建或者删除  配额信息设置
//                String count = request.getParameter("count");
//                String resourceCode = request.getParameter("resourceCode");
                    // 如果是创建 则进行申请配额资源
//                if (requestURI.contains("/create/")) {
//                    if (StringUtils.isEmpty(count) || StringUtils.isEmpty(resourceCode)) {
//                        throw new CommonCustomException(CommonEnum.BODY_NOT_MATCH);
//                    }
//                    quotaHandle.putQuotas(auth, Constant.OCCUPY, resourceCode, Integer.valueOf(count));
//                    filterChain.doFilter(requestWrapper, servletResponse);
//                 // 删除 先注释 不能使用先释放，在进行占用
//                } else if (requestURI.contains("/delete/")) {
//                    if (StringUtils.isEmpty(count) || StringUtils.isEmpty(resourceCode)) {
//                        throw new CommonCustomException(CommonEnum.BODY_NOT_MATCH);
//                    }
//                    quotaHandle.putQuotas(auth, "free", resourceCode, Integer.valueOf(count));
//                    filterChain.doFilter(requestWrapper, servletResponse);
//                } else {
                    filterChain.doFilter(requestWrapper, servletResponse);
//                }
                } else {
                    LogUtil.error(String.format("ServletFilter过滤器访问认证权限系统状错误，code=%s,message=%s,data=%s", responseResult.getCode(), responseResult.getMessage(), responseResult.getData()));
                    render(response, responseResult.getCode(), responseResult.getMessage(), "");
                }

            } catch (CommonCustomException ce){
                LogUtil.error(ce.getMessage(), ce);
                render(response, ce.getErrorCode(), ce.getErrorMsg(), ce.getMessage());
            } catch(Exception e) {
                LogUtil.error(e.getMessage(), e);
                // 自定义异常的类，用户返回给客户端相应的JSON格式的信息
                render(response, CommonEnum.INTERNAL_SERVER_ERROR.getResultCode(), CommonEnum.INTERNAL_SERVER_ERROR.getResultMsg(), "");
            }
        }


    }

    private void render(HttpServletResponse response, Integer code, String codeMsg, String data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");
        String str = JSON.toJSONString(new ResponseResult(code, codeMsg, data));
        out.write(str.getBytes("UTF-8"));
        out.flush();
    }

    @Override
    public void destroy() {
        System.out.println("过滤器撤销");
    }
}