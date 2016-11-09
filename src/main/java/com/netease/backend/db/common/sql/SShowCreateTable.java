package com.netease.backend.db.common.sql;


public class SShowCreateTable extends Statement {
    private static final long serialVersionUID = 1L;
    
    private String tableName;

	public SShowCreateTable(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}
}
