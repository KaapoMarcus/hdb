package com.netease.backend.db.common.stat;

import java.io.Serializable;

public class BucketStat implements Serializable {

	private static final long serialVersionUID = 1549598871762753435L;

	
	private String policyName = "";
	
	
	private int bucketNo = 0;
	
	
	private String dbUrl = "";
	
	
	private int readCount = 0;	
	
	
	private int updateCount = 0;
	
	
	private int insertCount = 0;
	
	
	
	public BucketStat(String policy, int bucket, String url, int readCount, int updateCount, int insertCount)
	{
		this.policyName = policy;
		this.bucketNo = bucket;
		this.dbUrl = url;
		this.readCount = readCount;
		this.updateCount = updateCount;
		this.insertCount = insertCount;
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		BucketStat other = (BucketStat)otherObject;
		
		return this.policyName.equals(other.policyName) && this.bucketNo == other.bucketNo;
	}

	public int getInsertCount() {
		return insertCount;
	}

	public void setInsertCount(int insertCount) {
		this.insertCount = insertCount;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public String getDbUrl() {
		return this.dbUrl;
	}

	public void setDbUrl(String url) {
		this.dbUrl = url;
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public int getBucketNo() {
		return bucketNo;
	}
}
