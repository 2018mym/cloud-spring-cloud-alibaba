package com.gwm.cloudcommon.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    static public Date parseDate(String s) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(s);
    }
    
    public static String parseStr(Date d) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return format.format(d);
    }
    
    public static String parseHHmmssStr(Date d) {
    	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    	return format.format(d);
    }
    public static String parseHH(Date d) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
    	return format.format(d);
    }
    
    public static String parseHH(String d) {
    	//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
    	String value = d.substring(0,13);
    	return value;
    }
    
    
    public static List<String> getRecent24hours(){
    	Calendar expireDate = Calendar.getInstance(); 
		List<String> list = new ArrayList<String>();
		for(int i=1;i<=24;i++){
		  //在减去的时间上再减去1	
		  expireDate.set(Calendar.HOUR_OF_DAY, (expireDate.get(Calendar.HOUR_OF_DAY)-1));
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH");
		  list.add(df.format(expireDate.getTime()));
		}
		return list;
    }
    
    public static List<String> getRecent31days(){
    	Calendar expireDate = Calendar.getInstance(); 
		List<String> list = new ArrayList<String>();
		for(int i=1;i<=31;i++){
		  //在减去的时间上再减去1	
		  expireDate.set(Calendar.DAY_OF_MONTH, (expireDate.get(Calendar.DAY_OF_MONTH)-1));
		  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		  list.add(df.format(expireDate.getTime()));
		}
		return list;
    }
    
    /*
     * 判时间差距，两个时间相差多少天，时，分，秒
     */
    public static Long getDiffMinutes(Date currentDate,Date pastDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long days = null;
        try {
            Date currentTime = dateFormat.parse(dateFormat.format(currentDate));//现在系统当前时间
            Date pastTime    = dateFormat.parse(dateFormat.format(pastDate));
            //System.out.println("currentTime"+parseStr(currentTime));
            //System.out.println("pastDate"+parseStr(pastTime));
            long diff = currentTime.getTime() - pastTime.getTime();
            days = diff/(1000*60); //按分钟比较
            //System.out.println("diff:"+days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }
    /*
     * 当前时间的分钟数
     */
    public static Long getMilliSeconds(Date d) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long times = null;
        try {
            Date currentTime = dateFormat.parse(dateFormat.format(d));//现在系统当前时间
            times = currentTime.getTime()/1000*60;
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }
    //返回时间的秒数
    public static String formatDateTime(Long times){
    	return  (times/1000)+"m";
    }
    
    
    
    public static void main(String args[]){

    }
    
    
}
