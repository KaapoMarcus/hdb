package com.netease.backend.db.common.sql;


public class SSetTableIdAssignment extends Statement {
	private static final long serialVersionUID = -1956681265767697465L;
	private String tableName;
	private long startID;
	private long remainID;
	private long assignCount;
	private boolean notifyClient;
	
	public SSetTableIdAssignment(String name) {
		tableName = name;
		startID = -1;
		remainID = -1;
		assignCount = -1;
		notifyClient = true;
	}
	
	public boolean isNotifyClient() {
		return notifyClient;
	}

	public void setNotifyClient(boolean notifyClient) {
		this.notifyClient = notifyClient;
	}

	public String getTableName() {
		return tableName;
	}
	
	public long getStartID() {
		return startID;
	}
	
	public void setStartID(long startID) {
		this.startID = startID;
	}
	
	public long getRemainID() {
		return remainID;
	}
	
	public void setRemainID(long remainID) {
		this.remainID = remainID;
	}

	public long getAssignCount() {
		return assignCount;
	}

	public void setAssignCount(long assignCount) {
		this.assignCount = assignCount;
	}
}
