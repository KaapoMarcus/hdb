package com.netease.backend.db.common.sql;


public class SStartStatTask extends Statement {
	private static final long serialVersionUID = 8002979845255975044L;
	
	private int taskId;
	
	public SStartStatTask(int taskID) {
		this.taskId = taskID;
	}
	
	public int getTaskId() {
		return taskId;
	}
}
