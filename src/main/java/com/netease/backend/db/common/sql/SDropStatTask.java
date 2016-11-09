package com.netease.backend.db.common.sql;


public class SDropStatTask extends Statement {
    private static final long serialVersionUID = -5768658132597764935L;
    
    private int taskId;

	public SDropStatTask(int taskId) {
		this.taskId = taskId;
	}

	public int getTaskId() {
		return taskId;
	}
}
