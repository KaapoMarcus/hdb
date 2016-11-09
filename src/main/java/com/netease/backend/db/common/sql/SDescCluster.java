package com.netease.backend.db.common.sql;


public class SDescCluster extends Statement {
	private static final long serialVersionUID = -4060500027171785726L;
	
	private String name;
	public SDescCluster(String dbnClusterName) {
		this.name = dbnClusterName;
	}
	
	public String getName() {
		return this.name;
	}
}
