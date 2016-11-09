package com.netease.backend.db.common.stat;

import java.io.Serializable;


public class TableMemcachedStat implements Serializable {
	private static final long serialVersionUID = 5881814333141606757L;

	
	private String tableName;

	
	private MemcachedStat memcachedStat;

	public TableMemcachedStat(String tableName, MemcachedStat stat) {
		if (stat == null)
			throw new NullPointerException("memcached stat is null.");
		
		this.tableName = tableName;
		this.memcachedStat = stat;
	}

	public String getTableName() {
		return tableName;
	}

	public MemcachedStat getMemcachedStat() {
		return memcachedStat;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(tableName);
		builder.append(" [");
		builder.append(memcachedStat.toString());
		builder.append("]");
		return builder.toString();
	}
}
