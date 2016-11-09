package com.netease.backend.db.common.sql;

import java.util.ArrayList;
import java.util.List;


public class SAlterProcedureAddDbn extends SAlterProcedure {
	private static final long serialVersionUID = -3715477692705571247L;
	
	private List<String> dbns;
	
	public SAlterProcedureAddDbn(String name) {
		super(name);
		this.dbns = new ArrayList<String>();
	}
	
	public List<String> getDbns() {
		return this.dbns;
	}
	
	public void setDbns(List<String> strs) {
		this.dbns = strs;
	}
}
