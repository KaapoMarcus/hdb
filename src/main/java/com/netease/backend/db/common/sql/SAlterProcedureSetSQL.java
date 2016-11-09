package com.netease.backend.db.common.sql;


public class SAlterProcedureSetSQL extends SAlterProcedure {
	private static final long serialVersionUID = 1472501780811776763L;
	
	private SCreateProcedure createProc;
	
	public SAlterProcedureSetSQL(String name) {
		super(name);
	}
	
	public SCreateProcedure getCreateProc() {
		return this.createProc;
	}
	
	public void setCreateProc(SCreateProcedure s) {
		this.createProc = s;
	}
}
