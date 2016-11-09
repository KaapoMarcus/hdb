package com.netease.backend.db.common.sql;


public class SStopStatTask extends Statement {
	private static final long serialVersionUID = 2912425699499864723L;
	
	private int taskId;
	
	public SStopStatTask(int taskId) {
		this.taskId = taskId;
	}
	
	public int getTaskId() {
		return taskId;
	}
}
