package com.netease.backend.db.common.sql;

import java.util.List;


public class SCreatePolicy extends Statement {
    private static final long serialVersionUID = -813040514184870880L;
    
    private String policyName;
	private String bucketLayoutMethod;
	private List<String> srcDbns;
	private int bucketCount;
	private String lookupPolicy;
	private String comment;
	private String dbnTypeStr;
	
	public SCreatePolicy(String policyName, String type, String bucketLayoutMethod, List<String> srcDbns, int bucketCount, String lookupPolicy, String comment) {
		this.policyName = policyName;
		this.dbnTypeStr = type;
		this.bucketLayoutMethod = bucketLayoutMethod;
		this.srcDbns = srcDbns;
		this.bucketCount = bucketCount;
		this.lookupPolicy = lookupPolicy;
		this.comment = comment;
	}
	public String getPolicyName() {
		return policyName;
	}
	public int getBucketCount() {
		return bucketCount;
	}
	public String getBucketLayoutMethod() {
		return bucketLayoutMethod;
	}
	public List<String> getSrcDbns() {
		return srcDbns;
	}
	public String getLookupPolicy() {
		return lookupPolicy;
	}
	public void setLookupPolicy(String lookupPolicy) {
		this.lookupPolicy = lookupPolicy;
	}
	public String getComment() {
		return comment;
	}
	public String getDbnTypeStr() {
		return dbnTypeStr;
	}
}
