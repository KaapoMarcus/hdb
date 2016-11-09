package com.netease.backend.db.common.exceptions;

import java.sql.SQLException;


public class DBNConnFullException extends SQLException {
	private static final long serialVersionUID = 409864676604857548L;
	
	private String url;
	private String[] allUrls;
	private boolean xa;
	
	public DBNConnFullException(String reason, String sqlState, int vendorCode, String url, boolean xa) {
		super(reason, sqlState, vendorCode);
		this.url = url;
		this.xa = xa;
	}

	public String getUrl() {
		return url;
	}

	public boolean isXa() {
		return xa;
	}

	public String[] getAllUrls() {
		return allUrls;
	}

	public void setAllUrls(String[] allUrls) {
		this.allUrls = allUrls;
	}
}
