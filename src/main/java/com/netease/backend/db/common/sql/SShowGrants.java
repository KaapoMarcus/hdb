package com.netease.backend.db.common.sql;


public class SShowGrants extends Statement {
    private static final long serialVersionUID = 1L;
    
    
	private String user;

	public SShowGrants(String user) {
		super();
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
