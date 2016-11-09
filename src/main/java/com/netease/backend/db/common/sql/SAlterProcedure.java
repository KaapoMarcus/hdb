package com.netease.backend.db.common.sql;


public class SAlterProcedure extends Statement {
	private static final long serialVersionUID = -4640047472871191197L;
	
	protected String spName;
	
	
	public SAlterProcedure(String name) {
		super();
		this.spName = name;
	}
	
	public String getSpName() {
		return spName;
	}
}
