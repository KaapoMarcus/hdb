package com.netease.backend.db.common.sql;


public class SDescHashFunction extends Statement {
	
	private static final long serialVersionUID = 8159908433501601346L;
	private String name;
	
	public SDescHashFunction(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
