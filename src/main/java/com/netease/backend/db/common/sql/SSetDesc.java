package com.netease.backend.db.common.sql;


public class SSetDesc extends Statement {
	private static final long serialVersionUID = 417653035284626900L;
	private String user;
	private String desc;
	
	public SSetDesc(String user, String desc) {
		this.user = user;
		this.desc = desc;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getDesc() {
		return desc;
	}
}
