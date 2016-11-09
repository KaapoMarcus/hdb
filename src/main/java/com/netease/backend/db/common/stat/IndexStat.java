package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.netease.backend.db.common.utils.StringUtils;


public class IndexStat implements Serializable{

	private static final long serialVersionUID = -2777706353469823177L;

	
	private String tableName;
	
	
	private String indexName;
	
	
	private List<String> fieldList = new ArrayList<String>();
	
	
	private List<StmtExplain> stmtList = new ArrayList<StmtExplain>();
	
	
	private int usedTimes = 0;
	
	
	private int maxRows = 0;
	
	
	private int extendColumnCount = 0;
	
	
	private int extendLength = 0;
	
	
	private int ColumnCount = 0;
	
	
	private int IndexLength = 0;
	
	
	public IndexStat(String table, String index, int count)
	{
		this.tableName = table;
		this.indexName = index;
		this.usedTimes = count;
	}
	
	

	
	
	public IndexStat(String table, String indexName, List<String> fields, StmtExplain stmt)
	{
		this.tableName = table;
		this.indexName = indexName;
		if(fields != null)
			this.fieldList = fields;
		addStat(stmt);
	}
	
	
	public void addStat(StmtExplain stmt)
	{
		if(stmtList == null)
			stmtList = new ArrayList<StmtExplain>();
		
		if(stmt != null && !stmtList.contains(stmt))
		{
			stmtList.add(stmt);
			usedTimes += stmt.getCount();
			if(maxRows < stmt.getKeyExplain().getMaxRows())
				maxRows = stmt.getKeyExplain().getMaxRows();
		}
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public List<StmtExplain> getStmtList() {
		return stmtList;
	}

	public String getTableName() {
		return tableName;
	}
	
	
	public String getIndexName() {
		if(indexName == null || indexName.trim().equals(""))
			return "";
		else 
			return indexName;
	}
	
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		IndexStat other = (IndexStat)otherObject;
		
		return tableName.equals(other.tableName) && getIndexName().equals(other.getIndexName()) && 
				(StringUtils.containAllIgnoreCase(getFieldList(), other.getFieldList()) && 
				StringUtils.containAllIgnoreCase(other.getFieldList(), getFieldList()));
	}
	
	
	public void merge(IndexStat other)
	{
		if(other == null || !tableName.equalsIgnoreCase(other.tableName) ||
				!getIndexName().equalsIgnoreCase(other.getIndexName()) || other.getStmtList() == null)
			return;
		

		
		for(StmtExplain stmt : other.getStmtList())
			addStat(stmt);
	}

	public int getUsedTimes() {
		return usedTimes;
	}
	
	
	public int getAvgRows()
	{
		int count = 0;
		int totalRows = 0;
		for (StmtExplain stmt : stmtList)
		{
			count += stmt.getKeyExplain().getCount();
			totalRows += stmt.getKeyExplain().getRows();
		}
		if(count == 0)
			return 0;
		else 
			return totalRows/count;
	}

	public int getMaxRows() {
		return maxRows;
	}
	
	public String toString()
	{
		String str = "index="+getIndexName()+", times="+usedTimes+", avgRows="+
		getAvgRows()+", maxRows="+maxRows+", extendColumnCount="+extendColumnCount+",extendLength="+extendLength;
		for(StmtExplain explain : stmtList)
			str += "\n"+explain;
		return str;
	}


	public int getExtendColumnCount() {
		return extendColumnCount;
	}

	public void setExpendColumnCount(int expendColumnCount) {
		this.extendColumnCount = expendColumnCount;
	}

	public int getExtendLength() {
		return extendLength;
	}

	public void setExtendLength(int extendLength) {
		this.extendLength = extendLength;
	}

	public int getColumnCount() {
		return ColumnCount;
	}

	public void setColumnCount(int columnCount) {
		ColumnCount = columnCount;
	}

	public int getIndexLength() {
		return IndexLength;
	}

	public void setIndexLength(int indexLength) {
		IndexLength = indexLength;
	}
}
