package com.netease.backend.db.common.sql;


public class SAlterProcedureSetDesc extends SAlterProcedure {
	private static final long serialVersionUID = 4005896636030385963L;
	
	private String desc;
	
	public SAlterProcedureSetDesc(String name) {
		super(name);
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public void setDesc(String str) {
		this.desc = str;
	}
}
