package com.netease.backend.db.common.sql;


public class SAlterTriggerSetSQL extends SAlterTrigger {
	private static final long serialVersionUID = 3208824043991989716L;
	
	private SCreateTrigger createTriggerStmt;
	
	public SAlterTriggerSetSQL(String name) {
		super(name);
	}
	
	public SCreateTrigger getCreateTrigger() {
		return this.createTriggerStmt;
	}
	
	public void setCreateTrigger(SCreateTrigger t) {
		this.createTriggerStmt = t;
	}
	
}
