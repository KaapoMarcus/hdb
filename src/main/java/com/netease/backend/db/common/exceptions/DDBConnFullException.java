package com.netease.backend.db.common.exceptions;

import java.sql.SQLException;


public class DDBConnFullException extends SQLException {
	private static final long serialVersionUID = -6539134786768938284L;
	
	private String url;
	private String[] allUrls;
	private boolean xa;
	
	public DDBConnFullException(String reason, String url, String[] allUrls, boolean xa) {
		super(reason);
		this.url = url;
		this.allUrls = allUrls;
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
