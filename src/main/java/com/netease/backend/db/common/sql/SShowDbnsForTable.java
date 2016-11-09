package com.netease.backend.db.common.sql;

public class SShowDbnsForTable extends Statement {
	private static final long serialVersionUID = 9153384241821197758L;
	
	private String tableName;
	
	public SShowDbnsForTable(String name) {
		super();
		this.tableName = name;
	}
	
	public String getTableName() {
		return tableName;
	}
}
