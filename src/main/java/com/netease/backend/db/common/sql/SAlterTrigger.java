package com.netease.backend.db.common.sql;


public class SAlterTrigger extends Statement {
	private static final long serialVersionUID = -2088942701508915438L;
	
	protected String triggerName;
	
	public SAlterTrigger(String name) {
		super();
		this.triggerName = name;
	}
	
	public String getTriggerName() {
		return this.triggerName;
	}
}
