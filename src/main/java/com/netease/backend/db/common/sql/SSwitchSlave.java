package com.netease.backend.db.common.sql;


public class SSwitchSlave extends Statement {
	private static final long serialVersionUID = -4682911231242484512L;
	
	private String name;
	private int waitTime;
	
	public SSwitchSlave(String dbName) {
		super();
		this.name = dbName;
	}
	
	public void setWaitTime(int time) {
		this.waitTime = time;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getWaitTime() {
		return this.waitTime;
	}
}
