package com.netease.backend.db.common.sql;


public class SAlterClientPsCache extends Statement {
	private static final long serialVersionUID = 3768725678909800948L;
	private long clientID;
	private Integer cacheSqlSizeLimit;
	private Integer parseTreeCacheSize;
	
	public SAlterClientPsCache(long clientId) {
		clientID = clientId;
	}
	
	public long getClientID() {
		return clientID;
	}
	
	public Integer getCacheSqlSizeLimit() {
		return cacheSqlSizeLimit;
	}
	
	public void setCacheSqlSizeLimit(int limit) {
		this.cacheSqlSizeLimit = limit;
	}
	
	public Integer getParseTreeCacheSize() {
		return parseTreeCacheSize;
	}
	
	public void setParseTreeCacheSize(int size) {
		this.parseTreeCacheSize = size;
	}
}
