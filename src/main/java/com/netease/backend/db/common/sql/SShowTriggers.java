package com.netease.backend.db.common.sql;


public class SShowTriggers extends Statement {
	private static final long serialVersionUID = 1L;
	
	private String tableName;
	private String triggerName;
	
	public SShowTriggers() {
		super();
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public void setTableName(String name) {
		this.tableName = name;
	}
	
	public String getTriggerName() {
		return this.triggerName;
	}
	
	public void setTriggerName(String name) {
		this.triggerName = name;
	}
}
