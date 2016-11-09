package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.List;

public class DataValidateTask implements Serializable {
	private static final long serialVersionUID = 1312816682542579361L;
	
	
	public static final int STATUS_START = 1;
	
	
	public static final int STATUS_STOP = 2;
	
	
	public static final int STATUS_VALIDATING = 3;
	
	
	public static final int STATUS_FINISHED_SUCC = 4;
	
	
	public static final int STATUS_FINISHED_FAILED = 5;
	
	
	private long oatID = -1;
	
	
	private String sourceTable = null;
	private String destTable = null;

	
    private long rowsAll = -1;
    
    
    private long rowsLimit = 0;
    
    
    private int chunkSize = 100;
    
    
    private int sleepTime = 100;
    
    
    private int status;
    
    
    private String uniqueColumn = null;
    
    private List<DataValidateDb> dbList = null;
    
    
    public DataValidateTask() {

    }
    
    
    public DataValidateTask(long oatID, String sourceTable, String destTable, long rowAll, 
    		long rowsLimit, int chunckSize, int sleepTime, String uniqueColumn, int status) {
    	this.oatID = oatID;
    	this.sourceTable = sourceTable;
    	this.destTable = destTable;
    	this.rowsAll = rowAll;
    	this.rowsLimit = rowsLimit;
    	this.chunkSize = chunckSize;
    	this.sleepTime = sleepTime;
    	this.uniqueColumn = uniqueColumn;
    	this.status = status;
    }

	public long getRowsLimit() {
		return rowsLimit;
	}

	public void setRowsLimit(long rowsLimit) {
		this.rowsLimit = rowsLimit;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	public String getUniqueColumn() {
		return uniqueColumn;
	}

	public void setUniqueColumn(String uniqueColumn) {
		this.uniqueColumn = uniqueColumn;
	}

	public long getRowsAll() {
		return rowsAll;
	}

	public void setRowsAll(long rowsAll) {
		this.rowsAll = rowsAll;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getOatID() {
		return oatID;
	}
	
	public void setOatID(long id) {
		this.oatID = id;
	}
	
	public String toString() {
		return "[" + this.oatID + "]";
	}
	
	public List<DataValidateDb> getDbList() {
		return dbList;
	}
	
	public void setDbList(List<DataValidateDb> list) {
		this.dbList = list;
	}
	
	public String getSourceTable() {
		return sourceTable;
	}

	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	public String getDestTable() {
		return destTable;
	}

	public void setDestTable(String destTable) {
		this.destTable = destTable;
	}
}

