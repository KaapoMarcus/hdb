package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.DbnCluster;


public class SCreateDbnCluster extends Statement {

	private static final long serialVersionUID = 5320766412569193686L;
	
	
	private String dbnClusterName;
	
	private String policyName;
	
	private DbnCluster dbnCluster;
	
	private String sql;
	
	public SCreateDbnCluster(String name, String plyName, DbnCluster clu, String sql) {
		this.dbnClusterName = name;
		this.policyName = plyName;
		this.dbnCluster = clu;
		this.sql = sql;
	}

	public String getDbnClusterName() {
		return dbnClusterName;
	}

	public void setDbnClusterName(String dbnClusterName) {
		this.dbnClusterName = dbnClusterName;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public DbnCluster getDbnCluster() {
		return dbnCluster;
	}

	public void setDbnCluster(DbnCluster dbnCluster) {
		this.dbnCluster = dbnCluster;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
}
