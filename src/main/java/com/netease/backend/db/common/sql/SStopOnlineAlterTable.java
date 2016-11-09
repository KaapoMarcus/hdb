package com.netease.backend.db.common.sql;


public class SStopOnlineAlterTable extends Statement {
	private static final long serialVersionUID = 8761366905856047073L;
	private long taskID;

	public SStopOnlineAlterTable(long id) {
		super();
		this.taskID = id;
	}

	public long getTaskID() {
		return this.taskID;
	} 
}

