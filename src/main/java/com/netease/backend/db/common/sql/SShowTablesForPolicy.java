package com.netease.backend.db.common.sql;

public class SShowTablesForPolicy extends Statement {
	private static final long serialVersionUID = -5916218667642396356L;
	
	private String policyName;

	public SShowTablesForPolicy(String policyName) {
		super();
		this.policyName = policyName;
	}

	public String getPolicyName() {
		return policyName;
	}
}
