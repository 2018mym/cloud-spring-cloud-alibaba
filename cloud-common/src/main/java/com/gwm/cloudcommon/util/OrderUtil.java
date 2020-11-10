package com.gwm.cloudcommon.util;

import java.text.SimpleDateFormat;
import java.util.Random;

public class OrderUtil {
    // 获得日期
    public static String getTimeStamp() {
        String temp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        temp = sdf.format(new java.util.Date());
        return temp;
    }

    public  static String getOrderId() {

        StringBuffer buf = new StringBuffer();

        buf.append(getTimeStamp());    // 时间

        Random rand = new Random();
        for (int i=0;i<4;i++) {             // 四位随机数
            buf.append(rand.nextInt(10));
        }

        return buf.toString();
    }

    // 测试
    public static void main(String args[]) {

        OrderUtil createOrderId = new OrderUtil();

        String orderid = createOrderId.getOrderId();

        System.out.println(orderid);
    }
}
