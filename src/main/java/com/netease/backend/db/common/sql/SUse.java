package com.netease.backend.db.common.sql;

import java.util.List;


public class SUse extends Statement {
	private static final long serialVersionUID = 8350708120694519494L;
	
	private boolean serial;
	private UseType type;
	private String name;
	private List<String> dbns;
	
	
	public enum UseType {
		ALL,	
		DBA,	
		DBI,	
		SYSDB,	
		DBNS_FOR_TABLE,	
		DBNS_FOR_POLICY,
		DBNS,	
		DDB		
	}

	public boolean isSerial() {
		return serial;
	}

	public void setSerial(boolean serial) {
		this.serial = serial;
	}

	public UseType getType() {
		return type;
	}

	public void setType(UseType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getDbns() {
		return dbns;
	}

	public void setDbns(List<String> dbns) {
		this.dbns = dbns;
	}
}
