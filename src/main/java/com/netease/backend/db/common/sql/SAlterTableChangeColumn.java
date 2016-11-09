package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.schema.ColumnInfo;

public class SAlterTableChangeColumn extends SAlterTableOp {
    private static final long serialVersionUID = 3570419467757713034L;
    
    private String oldColumnName;
    private ColumnInfo newColumn;
    private boolean isBalanceField = false;
    private boolean specifiedPosition = false;
    private String previousColumnName = null;
    
    public SAlterTableChangeColumn(String oldColumn, ColumnInfo newColumn) {
        this.oldColumnName = oldColumn;
        this.newColumn = newColumn;
        
    }
    
	public SAlterTableChangeColumn(String oldColumn, ColumnInfo newColumn,
			boolean specifiedPosition, String previousColumnName) {
		this.oldColumnName = oldColumn;
		this.newColumn = newColumn;
		this.specifiedPosition = specifiedPosition;
        this.previousColumnName = previousColumnName;
	}
    
    public String getOldColumnName() {
        return oldColumnName;
    }
    
    public ColumnInfo getNewColumn() {
        return newColumn;
    }
    
    public String toString() {
    	String tmp = "change column " + oldColumnName;
    	if (specifiedPosition) {
    		if (null != previousColumnName)
    			tmp += " AFTER " + previousColumnName;
    		else
    			tmp += " FIRST";
    	}
    	return tmp;
    }

	public boolean isBalanceField() {
		return isBalanceField;
	}

	public void setBalanceField(boolean isBalanceField) {
		this.isBalanceField = isBalanceField;
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
