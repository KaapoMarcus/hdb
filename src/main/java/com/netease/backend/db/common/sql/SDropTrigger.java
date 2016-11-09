package com.netease.backend.db.common.sql;


public class SDropTrigger extends Statement {
	private static final long serialVersionUID = -6528264225770590972L;
	
	private String name;
	private boolean exists;
	
	public SDropTrigger(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean haveExists() {
		return exists;
	}
	
	public void setExists(boolean b) {
		this.exists = b;
	}
}
