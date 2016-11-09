package com.netease.backend.db.common.sql;


public class SDescPolicy extends Statement {
	private static final long serialVersionUID = -2597553999844044595L;
	
	private String name;

	public SDescPolicy(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
