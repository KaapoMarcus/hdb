package com.netease.backend.db.common.sql;


public class SAlterStatResult extends Statement {
	private static final long serialVersionUID = -3384411006685207330L;
	
	private int taskId;
	private int resultId;
	private String desc;
	
	public SAlterStatResult(int taskId, int resultId, String desc) {
		this.taskId = taskId;
		this.resultId = resultId;
		this.desc = desc;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public int getResultId() {
		return resultId;
	}
	
	public String getDesc() {
		return desc;
	}
}
