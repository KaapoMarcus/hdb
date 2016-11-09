package com.netease.backend.db.common.sql;


public class SGetStatResult extends Statement {
	private static final long serialVersionUID = 398243934657251597L;
	
	private int taskId;
	private String desc;
	
	public SGetStatResult(int taskId) {
		this.taskId = taskId;
		this.desc = "";
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String s) {
		this.desc = s;
	}
}
