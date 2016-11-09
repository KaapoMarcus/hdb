package com.netease.backend.db.common.sql;

import java.io.Serializable;

public abstract class SAlterTableOp implements Serializable {
	
	private static final long serialVersionUID = 3840608324698472542L;
	
	
	protected String sql;

	
	protected String clauseSql;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getClauseSql() {
		return clauseSql;
	}

	public void setClauseSql(String clauseSql) {
		this.clauseSql = clauseSql;
	}
}
