package com.netease.backend.db.common.sql;

import java.io.Serializable;
import java.util.List;


public class SMigTask implements Serializable {
	private static final long serialVersionUID = 3335852374045033394L;
	
	private List<Integer> bucketNos;
	private String policy;
	private String desdb;
	private boolean resetTable;
	
	public SMigTask(List<Integer> bucketNos, String policy, String desdb, 
			boolean resetTable) {
		this.bucketNos = bucketNos;
		this.policy = policy;
		this.desdb = desdb;
		this.resetTable = resetTable;
	}
	
	public List<Integer> getBucketNos() {
		return bucketNos;
	}
	
	public String getPolicy() {
		return policy;
	}
	
	public String getDesdb() {
		return desdb;
	}
	
	public boolean isResetTable() {
		return resetTable;
	}
}
