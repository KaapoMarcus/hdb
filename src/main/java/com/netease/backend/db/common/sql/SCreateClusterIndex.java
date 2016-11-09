package com.netease.backend.db.common.sql;


public class SCreateClusterIndex extends Statement {
	private static final long serialVersionUID = 865275522978946079L;
	
	
	private String indexName;
	
	private String dbnClusterName;
	
	private boolean isReverse = false;
	
	private String tableSpace = null;
	
	private String sql;
	
	public SCreateClusterIndex(String name, String cluster, boolean isRev, String tblSpace, String sql) {
		this.indexName = name;
		this.dbnClusterName = cluster;
		this.isReverse = isRev;
		this.tableSpace = tblSpace;
		this.sql = sql;
	}

	public String getDbnClusterName() {
		return dbnClusterName;
	}

	public void setDbnClusterName(String dbnClusterName) {
		this.dbnClusterName = dbnClusterName;
	}

	public boolean isReverse() {
		return isReverse;
	}

	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String name) {
		this.indexName = name;
	}
}
