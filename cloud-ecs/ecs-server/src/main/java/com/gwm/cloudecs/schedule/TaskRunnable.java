package com.gwm.cloudecs.schedule;

public interface TaskRunnable extends  Runnable{
    /*该接口定义了线程的名字，用于管理，如判断是否存活，是否停止该线程等等*/
    String getName();
}
