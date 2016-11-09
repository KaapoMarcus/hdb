package com.netease.backend.db.common.sql;


public class SDropTable extends Statement {
	private static final long serialVersionUID = 4150563262108133425L;
	private String name;
	private boolean exists;
	
	public SDropTable(String name) {
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
