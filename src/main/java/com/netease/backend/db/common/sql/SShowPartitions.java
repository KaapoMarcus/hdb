package com.netease.backend.db.common.sql;


public class SShowPartitions extends Statement {
	
	private static final long serialVersionUID = 1L;
	
	private String tableName;
	private String dbName;
	
	public SShowPartitions(String tableName) {
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String name) {
		this.dbName = name;
	}
}
