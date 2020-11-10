package com.gwm.cloudecs.model.entity;

import java.util.Date;

public class CloudOrder {
    private Long id;

    private String uuid;

    private String type;

    private String submitname;

    private String submitcode;

    private Date submitdate;

    private String auditname;

    private String auditcode;

    private Date auditdate;

    private Integer status;

    private String rejectreason;

    private String workflowid;

    private String params;
    private String detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getSubmitname() {
        return submitname;
    }

    public void setSubmitname(String submitname) {
        this.submitname = submitname == null ? null : submitname.trim();
    }

    public String getSubmitcode() {
        return submitcode;
    }

    public void setSubmitcode(String submitcode) {
        this.submitcode = submitcode == null ? null : submitcode.trim();
    }

    public Date getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(Date submitdate) {
        this.submitdate = submitdate;
    }

    public String getAuditname() {
        return auditname;
    }

    public void setAuditname(String auditname) {
        this.auditname = auditname == null ? null : auditname.trim();
    }

    public String getAuditcode() {
        return auditcode;
    }

    public void setAuditcode(String auditcode) {
        this.auditcode = auditcode == null ? null : auditcode.trim();
    }

    public Date getAuditdate() {
        return auditdate;
    }

    public void setAuditdate(Date auditdate) {
        this.auditdate = auditdate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRejectreason() {
        return rejectreason;
    }

    public void setRejectreason(String rejectreason) {
        this.rejectreason = rejectreason == null ? null : rejectreason.trim();
    }

    public String getWorkflowid() {
        return workflowid;
    }

    public void setWorkflowid(String workflowid) {
        this.workflowid = workflowid == null ? null : workflowid.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}