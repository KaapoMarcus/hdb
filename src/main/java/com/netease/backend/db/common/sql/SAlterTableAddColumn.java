package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.ColumnInfo;

public class SAlterTableAddColumn extends SAlterTableOp {
    private static final long serialVersionUID = -1510221369473681160L;
    
    private ColumnInfo column;
    private boolean specifiedPosition = false;
    private String previousColumnName = null;
    
    public SAlterTableAddColumn(ColumnInfo column) {
        this.column = column;
    }
    
	public SAlterTableAddColumn(ColumnInfo column, boolean specifiedPosition,
			String previousColumnName) {
		this.column = column;
		this.specifiedPosition = specifiedPosition;
		this.previousColumnName = previousColumnName;
	}
    
    public ColumnInfo getColumn() {
        return column;
    }
    
    public String toString() {
    	String tmp = "add column " + column.getName();
    	if (specifiedPosition) {
    		if (null != previousColumnName)
    			tmp += " AFTER " + previousColumnName;
    		else
    			tmp += " FIRST";
    	}
    	return tmp;
    }
    
    public boolean equals(Object obj) {
    	if (!(obj instanceof SAlterTableAddColumn))
    		return false;
    	return column.equals(((SAlterTableAddColumn) obj).getColumn());
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
