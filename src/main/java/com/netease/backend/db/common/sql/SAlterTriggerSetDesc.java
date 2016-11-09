package com.netease.backend.db.common.sql;


public class SAlterTriggerSetDesc extends SAlterTrigger {
	private static final long serialVersionUID = -8319423477873613899L;
	
	private String desc;
	
	public SAlterTriggerSetDesc(String name) {
		super(name);
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public void setDesc(String str) {
		this.desc = str;
	}
}
