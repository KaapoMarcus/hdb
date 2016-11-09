package com.netease.backend.db.common.sql;

import java.util.List;


public class SRemoveHost extends Statement {
    private static final long serialVersionUID = -5591696848173719515L;
    
    private List<String> hosts;
	private int type;
	private String user;
	
	public SRemoveHost(String user, int type, List<String> hosts) {
		super();
		this.hosts = hosts;
		this.type = type;
		this.user = user;
	}

	public List<String> getHosts() {
		return hosts;
	}

	public int getType() {
		return type;
	}

	public String getUser() {
		return user;
	}
}
