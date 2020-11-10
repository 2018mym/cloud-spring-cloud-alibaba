package com.gwm.cloudecs.model.entity;

public class CommitJob {
    private Integer id;

    private String approvalAt;

    private String cephUser;

    private String commitAt;

    private String deleted;

    private String deletedAt;

    private String description;

    private String jobNum;

    private String jobType;

    private String name;

    private String phone;

    private Integer quota;

    private String reason;

    private String status;

    private String uuid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApprovalAt() {
        return approvalAt;
    }

    public void setApprovalAt(String approvalAt) {
        this.approvalAt = approvalAt == null ? null : approvalAt.trim();
    }

    public String getCephUser() {
        return cephUser;
    }

    public void setCephUser(String cephUser) {
        this.cephUser = cephUser == null ? null : cephUser.trim();
    }

    public String getCommitAt() {
        return commitAt;
    }

    public void setCommitAt(String commitAt) {
        this.commitAt = commitAt == null ? null : commitAt.trim();
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted == null ? null : deleted.trim();
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt == null ? null : deletedAt.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum == null ? null : jobNum.trim();
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType == null ? null : jobType.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }
}