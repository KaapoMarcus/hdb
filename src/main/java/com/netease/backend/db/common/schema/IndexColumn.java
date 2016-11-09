package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class IndexColumn implements Serializable{

	private static final long serialVersionUID = -8487440586851167541L;
	
	
	private String tableName;
	
	
	private String indexName;
	
	
	private String columnName;
	
	
	private int seqInIndex;
	
	
	private long cardinality;
	
	
	private long rowsPerValue;
	
	
	private boolean isExpression = false;
	
	
	private boolean isDesc = false;

	
	private Object parseTree = null;

	
	public IndexColumn(String table, String index, String column, int seq, long cardinality, 
			long rows, boolean isExpr, boolean isDesc)
	{
		this.tableName = table;
		this.indexName = index;
		this.columnName = column;
		this.seqInIndex = seq;
		this.cardinality = cardinality;
		this.rowsPerValue = rows;
		this.isExpression = isExpr;
		this.isDesc = isDesc;
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		IndexColumn other = (IndexColumn)otherObject;
		
		return columnName.equalsIgnoreCase(other.columnName);
	}
	
	
	public Object clone()
	{
		try
		{
			IndexColumn cloned = (IndexColumn)super.clone();
			return cloned;
		}catch(CloneNotSupportedException e) { return null; }
	}

	synchronized public long getCardinality() {
		return cardinality;
	}

	synchronized public void setCardinality(long cardinality) {
		this.cardinality = cardinality;
	}

	public String getColumnName() {
		return columnName;
	}

	public long getRowsPerValue() {
		return rowsPerValue;
	}

	public void setRowsPerValue(long rowsPerValue) {
		this.rowsPerValue = rowsPerValue;
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
	
	public void rename(String newName){
		columnName=newName;
	}
	
	public boolean isExpression() {
		return isExpression;
	}

	public void setExpression(boolean isExpression) {
		this.isExpression = isExpression;
	}

	public boolean isDesc() {
		return isDesc;
	}

	public void setDesc(boolean isDesc) {
		this.isDesc = isDesc;
	}

	public Object getParseTree() {
		return parseTree;
	}

	public void setParseTree(Object parseTree) {
		this.parseTree = parseTree;
	}

	
}
