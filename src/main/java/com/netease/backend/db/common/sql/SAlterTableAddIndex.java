package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.IndexInfo;

public class SAlterTableAddIndex extends SAlterTableOp {
    private static final long serialVersionUID = -4991594443241128710L;
    
    private IndexInfo index;
    
    
    private boolean isOracleCreated = false;
    
    
    public SAlterTableAddIndex(IndexInfo index, boolean isOracleCreated) {
        this.index = index;
        this.isOracleCreated = isOracleCreated;
    }
    
    public IndexInfo getIndex() {
        return index;
    }
    
	public boolean isOracleCreated() {
		return isOracleCreated;
	}

	public void setOracleCreated(boolean isOracleCreated) {
		this.isOracleCreated = isOracleCreated;
	}
	
	public String toString() {
    	if ("PRIMARY".equalsIgnoreCase(index.getIndexName()))
    		return "add primary key";
    	if (isOracleCreated)
    		return "create index " + index.getIndexName();
    	return "add index " + index.getIndexName();
    }
}
