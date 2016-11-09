package com.netease.backend.db.common.sql;

public class SAlterTableDropColumn extends SAlterTableOp {
    private static final long serialVersionUID = 8675861561378872518L;
    
    private String columnName;
    
    public SAlterTableDropColumn(String columnName) {
        this.columnName = columnName;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public String toString() {
    	return "drop column " + columnName;
    }
}
