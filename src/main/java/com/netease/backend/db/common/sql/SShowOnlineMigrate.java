package com.netease.backend.db.common.sql;


public class SShowOnlineMigrate extends Statement {
	private static final long serialVersionUID = 679592574054712860L;
	private long taskID;
	
	public SShowOnlineMigrate() {
		super();
		taskID = -1;
	}
	
	public long getTaskID() {
		return this.taskID;
	}
	
	public void setTaskID(long id) {
		this.taskID = id;
	}
}
