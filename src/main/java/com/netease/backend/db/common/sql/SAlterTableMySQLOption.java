package com.netease.backend.db.common.sql;


public class SAlterTableMySQLOption extends SAlterTableOp {

	
	private static final long serialVersionUID = 1375502604280283172L;

	public String toString() {
		return "alter table option " + getClauseSql();
	}
}
