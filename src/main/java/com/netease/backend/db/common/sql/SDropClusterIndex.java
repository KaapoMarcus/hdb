package com.netease.backend.db.common.sql;


public class SDropClusterIndex extends Statement {
	private static final long serialVersionUID = -1269775875476941322L;
	
	
	private String dbnClusterName;
	
	private String indexName;
	
	private boolean isForce;
	
	public SDropClusterIndex(String name, String clusterName, boolean force) {
		this.indexName = name;
		this.dbnClusterName = clusterName;
		this.isForce = force;
	}
	
	
	public String getSql() {
		String sql = "DROP INDEX " + indexName;
		if (isForce)
			sql = sql + " FORCE";
		return sql;
	}

	public String getDbnClusterName() {
		return dbnClusterName;
	}

	public void setDbnClusterName(String dbnClusterName) {
		this.dbnClusterName = dbnClusterName;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public boolean isForce() {
		return isForce;
	}

	public void setForce(boolean isForce) {
		this.isForce = isForce;
	}
}
