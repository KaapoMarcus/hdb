package com.netease.backend.db.common.sql;


public class SEnableWritePolicy extends Statement {
	private static final long serialVersionUID = 2466526139181795212L;
	private String policyName;
	
	public SEnableWritePolicy(String plyName) {
		this.policyName = plyName;
	}
	
	public String getPolicyName() {
		return this.policyName;
	}
}
