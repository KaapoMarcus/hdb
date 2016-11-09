package com.netease.backend.db.common.sql;


public class SEnableWriteTable extends Statement {
	private static final long serialVersionUID = 3713897749314675961L;
	private String tableName;
	
	public SEnableWriteTable(String tblName) {
		this.tableName = tblName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
}
