package com.netease.backend.db.common.sql;


public class SKillThread extends Statement {
	private static final long serialVersionUID = 1L;
	
	private String host;
	private String info;
	
	public SKillThread() {
	}
	
	public String getHost() {
		return host;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setHost(String h) {
		this.host = h;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
}
