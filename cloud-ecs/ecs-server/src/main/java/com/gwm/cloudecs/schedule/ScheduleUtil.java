package com.gwm.cloudecs.schedule;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @desc
 */

public class ScheduleUtil {

    

    private static HashMap<String, ScheduledFuture> map = new HashMap<>();
    private static ScheduledExecutorService pool;

    public static void init() {
        pool = Executors.newScheduledThreadPool(50);
    }

    /**
     * @param sr     需要执行测线程，该线程必须继承SRunnable
     * @param delay  延迟执行时间
     * @param period 执行周期时间
     * @param unit   时间单位 比如TimeUnit.SECONDS
     */
    public static void stard(TaskRunnable sr, long delay, long period, TimeUnit unit) {
        if (sr.getName() == null || map.get(sr.getName()) != null) {
            throw new UnsupportedOperationException("线程名不能为空或者线程名不能重复！");
        }
        if (pool == null || pool.isShutdown()) init();
        ScheduledFuture scheduledFuture = pool.scheduleAtFixedRate(sr, delay, period, unit);
        map.put(sr.getName(), scheduledFuture);
    }

    /**
     * @param thName 停止当前正在执行的线程，该线程必须是继承SRunnable
     */
    public static void stop(String thName) {
        Assert.notNull(thName, "停止线程时，线程名不能为空！");

        if (pool == null || pool.isShutdown()) return;//服务未启动
        if (map.size() > 0 && map.get(thName) != null) {
            map.get(thName).cancel(true);
            map.remove(thName);
        }
        if (map.size() <= 0) {
            shutdown();
        }
    }

    /**
     * 停止所有线程服务
     */
    public static void shutdown() {
        map.clear();
        pool.shutdown();
    }

    /**
     * 判断该线程是否还存活着，还在运行
     *
     * @param sr
     * @return
     */
    public static boolean isAlive(TaskRunnable sr) {
        if (map.size() > 0 && map.get(sr.getName()) != null) {
            return !map.get(sr.getName()).isDone();
        }
        return false;
    }

    public static void main(String[] args) {
        Map<String, Boolean> volumeServerMap = new HashMap<String, Boolean>(){{put("a", false);put("b", false);}};
        // 检测磁盘状态
        int volumeCount = 0;
        for (Map.Entry<String, Boolean> entry : volumeServerMap.entrySet()) {
            if (!entry.getValue()) {
                if ("a".equals(entry.getKey())) {
                    volumeServerMap.put(entry.getKey(), true);
                }
            }
        }
        System.out.println(volumeServerMap);
//
//        for (int i = 1; i < 12; i++) {
//
//            //1、待执行的线程类
//            int finalI = i;
//            TaskRunnable sr = new TaskRunnable() {
//                String thNmae = String.valueOf(finalI);
//                int t = 1;
//                int t2 = 1;
//                boolean f = false;
//                boolean f2 = false;
//                @Override
//                public void run() {
////                    in = ScheduleUtil.class.getClassLoader().getResourceAsStream(properiesName);
//                    System.out.println(thNmae + "-----------------开始检测。。。t:"+t+"-t2:"+t2+"-f:"+f+"-f2"+f2);
////                    if (!f){
////                        if (t<5) {
////                            t++;
////                        } else {
////                            f = true;
////                        }
////                    }
////                    if (!f2) {
////                        if (t2<3) {
////                            t2++;
////                        } else {
////                            f2 = true;
////                        }
////                    }
////                    if (f&&f2) {
////                        System.out.println("啧啧啧啧啧啧。、。。。。该让该线程停止了"+System.currentTimeMillis());
////                        ScheduleUtil.stop(thNmae);
////                    } else {
////                        System.out.println("还需要再检测了一次。。。" + System.currentTimeMillis());
////                    }
//
//                }
//
//                @Override
//                public String getName() {
//                    return String.valueOf(thNmae);
//                }
//            };
//
////2、启动
////            System.out.println("启动周期线程..." + i);
//            ScheduleUtil.stard(sr, 1, 2, TimeUnit.SECONDS);
//        }
////        try {
////            Thread.sleep(60000);
////
//////3、停止
////            System.out.println("停止周期线程");
////            ScheduleUtil.stop("1");
////            Thread.sleep(5000);
////            ScheduleUtil.stop("2");
////            Thread.sleep(3000);
////            ScheduleUtil.stop("4");
////            Thread.sleep(1000);
////            ScheduleUtil.stop("3");
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }

    }
}