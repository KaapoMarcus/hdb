package com.netease.backend.db.common.sql;


public class SChangeTablePolicy extends Statement {
	private static final long serialVersionUID = -2918748979144530948L;
	private String tblName;
	private String plyName;
	
	public SChangeTablePolicy(String table, String policy) {
		this.tblName = table;
		this.plyName = policy;
	}
	
	public String getTblName() {
		return this.tblName;
	}
	
	public String getPlyName() {
		return this.plyName;
	}
}
