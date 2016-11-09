package com.netease.backend.db.common.sql;


public class SShowSlaveDbns extends Statement {
	private static final long serialVersionUID = 4181549647001584803L;
	
	private String masterName;
	
	public SShowSlaveDbns() {
		super();
	}
	
	public void setMasterName(String name) {
		this.masterName = name;
	}
	
	public String getMasterName() {
		return this.masterName;
	}
}
