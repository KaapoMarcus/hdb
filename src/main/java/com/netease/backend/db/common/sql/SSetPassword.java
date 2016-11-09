package com.netease.backend.db.common.sql;


public class SSetPassword extends Statement {
    private static final long serialVersionUID = 7264117573644399876L;
    
    
	private String user;
	private String password;
	
	public SSetPassword(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUser() {
		return user;
	}
}
