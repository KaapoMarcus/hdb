package com.netease.backend.db.common.sql;


public class SDropPartitions extends Statement {
	
	private static final long serialVersionUID = -2414484902321019149L;
	
	private String tableName;
	private int count;	
	
	public SDropPartitions(String tableName, int count) {
		this.tableName = tableName;
		this.count = count;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public int getCount() {
		return count;
	}
}
