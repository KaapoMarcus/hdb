package com.netease.backend.db.common.stat;

import java.io.Serializable;



public class IndexColumnStat implements Serializable {
	private static final long serialVersionUID = 5945331713521595170L;
	private String tableName;
	private String indexName;
	private int seqInIndex;
	private String columnName;
	private long totalCardinality = 0;
	private int count = 0;
	private String sig;
	
	public IndexColumnStat(String tableName, String indexName, int sequence, 
			String columnName, long cardinality) 
	{
		this.tableName = tableName;
		this.indexName = indexName;
		this.seqInIndex = sequence;
		this.columnName = columnName;
		this.totalCardinality = cardinality;
		this.count = 1;
		this.sig = tableName+"."+indexName+"."+columnName+"."+seqInIndex;
	}
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		IndexColumnStat other = (IndexColumnStat)otherObject;
		
		return tableName.equalsIgnoreCase(other.tableName) 
				&& indexName.equalsIgnoreCase(other.indexName)
				&& columnName.equalsIgnoreCase(other.columnName)
				&& seqInIndex == other.seqInIndex;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getCount() {
		return count;
	}

	public String getIndexName() {
		return indexName;
	}

	public int getSeqInIndex() {
		return seqInIndex;
	}

	public String getTableName() {
		return tableName;
	}

	public long getTotalCardinality() {
		return totalCardinality;
	}
	
	public void merge(IndexColumnStat columnStat)
	{
		if(this.equals(columnStat))
		{
			this.totalCardinality += columnStat.totalCardinality;
			this.count ++;
		}
	}
	
	public String getSig()
	{
		return this.sig;
	}
	
	public long getAvgCardinality()
	{
		if(count == 0)
			return 0;
		else
			return totalCardinality/count;
	}
	
	public String toString()
	{
		return "table="+tableName+", index="+indexName+", seq="+seqInIndex+", " +
				"column="+columnName+", cardinality="+getAvgCardinality();
	}
}
