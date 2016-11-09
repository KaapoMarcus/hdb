package com.netease.backend.db.common.sql;


public class SAlterGlobalPsCache extends Statement {
	private static final long serialVersionUID = 3859895459419636967L;
	private Integer prepareParamLimit;
	private Integer cacheSqlSizeLimit;
	private Integer parseTreeCacheSize;
	
	public Integer getPrepareParamLimit() {
		return prepareParamLimit;
	}
	
	public void setPrepareParamLimit(int limit) {
		if (limit < 3 || limit > 100)
			throw new IllegalArgumentException("prepareParamLimit should be in [3,100].");
		this.prepareParamLimit = limit;
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
