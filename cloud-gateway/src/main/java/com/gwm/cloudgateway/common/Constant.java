package com.gwm.cloudgateway.common;

public class Constant {

    public static final int SUCCESSCODE = 200;
    public static final int FAILEDCODE = -1;

    public static final String SUCCESSMSG = "操作成功";
    public static final String FAILEDMSG = "操作失败";

    /**
     * 配额关键字 释放
     */
    public static final String FREE = "free";
    /**
     * 配额关键字 占用
     */
    public static final String OCCUPY = "occupy";



    /**
     * request请求头属性
     */
    public static final String REQUEST_AUTH_HEADER = "Authorization";
    /**
     * 刷新token字段定义 刷新token用户和此token所带的时间戳
     */
    public final static String TOKEN_JWT_REFRESH_USERNAME = "token_jwt_refresh_userName:%s_time:%s";
    // 刷新token 缓冲时间
    public final static int TOKEN_JWT_REFRESH_USERNAME_TIME = 5 ;

    /**
     * redis-key-前缀-shiro:refresh_token
     */
    public final static String PREFIX_SHIRO_REFRESH_TOKEN = "token_jwt:refresh_token:";
    /**
     * redis-key-前缀-shiro:refresh_token
     */
    public final static int PREFIX_SHIRO_REFRESH_TIME_TOKEN = 30 * 60;

    /**
     * JWT-currentTimeMillis 时间戳字段
     */
    public final static String CURRENT_TIME_MILLIS = "currentTimeMillis";


    /**
     * JWT token前缀
     */
    public static final String JWT_TOKEN = "token_jwt_";

    /**
     * 验证码缓存中时间
     */
    public static final long VERIFICATION__CODE_TIME = 5 * 60;

    /**
     * token中缓存时间
     */
    public static int TOKENEXPIRETIME = 1 * 60;
    /**
     * token中缓存时间  超时缓存时间
     */
    public static int REFRESHCHECKTIME = 1 * 60;
    /**
     * redis中token缓存时间
     */
    public static int REFRESHJWTTOKENEXPIRETIME = 30 * 60;


    /**
     * 过期时间
     */
    public static class ExpireTime {
        private ExpireTime() {
        }

        public static final int TEN_SEC = 5;//10s
        public static final int THIRTY_SEC = 30;//30s
        public static final int ONE_MINUTE = 60;//一分钟
        public static final int THIRTY_MINUTES = 60 * 30;//30分钟
        public static final int ONE_HOUR = 60 * 60;//一小时
        public static final int THREE_HOURS = 60 * 60 * 3;//三小时
        public static final int TWELVE_HOURS = 60 * 60 * 12;//十二小时，单位s
        public static final int ONE_DAY = 60 * 60 * 24;//二十四小时
    }
}
