package com.netease.backend.db.common.sql;


public class SAlterDbnClusterSize extends Statement {
	private static final long serialVersionUID = -8449197430426755899L;
	
	
	String name;
	
	int size;
	
	String sql = null;
	
	public SAlterDbnClusterSize(String name, int size) {
		if (null == name)
			throw new IllegalArgumentException("Name of DbnCluster can not be null!");
		if (size < 1)
			throw new IllegalArgumentException("Size of DbnCluster should be positive!");
		this.name = name;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if (size < 1)
			throw new IllegalArgumentException("Size of DbnCluster should be positive!");
		this.size = size;
	}

	public String getSql() {
		if (null == sql || (null != sql && "".equals(sql))) {
			sql = "alter cluster " + name + " size " + size;
		}
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
