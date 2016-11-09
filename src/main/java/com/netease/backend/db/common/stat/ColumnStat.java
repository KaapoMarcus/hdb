package com.netease.backend.db.common.stat;

import java.io.Serializable;

public class ColumnStat implements Serializable {

	private static final long serialVersionUID = 5100908451437201339L;
	
	
	String tableName = "";
	
	
	String columnName = "";
	
	
	int referCount = 0;
	
	
	public ColumnStat(String table, String column, int count)
	{
		this.tableName = table;
		this.columnName = column;
		this.referCount = count;
	}

	public String getTableName() {
		return tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getReferCount() {
		return referCount;
	}

}
