package com.netease.backend.db.common.sql;

import java.util.List;

import com.netease.util.Pair;


public class SOnlineMigrate extends Statement {
	private static final long serialVersionUID = -3004375424862119074L;
	
	private List<String> policy;
	private List<Pair<String, String>> srcDbns;
	private List<Pair<String, String>> newDbns;
	private int repTimeout;
	private int migScale;
	
	public SOnlineMigrate(List<Pair<String, String>> srcDb, List<Pair<String, String>> newDb, 
			int migScale, List<String> policys) {
		super();
		this.policy = policys;
		this.srcDbns = srcDb;
		this.newDbns = newDb;
		this.repTimeout = 1000;
		this.migScale = migScale;
	}

	public int getRepTimeout() {
		return repTimeout;
	}

	public void setRepTimeout(int repTimeout) {
		this.repTimeout = repTimeout;
	}

	public List<Pair<String, String>> getSrcDbns() {
		return srcDbns;
	}

	public void setSrcDbns(List<Pair<String, String>> srcDbns) {
		this.srcDbns = srcDbns;
	}

	public List<Pair<String, String>> getNewDbns() {
		return newDbns;
	}

	public List<String> getPolicy() {
		return policy;
	}

	public int getMigScale() {
		return migScale;
	}

	public void setMigScale(int migScale) {
		this.migScale = migScale;
	}
}
