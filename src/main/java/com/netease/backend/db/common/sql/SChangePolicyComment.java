package com.netease.backend.db.common.sql;


public class SChangePolicyComment extends Statement {
	private static final long serialVersionUID = 1866048777594291022L;
	
	private String policyName;
	private String newComment;
	
	public SChangePolicyComment(String plyName, String newCmt) {
		this.policyName = plyName;
		this.newComment = newCmt;
	}
	public String getPlyName() {
		return policyName;
	}
	public String getComment() {
		return newComment;
	}
}
