package com.netease.backend.db.common.sql;



public class SSingleMigrate extends Statement {
	private static final long serialVersionUID = -2929017289610077632L;

	private int taskId;
	private SMigTask task;
	private boolean online;
	private String selectOrder;	
	
	public SSingleMigrate(SMigTask task) {
		this.task = task;
		this.selectOrder = "NO_ORDER";
	}
	
	public SMigTask getMigTask() {
		return task;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public void setOnline(boolean b) {
		this.online = b;
	}
	
	public String getSelectOrder() {
		return selectOrder;
	}
	
	public void setSelectOrder(String order) {
		this.selectOrder = order;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int id) {
		this.taskId = id;
	}
}
