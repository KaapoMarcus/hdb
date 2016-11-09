package com.netease.backend.db.common.sql;


public class SShowMigrate extends Statement {
	private static final long serialVersionUID = 1L;
	
	private String type;	
	
	public SShowMigrate(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
