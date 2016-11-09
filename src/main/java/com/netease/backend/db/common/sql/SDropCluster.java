package com.netease.backend.db.common.sql;


public class SDropCluster extends Statement {
	private static final long serialVersionUID = -4792461236228496225L;
	private String name;
	private boolean dropTables;
	
	public SDropCluster(String cluName, boolean drop) {
		name = cluName;
		dropTables = drop;
	}
	
	public String getName() {
		return name;
	}

	public boolean isDropTables() {
		return dropTables;
	}
}
