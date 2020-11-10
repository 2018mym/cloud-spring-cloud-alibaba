package com.gwm.cloudcommon.util;

import com.gwm.cloudcommon.handler.ServletFilterAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintStream;


/**
 * Title : logUtil
 * @{author} Administrator
 * @{date} 2019年8月5日
 * @{description} 日志工具类

 */
public class LogUtil {
    // 源生日志记录对象
//     private static final Logger log = LoggerFactory.getLogger("custom_log");
    // 本对象的路径
    private static String thisClassUrl = LogUtil.class.getName();
    // 控制台输出
    private static PrintStream out = System.out;

    // 私有构造

    private LogUtil() {
    }

    /**
     * 获取日志记录器
     *
     * @return
     * @author Norton Lai
     * @created 2018-9-7 下午5:47:02
     */
    private static Logger getLog() {
        String clazzName = getClazzName();
        Logger logger =  LoggerFactory.getLogger(clazzName);
        return logger;
    }


    /**
     * 纯粹的syso
     *
     * @param o
     * @author Norton Lai
     * @created 2018-8-26 下午6:09:14
     */
    public static void syso(Object o) {
        out.println(o);
    }


    /**
     * 获取类名
     *
     * @return
     * @author Norton Lai
     * @created 2018-9-7 下午5:45:53
     */
    private static String getClazzName() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String className = null;
        // 从栈的最上开始 往下找 找到第一个不为Log和线程的类
        for (int i = 0; i < ste.length; i++) {
            className = ste[i].getClassName();

            if ("java.lang.Thread".equals(className) || thisClassUrl.equals(className)) {
                if (i != (ste.length - 1)) {
                    continue;// 如果不是最后一个就跳过，如果是最后一个那就没办法了，返回这栈信息吧
                }
            }
            break;
        }
        return className;
    }



    /**
     * 系统启动消息
     * @param msg
     * @author Norton Lai
     * @created 2018-9-10 下午4:45:13
     */
    public static void startMsg(String msg){
        info("###########################"+msg+"###########################");
    }
    /**
     * 关闭系统消息
     * @param msg
     * @author Norton Lai
     * @created 2018-9-10 下午5:07:57
     */
    public static void closeMsg(String msg){
        info("###########################"+msg+"###########################");
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void debug(String arg0) {
        Logger log = getLog();
        if (log.isDebugEnabled()) {
            
            log.debug(arg0);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void debug(String arg0, Object arg1) {
        Logger log = getLog();
        if (log.isDebugEnabled()) {
            log.debug("yuiyiyiyiyyyyyyyyyyyyyyyyy");
            log.debug(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void debug(String arg0, Object[] arg1) {
        Logger log = getLog();
        if (log.isDebugEnabled()) {
            
            log.debug(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void debug(String arg0, Throwable arg1) {
        Logger log = getLog();
        if (log.isDebugEnabled()) {
            
            log.debug(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void error(String arg0) {
        Logger log = getLog();
        if (log.isErrorEnabled()) {
            
            log.error(arg0);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void error(String arg0, Object arg1) {
        Logger log = getLog();
        if (log.isErrorEnabled()) {
            
            log.error(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void error(String arg0, Object[] arg1) {
        Logger log = getLog();
        if (log.isErrorEnabled()) {
            
            log.error(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void error(String arg0, Throwable arg1) {
        Logger log = getLog();
        if (log.isErrorEnabled()) {
            
            log.error(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void info(String arg0) {
        Logger log = getLog();
        if (log.isInfoEnabled()) {
            
            log.info(arg0);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void info(String arg0, Object arg1) {
        Logger log = getLog();
        if (log.isInfoEnabled()) {
            
            log.info(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void info(String arg0, Object[] arg1) {
        Logger log = getLog();
        if (log.isInfoEnabled()) {
            
            log.info(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void info(String arg0, Throwable arg1) {
        Logger log = getLog();
        if (log.isInfoEnabled()) {
            
            log.info(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void trace(String arg0) {
        Logger log = getLog();
        if (log.isTraceEnabled()) {
            
            log.trace(arg0);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void trace(String arg0, Object arg1) {
        Logger log = getLog();
        if (log.isTraceEnabled()) {
            
            log.trace(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void trace(String arg0, Object[] arg1) {
        Logger log = getLog();
        if (log.isTraceEnabled()) {
            
            log.trace(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void trace(String arg0, Throwable arg1) {
        Logger log = getLog();
        if (log.isTraceEnabled()) {
            
            log.trace(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void warn(String arg0) {
        Logger log = getLog();
        if (log.isWarnEnabled()) {
            
            log.warn(arg0);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void warn(String arg0, Object arg1) {
        Logger log = getLog();
        if (log.isWarnEnabled()) {
            
            log.warn(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void warn(String arg0, Object[] arg1) {
        Logger log = getLog();
        if (log.isWarnEnabled()) {
            
            log.warn(arg0, arg1);
        }
    }

    /**
     * 日志记录
     *
     * @param arg0
     * @author Norton Lai
     * @created 2018-8-25 下午7:10:11
     */
    public static void warn(String arg0, Throwable arg1) {
        Logger log = getLog();
        if (log.isWarnEnabled()) {
            
            log.warn(arg0, arg1);
        }
    }

}
