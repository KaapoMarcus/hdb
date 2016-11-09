package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class QueryServerInfo implements Serializable {

	private static final long serialVersionUID = -7282268156917042524L;

	
	private int asid;

	
	private String ip;

	
	private int port;

	
	private boolean isAsidReclaimed;

	
	
	public static final int STATUS_NORMAL = 0;
	
	public static final int STATUS_DEAD = 1;
	
	transient private int checkFailTimes;
	
	transient private int status = STATUS_NORMAL;

	public QueryServerInfo(int id, String ip, int port) {
		if (ip == null)
			throw new NullPointerException("IP can not be null.");
		this.asid = id;
		this.ip = ip;
		this.port = port;
		this.isAsidReclaimed = false;
	}

	public int getAsid() {
		return asid;
	}

	public void setAsid(int asid) {
		this.asid = asid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isAsidReclaimed() {
		return isAsidReclaimed;
	}

	public void setAsidReclaimed(boolean isAsidReclaimed) {
		this.isAsidReclaimed = isAsidReclaimed;
	}
	
	public int getCheckFailTimes() {
		return checkFailTimes;
	}

	public void setCheckFailTimes(int checkFailTimes) {
		this.checkFailTimes = checkFailTimes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || (!(obj instanceof QueryServerInfo)))
			return false;
		QueryServerInfo other = (QueryServerInfo) obj;
		if (this.ip != null && this.ip.equals(other.ip)
				&& this.port == other.port)
			return true;
		return false;
	}

}
