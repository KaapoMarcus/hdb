package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class BucketLoad implements Serializable {
	private static final long serialVersionUID = 3519078096695316647L;
	
	
	private String dbName;
	
	
	private String asID;
	
	
	private String asIP;
	
	
	private String policyName;
	
	
	
	
	int bucketNo;
	
	
	int readCount;
	
	
	int updateCount;
	
	
	int insertCount;
	
	
	private long startTime;
	
	
	private long endTime;
	
	public String getAsID() {
		return asID;
	}
	
	public void setAsID(String asid) {
		this.asID = asid;
	}
	
	public String getAsIP() {
		return asIP;
	}
	
	public void setAsIP(String asip) {
		this.asIP = asip;
	}
	
	
	
	
	
	
	
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long et) {
		this.endTime = et;
	}
	
	public String getPolicyName() {
		return policyName;
	}
	
	public void setPolicyName(String pn) {
		this.policyName = pn;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long st) {
		this.startTime = st;
	}
	
	public int getBucketNo() {
		return bucketNo;
	}
	
	public void setBucketNo(int bucketNo) {
		this.bucketNo = bucketNo;
	}
	
	public int getInsertCount() {
		return insertCount;
	}
	
	public void setInsertCount(int insertCount) {
		this.insertCount = insertCount;
	}
	
	public int getReadCount() {
		return readCount;
	}
	
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	
	public int getUpdateCount() {
		return updateCount;
	}
	
	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String dbn) {
		this.dbName = dbn;
	}
}
