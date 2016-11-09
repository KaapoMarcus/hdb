package com.netease.backend.db.common.sql;


public class SShowNtseParam extends Statement {
	private static final long serialVersionUID = 913250138692278759L;
	private String tableName;
	
	private boolean isTable;
	
	
	public SShowNtseParam(String tblName, boolean isTable) {
		this.tableName = tblName;
		this.isTable = isTable;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public boolean isTable() {
		return this.isTable;
	}
}
