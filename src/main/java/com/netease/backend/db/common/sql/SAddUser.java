package com.netease.backend.db.common.sql;


public class SAddUser extends Statement {
    private static final long serialVersionUID = 4991702732160673449L;
    
    private String name;
	private String password;
	
	private int type;
	
	public SAddUser(String name, String password, int type) {
		this.name = name;
		this.password = password;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public int getType() {
		return type;
	}
}
