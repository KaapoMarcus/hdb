package com.netease.backend.db.common.sql;

public class SExportSysdb extends Statement {
	private static final long serialVersionUID = 644590962791380276L;
	
	private String dir;

	public SExportSysdb(String dir) {
		super();
		this.dir = dir;
	}

	public String getDir() {
		return dir;
	}
}
