package com.netease.backend.db.common.sql;


public class SCancelOnlineAlterTable extends Statement {
	private static final long serialVersionUID = -6793050432034062192L;
	
	private long taskID;
	
	public SCancelOnlineAlterTable(long id) {
		super();
		this.taskID = id;
	}
	
	public long getTaskID() {
		return this.taskID;
	}
}


