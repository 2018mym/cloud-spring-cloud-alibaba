package com.gwm.cloudecs.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CloudOrderExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public CloudOrderExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUuidIsNull() {
            addCriterion("uuid is null");
            return (Criteria) this;
        }

        public Criteria andUuidIsNotNull() {
            addCriterion("uuid is not null");
            return (Criteria) this;
        }

        public Criteria andUuidEqualTo(String value) {
            addCriterion("uuid =", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotEqualTo(String value) {
            addCriterion("uuid <>", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThan(String value) {
            addCriterion("uuid >", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThanOrEqualTo(String value) {
            addCriterion("uuid >=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThan(String value) {
            addCriterion("uuid <", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThanOrEqualTo(String value) {
            addCriterion("uuid <=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLike(String value) {
            addCriterion("uuid like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotLike(String value) {
            addCriterion("uuid not like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidIn(List<String> values) {
            addCriterion("uuid in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotIn(List<String> values) {
            addCriterion("uuid not in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidBetween(String value1, String value2) {
            addCriterion("uuid between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotBetween(String value1, String value2) {
            addCriterion("uuid not between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(String value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(String value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(String value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(String value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(String value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(String value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLike(String value) {
            addCriterion("type like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotLike(String value) {
            addCriterion("type not like", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<String> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<String> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(String value1, String value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(String value1, String value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andSubmitnameIsNull() {
            addCriterion("submitName is null");
            return (Criteria) this;
        }

        public Criteria andSubmitnameIsNotNull() {
            addCriterion("submitName is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitnameEqualTo(String value) {
            addCriterion("submitName =", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameNotEqualTo(String value) {
            addCriterion("submitName <>", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameGreaterThan(String value) {
            addCriterion("submitName >", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameGreaterThanOrEqualTo(String value) {
            addCriterion("submitName >=", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameLessThan(String value) {
            addCriterion("submitName <", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameLessThanOrEqualTo(String value) {
            addCriterion("submitName <=", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameLike(String value) {
            addCriterion("submitName like", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameNotLike(String value) {
            addCriterion("submitName not like", value, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameIn(List<String> values) {
            addCriterion("submitName in", values, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameNotIn(List<String> values) {
            addCriterion("submitName not in", values, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameBetween(String value1, String value2) {
            addCriterion("submitName between", value1, value2, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitnameNotBetween(String value1, String value2) {
            addCriterion("submitName not between", value1, value2, "submitname");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeIsNull() {
            addCriterion("submitCode is null");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeIsNotNull() {
            addCriterion("submitCode is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeEqualTo(String value) {
            addCriterion("submitCode =", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeNotEqualTo(String value) {
            addCriterion("submitCode <>", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeGreaterThan(String value) {
            addCriterion("submitCode >", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeGreaterThanOrEqualTo(String value) {
            addCriterion("submitCode >=", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeLessThan(String value) {
            addCriterion("submitCode <", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeLessThanOrEqualTo(String value) {
            addCriterion("submitCode <=", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeLike(String value) {
            addCriterion("submitCode like", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeNotLike(String value) {
            addCriterion("submitCode not like", value, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeIn(List<String> values) {
            addCriterion("submitCode in", values, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeNotIn(List<String> values) {
            addCriterion("submitCode not in", values, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeBetween(String value1, String value2) {
            addCriterion("submitCode between", value1, value2, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitcodeNotBetween(String value1, String value2) {
            addCriterion("submitCode not between", value1, value2, "submitcode");
            return (Criteria) this;
        }

        public Criteria andSubmitdateIsNull() {
            addCriterion("submitDate is null");
            return (Criteria) this;
        }

        public Criteria andSubmitdateIsNotNull() {
            addCriterion("submitDate is not null");
            return (Criteria) this;
        }

        public Criteria andSubmitdateEqualTo(Date value) {
            addCriterion("submitDate =", value, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateNotEqualTo(Date value) {
            addCriterion("submitDate <>", value, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateGreaterThan(Date value) {
            addCriterion("submitDate >", value, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateGreaterThanOrEqualTo(Date value) {
            addCriterion("submitDate >=", value, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateLessThan(Date value) {
            addCriterion("submitDate <", value, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateLessThanOrEqualTo(Date value) {
            addCriterion("submitDate <=", value, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateIn(List<Date> values) {
            addCriterion("submitDate in", values, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateNotIn(List<Date> values) {
            addCriterion("submitDate not in", values, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateBetween(Date value1, Date value2) {
            addCriterion("submitDate between", value1, value2, "submitdate");
            return (Criteria) this;
        }

        public Criteria andSubmitdateNotBetween(Date value1, Date value2) {
            addCriterion("submitDate not between", value1, value2, "submitdate");
            return (Criteria) this;
        }

        public Criteria andAuditnameIsNull() {
            addCriterion("auditName is null");
            return (Criteria) this;
        }

        public Criteria andAuditnameIsNotNull() {
            addCriterion("auditName is not null");
            return (Criteria) this;
        }

        public Criteria andAuditnameEqualTo(String value) {
            addCriterion("auditName =", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameNotEqualTo(String value) {
            addCriterion("auditName <>", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameGreaterThan(String value) {
            addCriterion("auditName >", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameGreaterThanOrEqualTo(String value) {
            addCriterion("auditName >=", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameLessThan(String value) {
            addCriterion("auditName <", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameLessThanOrEqualTo(String value) {
            addCriterion("auditName <=", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameLike(String value) {
            addCriterion("auditName like", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameNotLike(String value) {
            addCriterion("auditName not like", value, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameIn(List<String> values) {
            addCriterion("auditName in", values, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameNotIn(List<String> values) {
            addCriterion("auditName not in", values, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameBetween(String value1, String value2) {
            addCriterion("auditName between", value1, value2, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditnameNotBetween(String value1, String value2) {
            addCriterion("auditName not between", value1, value2, "auditname");
            return (Criteria) this;
        }

        public Criteria andAuditcodeIsNull() {
            addCriterion("auditCode is null");
            return (Criteria) this;
        }

        public Criteria andAuditcodeIsNotNull() {
            addCriterion("auditCode is not null");
            return (Criteria) this;
        }

        public Criteria andAuditcodeEqualTo(String value) {
            addCriterion("auditCode =", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeNotEqualTo(String value) {
            addCriterion("auditCode <>", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeGreaterThan(String value) {
            addCriterion("auditCode >", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeGreaterThanOrEqualTo(String value) {
            addCriterion("auditCode >=", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeLessThan(String value) {
            addCriterion("auditCode <", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeLessThanOrEqualTo(String value) {
            addCriterion("auditCode <=", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeLike(String value) {
            addCriterion("auditCode like", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeNotLike(String value) {
            addCriterion("auditCode not like", value, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeIn(List<String> values) {
            addCriterion("auditCode in", values, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeNotIn(List<String> values) {
            addCriterion("auditCode not in", values, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeBetween(String value1, String value2) {
            addCriterion("auditCode between", value1, value2, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditcodeNotBetween(String value1, String value2) {
            addCriterion("auditCode not between", value1, value2, "auditcode");
            return (Criteria) this;
        }

        public Criteria andAuditdateIsNull() {
            addCriterion("auditDate is null");
            return (Criteria) this;
        }

        public Criteria andAuditdateIsNotNull() {
            addCriterion("auditDate is not null");
            return (Criteria) this;
        }

        public Criteria andAuditdateEqualTo(Date value) {
            addCriterion("auditDate =", value, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateNotEqualTo(Date value) {
            addCriterion("auditDate <>", value, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateGreaterThan(Date value) {
            addCriterion("auditDate >", value, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateGreaterThanOrEqualTo(Date value) {
            addCriterion("auditDate >=", value, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateLessThan(Date value) {
            addCriterion("auditDate <", value, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateLessThanOrEqualTo(Date value) {
            addCriterion("auditDate <=", value, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateIn(List<Date> values) {
            addCriterion("auditDate in", values, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateNotIn(List<Date> values) {
            addCriterion("auditDate not in", values, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateBetween(Date value1, Date value2) {
            addCriterion("auditDate between", value1, value2, "auditdate");
            return (Criteria) this;
        }

        public Criteria andAuditdateNotBetween(Date value1, Date value2) {
            addCriterion("auditDate not between", value1, value2, "auditdate");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andRejectreasonIsNull() {
            addCriterion("rejectReason is null");
            return (Criteria) this;
        }

        public Criteria andRejectreasonIsNotNull() {
            addCriterion("rejectReason is not null");
            return (Criteria) this;
        }

        public Criteria andRejectreasonEqualTo(String value) {
            addCriterion("rejectReason =", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonNotEqualTo(String value) {
            addCriterion("rejectReason <>", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonGreaterThan(String value) {
            addCriterion("rejectReason >", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonGreaterThanOrEqualTo(String value) {
            addCriterion("rejectReason >=", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonLessThan(String value) {
            addCriterion("rejectReason <", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonLessThanOrEqualTo(String value) {
            addCriterion("rejectReason <=", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonLike(String value) {
            addCriterion("rejectReason like", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonNotLike(String value) {
            addCriterion("rejectReason not like", value, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonIn(List<String> values) {
            addCriterion("rejectReason in", values, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonNotIn(List<String> values) {
            addCriterion("rejectReason not in", values, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonBetween(String value1, String value2) {
            addCriterion("rejectReason between", value1, value2, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andRejectreasonNotBetween(String value1, String value2) {
            addCriterion("rejectReason not between", value1, value2, "rejectreason");
            return (Criteria) this;
        }

        public Criteria andWorkflowidIsNull() {
            addCriterion("workflowId is null");
            return (Criteria) this;
        }

        public Criteria andWorkflowidIsNotNull() {
            addCriterion("workflowId is not null");
            return (Criteria) this;
        }

        public Criteria andWorkflowidEqualTo(String value) {
            addCriterion("workflowId =", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidNotEqualTo(String value) {
            addCriterion("workflowId <>", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidGreaterThan(String value) {
            addCriterion("workflowId >", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidGreaterThanOrEqualTo(String value) {
            addCriterion("workflowId >=", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidLessThan(String value) {
            addCriterion("workflowId <", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidLessThanOrEqualTo(String value) {
            addCriterion("workflowId <=", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidLike(String value) {
            addCriterion("workflowId like", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidNotLike(String value) {
            addCriterion("workflowId not like", value, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidIn(List<String> values) {
            addCriterion("workflowId in", values, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidNotIn(List<String> values) {
            addCriterion("workflowId not in", values, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidBetween(String value1, String value2) {
            addCriterion("workflowId between", value1, value2, "workflowid");
            return (Criteria) this;
        }

        public Criteria andWorkflowidNotBetween(String value1, String value2) {
            addCriterion("workflowId not between", value1, value2, "workflowid");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}