package com.netease.backend.db.common.sql;


public class SDisableWriteTable extends Statement {
	private static final long serialVersionUID = 2097466425249577078L;
	private String tableName;
	
	public SDisableWriteTable(String tblName) {
		this.tableName = tblName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
}
