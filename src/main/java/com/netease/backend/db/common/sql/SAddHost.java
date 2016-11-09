package com.netease.backend.db.common.sql;

import java.util.List;


public class SAddHost extends Statement {
    private static final long serialVersionUID = -8543961780544046721L;
    
    public static final int TYPE_DBA = 1;
	public static final int TYPE_CLIENT = 2;
	public static final int TYPE_QS = 3;
	
	private List<String> hosts;
	private int type;
	private String user;
	
	public SAddHost(String user, int type, List<String> hosts) {
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
