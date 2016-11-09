package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.Database;

public class SAddDbn extends Statement {
    private static final long serialVersionUID = 182380285416507046L;
    
    private Database dbInfo;
	private String user;
	private String password;
	private boolean initUsers;
	
	public SAddDbn(Database dbInfo, String user, String password, boolean initUsers) {
		super();
		this.dbInfo = dbInfo;
		this.user = user;
		this.password = password;
		this.initUsers = initUsers;
	}
	
	public Database getDbInfo() {
		return dbInfo;
	}
	
	public String getPassword() {
		return password;
	}

	public String getUser() {
		return user;
	}

	public boolean isInitUsers() {
		return initUsers;
	}
}
