package com.netease.backend.db.common.sql;

import java.util.ArrayList;
import java.util.List;


public class SAlterTriggerRemoveDbn extends SAlterTrigger {
	private static final long serialVersionUID = 2112098811273199852L;
	
	private List<String> dbns;
	
	public SAlterTriggerRemoveDbn(String name) {
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
