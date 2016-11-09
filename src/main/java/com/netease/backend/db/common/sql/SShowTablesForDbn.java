package com.netease.backend.db.common.sql;

public class SShowTablesForDbn extends Statement {
	private static final long serialVersionUID = -189774972240066203L;
	
	private String dbnName;

	public SShowTablesForDbn(String dbnName) {
		super();
		this.dbnName = dbnName;
	}

	public String getDbnName() {
		return dbnName;
	}
}
