package com.netease.backend.db.common.sql;


public class SAlterTableRenameColumn extends SAlterTableOp {
	private static final long serialVersionUID = 6714042941356644796L;

	private String oldColumnName;
	private String newColumnName;
	private boolean isBalanceField = false;
	
	
	public SAlterTableRenameColumn(String oldname, String newname) {
		this.oldColumnName = oldname;
		this.newColumnName = newname;
	}

	public String getOldName() {
		return oldColumnName;
	}

	public void setOldName(String oldName) {
		this.oldColumnName = oldName;
	}

	public String getNewName() {
		return newColumnName;
	}

	public void setNewName(String newName) {
		this.newColumnName = newName;
	}
	
	public String toString() {
		return "rename column " + oldColumnName + " to " + newColumnName;
	}
	
	public boolean isBalanceField() {
		return isBalanceField;
	}

	public void setBalanceField(boolean isBalanceField) {
		this.isBalanceField = isBalanceField;
	}

	public boolean equals(Object obj) {
	   	if (!(obj instanceof SAlterTableRenameColumn))
	   		return false;
	   	return this.newColumnName.equals(((SAlterTableRenameColumn) obj).newColumnName)
	   		&& this.oldColumnName.equals(((SAlterTableRenameColumn) obj).oldColumnName);
	   }
}
