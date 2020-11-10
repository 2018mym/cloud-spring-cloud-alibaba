package com.gwm.cloudgateway.filter;

import com.alibaba.fastjson.JSON;

import com.gwm.cloudgateway.common.*;
import com.gwm.cloudgateway.common.model.UserVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;


@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Value("${user.url}")
    private String url;

    @Value("${user.check}")
    private String userCheck;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain filterChain) {
        ServerHttpRequest request = exchange.getRequest();

        //        //防止 OPTIONS 请求直接放行
        if (request.getMethod().equals(HttpMethod.OPTIONS)) {
            return filterChain.filter(exchange);
        }
        String requestURI = request.getURI().getPath();
        log.info(String.format("requestURI:%s", requestURI));
        if ("/".equals(requestURI)) {
            return filterChain.filter(exchange);
        }
        try {
            String auth = request.getHeaders().getFirst("ticket");
            String responseBody = HttpClient.get(url + userCheck, new HashMap<String, String>() {{
                put("ticket", auth);
            }});
            ResponseResult responseResult = JSON.parseObject(responseBody, ResponseResult.class);
            if (200 <= responseResult.getCode() && responseResult.getCode() < 300) {
                log.info("过滤器访问认证权限系统完成。。。。。");
                UserVo entity = JSON.parseObject(responseResult.getData().toString(), UserVo.class);
                if (!entity.isHaspower()) {
                    log.error(String.format("过滤器访问认证权限系统完成，但是此用户:%s没有访问权限",  entity.getUserId()));
                    return render(exchange.getResponse(), CommonEnum.UNAUTHORIZED.getResultCode(), CommonEnum.UNAUTHORIZED.getResultMsg(), "");
                }
                // 将用户信息放在requet header中
                Consumer<HttpHeaders> httpHeaders = httpHeader -> {
                    httpHeader.set("userId", entity.getUserId());
                    httpHeader.set("userName", entity.getUserName());
                    httpHeader.set("groupId", entity.getGroupId());
                    httpHeader.set("groupName", entity.getGroupName());
                };
                ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();
                exchange.mutate().request(serverHttpRequest).build();
                return filterChain.filter(exchange);
            } else {
                log.error(String.format("过滤器访问认证权限系统状错误，code=%s,message=%s,data=%s", responseResult.getCode(), responseResult.getMessage(), responseResult.getData()));
                return render(exchange.getResponse(), responseResult.getCode(), responseResult.getMessage(), "");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 自定义异常的类，用户返回给客户端相应的JSON格式的信息
           return render(exchange.getResponse(), CommonEnum.INTERNAL_SERVER_ERROR.getResultCode(), CommonEnum.INTERNAL_SERVER_ERROR.getResultMsg(), "");
        }
    }
    private Mono<Void> render(ServerHttpResponse response, Integer code, String codeMsg, String data) throws IOException {
        String str = JSON.toJSONString(new ResponseResult(code, codeMsg, data));
        DataBuffer buffer = response.bufferFactory().wrap(str.getBytes());
        response.setStatusCode(HttpStatus.OK);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "json/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
