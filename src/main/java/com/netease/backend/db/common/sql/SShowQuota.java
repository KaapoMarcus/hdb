package com.netease.backend.db.common.sql;


public class SShowQuota extends Statement {
    private static final long serialVersionUID = 1L;
    
    private String user;

	public SShowQuota(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
