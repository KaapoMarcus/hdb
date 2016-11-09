package com.netease.backend.db.common.sql;

public class SAlterTableRename extends SAlterTableOp {
    private static final long serialVersionUID = -1510221369473681160L;
    
    private String newTableName;
    
    public SAlterTableRename(String tableName) {
    	this.newTableName = tableName;
    }
    
    public String getNewTableName() {
        return newTableName;
    }
    
    public String toString() {
    	return "rename to " + newTableName;
    }
    
    public boolean equals(Object obj) {
    	if (!(obj instanceof SAlterTableRename))
    		return false;
    	return this.newTableName.equals(((SAlterTableRename) obj).newTableName);
    }
}
