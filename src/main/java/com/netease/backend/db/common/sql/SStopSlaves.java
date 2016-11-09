package com.netease.backend.db.common.sql;

import java.util.List;


public class SStopSlaves extends Statement {
	private static final long serialVersionUID = 6462920048714540284L;
	
	private List<String> dbNames;
	private boolean modifyCnf;
	
	public SStopSlaves(List<String> names) {
		super();
		this.dbNames = names;
		modifyCnf = true;
	}
	
	public List<String> getDbNames() {
		return this.dbNames;
	}
	
	public boolean isModifyCnf() {
		return this.modifyCnf;
	}
	
	public void setModifyCnf(boolean b) {
		this.modifyCnf = b;
	}
}
