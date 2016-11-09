package com.netease.backend.db.common.sql;

import java.util.ArrayList;
import java.util.List;


public class SAlterTriggerAddDbn extends SAlterTrigger {
	private static final long serialVersionUID = 639508396698462035L;
	
	private List<String> dbns;
	
	public SAlterTriggerAddDbn(String name) {
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
