package com.netease.backend.db.common.sql;


public class SShowProcedures extends Statement {
	private static final long serialVersionUID = 1L;
	
	private String spName;
	
	public SShowProcedures() {
		super();
	}
	
	public String getSpName() {
		return this.spName;
	}
	
	public void setSpName(String str) {
		this.spName = str;
	}
}
