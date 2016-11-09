package com.netease.backend.db.common.sql;


public class SDropUser extends Statement {
    private static final long serialVersionUID = -7121182664854012425L;
    
    private String name;

	public SDropUser(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
