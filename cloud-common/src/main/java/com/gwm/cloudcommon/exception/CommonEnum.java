package com.gwm.cloudcommon.exception;

import io.lettuce.core.dynamic.CommandMethod;

public enum CommonEnum implements BaseErrorInfoInterface {
    SUCCESS(200, "成功!"),
    NOT_FOUND(404, "未找到该资源!"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误!"),
    INTERNAL_SERVER_ERROR_NULL(500, "咦！服务器好像出错了!"),
    BODY_NOT_MATCH(400, "请求的数据格式不符!"),
    ADDSUCCESS(200, "新增成功!"),
    CREATESUCCESS(200, "新增成功!"),
    DELSUCCESS(200, "删除成功!"),
    UPDATESUCCESS(200, "更新成功!"),
    STARTSUCCESS(200, "启动成功"),
    STOPSUCCESS(200, "关闭成功"),
    RESTARTSUCCESS(200, "重启成功"),
    ATTACHSUCCESS(200, "重启成功"),
    REBUILDSUCCESS(200, "重置成功"),
    REVERTSUCCESS(200, "回滚成功"),
    //    ------------------------ shiro-------------
    UNAUTHENTIC(100401, "无权访问，当前是匿名访问，请先登录！"),
    UNAUTHORIZED(100403, "无权访问，当前帐号权限不足！"),
    UNAUTHTOKENEXPIRED(100404, "登录超时，请重新登录！"),
    //    ------------------------ verifiy-------------
    VERIFIYERROR(100405, "验证码错误、或已失效！"),
    //    ------------------------ user model-------------
    USERCODEERROR(200001, "帐号或密码错误！"),
    USERCODESUCCESS(200002, "登录成功！"),
    PROJECTQUOTASIZE(200003, "修改配额错误，无法修改！"),
    ROLEERROR(200001, "角色下有关联的用户或资源，不允许删除！"),
    //    ------------------------ecs model-------------
    ATTACHVOLUMEEERROR(300002, "挂载磁盘，磁盘状态不正确"),
    ATTACHINSTANCEERROR(300001, "挂载磁盘，虚拟机状态不正确"),
    REIONNOTNULLERROR(300003, "查询条件错误。无region参数"),
    DELETEVOLUMEERROR(300002, "删除磁盘错误，该磁盘已挂载"),
    SNAPSHOTTOTALERROR(300004, "创建快照错误，该卷创建快照大于3个"),
    VOLUMESIZEERROR(300005, "创建卷错误，卷大小不小于快照大小");
    /**
     * 错误码
     */
    private int resultCode;

    /**
     * 错误描述
     */
    private String resultMsg;

    CommonEnum(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public int getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
