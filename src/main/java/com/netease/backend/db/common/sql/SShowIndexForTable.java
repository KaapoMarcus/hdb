package com.netease.backend.db.common.sql;

public class SShowIndexForTable extends Statement {
	private static final long serialVersionUID = 1904359029589551579L;
	private String tableName;
	
	public SShowIndexForTable(String tblName) {
		this.tableName = tblName;
	}
	
	public String getTableName() {
		return tableName;
	}
}
