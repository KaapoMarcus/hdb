package com.netease.backend.db.common.sql;


public class SDropPolicy extends Statement {
	private static final long serialVersionUID = 4150563262108133425L;
	private String name;
	
	public SDropPolicy(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
