package com.netease.backend.db.common.sql;

public class SAlterTableDropIndex extends SAlterTableOp {
    private static final long serialVersionUID = 1725059233471852922L;
    
    private String indexName;
    
    
    private boolean isOracleCreated = false;
    
    public SAlterTableDropIndex(String indexName, boolean isCreated) {
        this.indexName = indexName;
        this.isOracleCreated = isCreated;
    }
    
    public String getIndexName() {
        return indexName;
    }
    
	public String toString() {
    	if (indexName.equalsIgnoreCase("PRIMARY"))
    		return "drop primary key";
    	return "drop index " + indexName;
    }
	
	public boolean isOracleCreated() {
		return isOracleCreated;
	}

	public void setOracleCreated(boolean isOracleCreated) {
		this.isOracleCreated = isOracleCreated;
	}
}
