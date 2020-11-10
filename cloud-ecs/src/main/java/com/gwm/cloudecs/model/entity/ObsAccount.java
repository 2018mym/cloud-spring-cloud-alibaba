package com.gwm.cloudecs.model.entity;

public class ObsAccount {
    private Integer id;

    private String accessKey;

    private String cephUser;

    private String createAt;

    private String deleted;

    private String deletedAt;

    private String jobNum;

    private Integer quota;

    private String secretKey;

    private String updateAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey == null ? null : accessKey.trim();
    }

    public String getCephUser() {
        return cephUser;
    }

    public void setCephUser(String cephUser) {
        this.cephUser = cephUser == null ? null : cephUser.trim();
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt == null ? null : createAt.trim();
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

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum == null ? null : jobNum.trim();
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey == null ? null : secretKey.trim();
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt == null ? null : updateAt.trim();
    }

}
