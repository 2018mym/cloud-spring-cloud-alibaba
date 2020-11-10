package com.gwm.cloudstreamsets.model.entity;

import java.util.Date;

public class StreamSets {
	private Integer id;
	private String uuid;
	private String name;
	private String host;
	private String port = "18630";
	private Integer state = 0;
	private String type;
	private Integer areaId;
	private String installState;
	private String env;
	private String region;
	private String instancesId;
	private String userId;
	private String groupId;
	private Integer vcpus;
	private Integer memoryMb;
	private Date createdAt;
	private Date updatedAt;
	private Date deletedAt;
	private Integer deleted;
	private Integer taskId;
	private String  ticket;
	private Integer hostId;
	private String zone;
	private Integer retry;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVcpus() {
		return vcpus;
	}

	public void setVcpus(Integer vcpus) {
		this.vcpus = vcpus;
	}

	public Integer getMemoryMb() {
		return memoryMb;
	}

	public void setMemoryMb(Integer memoryMb) {
		this.memoryMb = memoryMb;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}
	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getInstancesId() {
		return instancesId;
	}

	public void setInstancesId(String instancesId) {
		this.instancesId = instancesId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getInstallState() {
		return installState;
	}

	public void setInstallState(String installState) {
		this.installState = installState;
	}
	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}
}
