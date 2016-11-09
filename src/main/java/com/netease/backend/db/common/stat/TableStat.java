package com.netease.backend.db.common.stat;

import java.io.Serializable;



public class TableStat implements Serializable, Cloneable {

	private static final long serialVersionUID = -8513212979989654996L;
	
	
	private String tableName;
	
	
	private int dbnId;
	
	
	private String engine = "InnoDB";
	
	
	private long dataLength = 0;
	
	
	private long maxDataLength = 0;
	
	
	private long indexLength = 0;
	
	
	private long dataFree = 0;
	
	
	private long autoIncrement = 0;
	
	
	private long createTime;
	
	
	private long updateTime = 0;
	
	
	private long checkTime = 0;
	
	
	private String collation = "";

	
	private long statsTime = 0;
	
	
	private long rows = 0;
	
	
	private long rowLen = 0;
	
	
	private int count = 0;
	
	public TableStat(int dbnId, String name, long rowCount, long rowLen)
	{
		this.tableName = name;
		this.dbnId = dbnId;
		this.rows = rowCount;
		this.rowLen = rowLen;
		this.count = 1;
	}
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		TableStat other = (TableStat)otherObject;
		
		return dbnId == other.dbnId && tableName.equalsIgnoreCase(other.tableName);
	}
	
	
	public Object clone()
	{
		try
		{
			TableStat cloned = (TableStat)super.clone();
			return cloned;
		}catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public long getAvgRowLen() {
		if(count == 0)
			return 0;
		else
			return rowLen/count;
	}
	
	public long getAvgRows() {
		if(count == 0)
			return 0;
		else
			return rows/count;
	}

	public long getRows() {
		return rows;
	}

	public String getTableName() {
		return tableName;
	}
	
	public void merge(TableStat tableStat)
	{
		if(!tableName.equalsIgnoreCase(tableStat.tableName))
			return;
		this.rows += tableStat.rows;
		this.rowLen += tableStat.rowLen;
		this.dataLength += tableStat.dataLength;
		this.indexLength += tableStat.indexLength;
		this.count++;
	}

	public long getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(long autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public long getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getDataFree() {
		return dataFree;
	}

	public void setDataFree(long dataFree) {
		this.dataFree = dataFree;
	}

	public long getDataLength() {
		return dataLength;
	}

	public void setDataLength(long dataLength) {
		this.dataLength = dataLength;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public long getIndexLength() {
		return indexLength;
	}

	public void setIndexLength(long indexLength) {
		this.indexLength = indexLength;
	}

	public long getMaxDataLength() {
		return maxDataLength;
	}

	public void setMaxDataLength(long maxDataLength) {
		this.maxDataLength = maxDataLength;
	}

	public long getRowLen() {
		return rowLen;
	}

	public void setRowLen(long rowLen) {
		this.rowLen = rowLen;
	}

	public long getStatsTime() {
		return statsTime;
	}

	public void setStatsTime(long statsTime) {
		this.statsTime = statsTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public void setRows(long rows) {
		this.rows = rows;
	}

	public int getDbnId() {
		return dbnId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
