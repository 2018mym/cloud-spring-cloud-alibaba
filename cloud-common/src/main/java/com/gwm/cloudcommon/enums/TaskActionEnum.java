package com.gwm.cloudcommon.enums;

import java.util.Objects;

/**
 * @ClassName: TaskActionEnum
 * @Description: 任务执行
 * @Author: 99958168
 * @Date: 2020-06-11 15:49
 */
public enum TaskActionEnum {

    /**
     * 委托人解决任务
     */
    RESOLVE(0, "resolve", "委托人解决任务"),
    /**
     * 任务认领
     */
    CLAIM(1, "claim", "任务认领"),
    /**
     * 完成任务
     */
    COMPLETE(2, "complete", "完成任务"),
    /**
     * 委托任务
     */
    PENDING(3, "pending", "委托任务"),
    /**
     * 委托人已解决任务
     */
    RESOLVED(4, "resolved", "委托人已解决任务");

    private final int action;
    private final String name;
    private final String desc;

    TaskActionEnum(int action, String name, String desc) {
        this.action = action;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 判断值是否满足枚举中的value
     *
     * @param action
     * @return
     */
    public static boolean validation(Integer action) {
        for (TaskActionEnum s : TaskActionEnum.values()) {
            if (Objects.equals(s.getAction(), action)) {
                return true;
            }
        }
        return false;
    }

	public int getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}
    
    
    
    
    
    

}
