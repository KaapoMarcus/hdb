package com.netease.backend.db.common.sql;


public class SDiableWritePolicy extends Statement {
	private static final long serialVersionUID = -2853266622194551649L;
	private String policyName;
	
	public SDiableWritePolicy(String plyName) {
		this.policyName = plyName;
	}
	
	public String getPolicyName() {
		return this.policyName;
	}
}
