package com.netease.backend.db.common.sql;


public class SDropProcedure extends Statement {
	private static final long serialVersionUID = 8626122593886276115L;
	
	private String name;
	private boolean exists;
	
	public SDropProcedure(String name) {
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
