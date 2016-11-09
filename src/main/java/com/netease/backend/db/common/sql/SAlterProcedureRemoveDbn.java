package com.netease.backend.db.common.sql;

import java.util.ArrayList;
import java.util.List;


public class SAlterProcedureRemoveDbn extends SAlterProcedure {
	private static final long serialVersionUID = -6384984223379724391L;
	
	private List<String> dbns;
	
	public SAlterProcedureRemoveDbn(String name) {
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
