package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class TableMigResult  implements Serializable {

	private static final long serialVersionUID = -7186160677012781998L;

	
	private String tableName = "";
	
	
	private int selectCount = -1;
	
	
	private int insertCount = -1;
	
	
	private int deleteCount = -1;
	
	
	private String errMsg = "";
	
	public TableMigResult(String table)
	{
		tableName = table;
	}

	public int getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(int deleteCount) {
		this.deleteCount = deleteCount;
	}

	public int getInsertCount() {
		return insertCount;
	}

	public void setInsertCount(int insertCount) {
		this.insertCount = insertCount;
	}

	public int getSelectCount() {
		return selectCount;
	}

	public void setSelectCount(int selectCount) {
		this.selectCount = selectCount;
	}

	public String getTableName() {
		return tableName;
	}
	
	public String toString()
	{
		return tableName+"="+this.selectCount+";"+this.getInsertCount()+";"+this.getDeleteCount();
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	
	
	public boolean isFinished() {
		return this.selectCount>=0 && this.deleteCount>=0 && this.insertCount>=0;
	}
}
