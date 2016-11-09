package com.netease.backend.db.common.sql;

public class SShowDbnsForPolicy extends Statement {
	private static final long serialVersionUID = 3522007358000748985L;
	
	private String policyName;
	
	public SShowDbnsForPolicy(String policyName) {
		super();
		this.policyName = policyName;
	}
	
	public String getPolicyName() {
		return policyName;
	}
}
