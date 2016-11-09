package com.netease.backend.db.common.sql;


public class SDesc extends Statement {
	private static final long serialVersionUID = 8022650276884410761L;
	
	private String name;

	public SDesc(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
