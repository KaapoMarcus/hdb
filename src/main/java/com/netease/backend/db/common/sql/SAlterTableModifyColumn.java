package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.ColumnInfo;

public class SAlterTableModifyColumn extends SAlterTableOp {
    private static final long serialVersionUID = -6216700359314144574L;
    
    private String columnName;
    private ColumnInfo column;
    private boolean specifiedPosition = false;
    private String previousColumnName = null;
    
    public SAlterTableModifyColumn(String columnName, ColumnInfo column) {
        this.columnName = columnName;
        this.column = column;
    }
    
	public SAlterTableModifyColumn(String columnName, ColumnInfo column,
			boolean specifiedPosition, String previousColumnName) {
		this.columnName = columnName;
        this.column = column;
        this.specifiedPosition = specifiedPosition;
        this.previousColumnName = previousColumnName;
	}
    
    public String getColumnName() {
        return columnName;
    }
    
    public ColumnInfo getColumn() {
        return column;
    }
    
    public String toString() {
    	String tmp = "modify column " + column.getName();
    	if (specifiedPosition) {
    		if (null != previousColumnName)
    			tmp += " AFTER " + previousColumnName;
    		else
    			tmp += " FIRST";
    	}
    	return tmp;
    }
    
	public boolean isSpecifiedPosition() {
		return specifiedPosition;
	}

	public void setSpecifiedPosition(boolean specifiedPosition) {
		this.specifiedPosition = specifiedPosition;
	}

	public String getPreviousColumnName() {
		return previousColumnName;
	}

	public void setPreviousColumnName(String previousColumnName) {
		this.previousColumnName = previousColumnName;
	}
}
