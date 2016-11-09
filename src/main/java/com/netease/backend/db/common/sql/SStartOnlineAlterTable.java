package com.netease.backend.db.common.sql;


public class SStartOnlineAlterTable extends Statement {
	private static final long serialVersionUID = -6793050432034062192L;
	
	private long taskID;
	
	public SStartOnlineAlterTable(long id) {
		super();
		this.taskID = id;
	}
	
	public long getTaskID() {
		return this.taskID;
	}
}

