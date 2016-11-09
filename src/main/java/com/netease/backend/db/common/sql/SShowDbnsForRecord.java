package com.netease.backend.db.common.sql;

import java.util.List;



public class SShowDbnsForRecord extends Statement {
	private static final long serialVersionUID = 3522007358000748985L;
	
	
	private String tableName;
	
	
	private List<List<String>> keyValues;

	public SShowDbnsForRecord(String tableName, List<List<String>> keyValues) {
		super();
		this.tableName = tableName;
		this.keyValues = keyValues;
}

	public String getTableName() {
		return tableName;
	}

	public List<List<String>> getKeyValues() {
		return keyValues;
	}
	
	
}
