package com.netease.backend.db.common.sql;


public class SAddPartitions extends Statement {
	
	private static final long serialVersionUID = -8923172433395450284L;
	
	private String tableName;
	private int count;	
	
	public SAddPartitions(String tableName, int count) {
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
