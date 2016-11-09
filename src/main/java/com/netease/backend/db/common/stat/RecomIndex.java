package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.schema.IndexInfo;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.utils.StringUtils;


public class RecomIndex implements Serializable{
	private static final long serialVersionUID = 3181986001693294980L;
	
	
	private String tableName = "";
	
	
	transient private TableInfo table;
	
	
	private String indexName = "";
	
	
	private String indexDesc = "";
	
	
	private List<String> joinColumnList = new LinkedList<String>();
	
	
	private List<String> equalColumnList = new LinkedList<String>();
	
	
	private List<String> sortColumnList = new LinkedList<String>();
	
	
	private List<String> scopeColumnList = new LinkedList<String>();
	
	
	private List<String> selectColumnList = new LinkedList<String>();
	
	
	private List<StmtExplain> stmtList = new ArrayList<StmtExplain>();
	
	
	private boolean meetJoin = true;
	
	
	private boolean meetEqual = true;
	
	
	private boolean meetSort = true;
	
	
	private boolean meetScope = true;
	
	
	private boolean meetSelect = true;
	
	
	private int indexLength = 0;
	
	
	private int lengthLimit = 0;
	
	
	private int maxScanRows = 0;
	
	
	private int usedTimes = 0;
	
	
	public RecomIndex(TableInfo table, int lengthLimit, Collection<String> joinColumns, Collection<String> equalColumns, 
			Collection<String> sortColumns, Collection<String> scopeColumns, Collection<String> selectColumns, StmtExplain stmt)
	{
		this.table = table;
		this.tableName = table.getName();
		this.lengthLimit = lengthLimit;
		addStat(stmt);
		addJoinColumns(joinColumns);
		addEqualColumns(equalColumns);
		addSortColumns(sortColumns);
		addScopeColumns(scopeColumns);
		addSelectColumns(selectColumns);
		if(stmt != null)
			stmt.setOptimizeDesc(getOptimizeDesc());
	}

	
	public void addJoinColumns(Collection<String> joinColumns) 
	{
		if(joinColumns == null || joinColumns.size()==0)
			return;
		for(String columnName: joinColumns)
		{
			ColumnInfo column = table.getColumnInfo(columnName);
			if(!joinColumnList.contains(column.getName()))
			{
				if(lengthLimit > 0 && indexLength + column.getSize() > lengthLimit)
				{
					meetJoin = false;
					break;
				}
				joinColumnList.add(column.getName());
				indexLength += column.getSize();
			}
		}
		Collections.sort(joinColumnList);
		indexName += StringUtils.convertToString(joinColumnList, ", ");
		if(joinColumnList.size()>0)
			indexDesc += "JOIN["+StringUtils.convertToString(joinColumnList, ", ")+"]  ";
	}
	
	
	public void addEqualColumns(Collection<String> equalColumns) 
	{
		if(equalColumns == null || equalColumns.size()==0)
			return;
		List<String> balanceFields = table.getBalanceFields();
		for(String columnName: equalColumns)
		{
			ColumnInfo column = table.getColumnInfo(columnName);
			if(!joinColumnList.contains(column.getName()) && !equalColumnList.contains(column.getName()))
			{
				if(lengthLimit > 0 && indexLength + column.getSize() > lengthLimit)
				{
					meetEqual = false;
					break;
				}
				if(StringUtils.containIgnoreCase(balanceFields, column.getName()))		
					equalColumnList.add(0,column.getName());
				else
					equalColumnList.add(column.getName());
				indexLength += column.getSize();
			}
		}
		Collections.sort(equalColumnList);
		String equalColumnStr = StringUtils.convertToString(equalColumnList, ", ");
		if(indexName.length() > 0 && equalColumnStr.length()>0)
			indexName += ", ";
		indexName += equalColumnStr;
		if(equalColumnList.size()>0)
			indexDesc += "EQUAL["+StringUtils.convertToString(equalColumnList, ", ")+"]  ";
	}
	
	
	public void addSortColumns(Collection<String> sortColumns) 
	{
		if(sortColumns == null || sortColumns.size()==0)
			return;
		for(String columnName : sortColumns)
		{
			ColumnInfo column = table.getColumnInfo(columnName);
			if(!joinColumnList.contains(column.getName()) && !equalColumnList.contains(column.getName()) && 
					!sortColumnList.contains(column.getName()))
			{
				if(lengthLimit > 0 && indexLength + column.getSize() > lengthLimit)
				{
					meetSort = false;
					break;
				}
				sortColumnList.add(column.getName());
				indexLength += column.getSize();
			}
		}
		
		String sortColumnStr = StringUtils.convertToString(sortColumnList, ", ");
		if(indexName.length() > 0 && sortColumnStr.length()>0)
			indexName += ", ";
		indexName += sortColumnStr;
		if(sortColumnList.size()>0)
			indexDesc += "SORT["+StringUtils.convertToString(sortColumnList, ", ")+"]  ";
	}
	
	
	public void addScopeColumns(Collection<String> scopeColumns) 
	{
		if(scopeColumns == null || scopeColumns.size()==0)
			return;
		for(String columnName: scopeColumns)
		{
			ColumnInfo column = table.getColumnInfo(columnName);
			if(!joinColumnList.contains(column.getName()) && !equalColumnList.contains(column.getName()) && 
					!sortColumnList.contains(column.getName()) && !scopeColumnList.contains(column.getName()))
			{
				if(lengthLimit > 0 && indexLength + column.getSize() > lengthLimit)
				{
					meetScope = false;
					break;
				}
				scopeColumnList.add(column.getName());
				indexLength += column.getSize();
			}
		}
		Collections.sort(sortColumnList);
		String scopeColumnStr = StringUtils.convertToString(scopeColumnList, ", ");
		if(indexName.length() > 0 && scopeColumnStr.length()>0)
			indexName += ", ";
		indexName += scopeColumnStr;
		if(scopeColumnList.size()>0)
			indexDesc += "SCOPE["+StringUtils.convertToString(scopeColumnList, ", ")+"]  ";
	}
	
	
	
	public void addSelectColumns(Collection<String> selectColumns) 
	{
		if(selectColumns == null || selectColumns.size()==0)
			return;
		int selectLength = 0;
		IndexInfo primary = table.getPrimaryKey();
		for(String columnName: selectColumns)
		{
			ColumnInfo column = table.getColumnInfo(columnName);
			if(!joinColumnList.contains(column.getName()) && !equalColumnList.contains(column.getName()) && 
					!sortColumnList.contains(column.getName()) && !scopeColumnList.contains(column.getName()) &&
					!selectColumnList.contains(column.getName()))
			{
				if(primary != null && primary.getColumnNameList().contains(column.getName()))		
					continue;
				if(lengthLimit > 0 && indexLength + column.getSize() > lengthLimit)
				{
					selectColumnList.clear();
					meetSelect = false;
					return;
				}
				selectColumnList.add(column.getName());
				selectLength += column.getSize();
			}
		}
		indexLength += selectLength;
		Collections.sort(selectColumnList);
		String selectColumnStr = StringUtils.convertToString(selectColumnList, ", ");
		if(indexName.length() > 0 && selectColumnStr.length()>0)
			indexName += ", ";
		indexName += selectColumnStr;
		if(selectColumnList.size()>0)
			indexDesc += "SELECT["+StringUtils.convertToString(selectColumnList, ", ")+"]  ";
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		RecomIndex other = (RecomIndex)otherObject;
		
		return tableName.equals(other.tableName) && getIndexName().equals(other.getIndexName()) &&
				getIndexDesc().equals(other.getIndexDesc());
	}

	
	
	public void addStat(StmtExplain stmt)
	{
		if(stmtList == null)
			stmtList = new ArrayList<StmtExplain>();
		
		if(stmt != null && !stmtList.contains(stmt))
		{
			stmtList.add(stmt);
			usedTimes += stmt.getCount();
			if(maxScanRows < stmt.getKeyExplain().getMaxRows())
				maxScanRows = stmt.getKeyExplain().getMaxRows();
		}
	}
	
	
	public void merge(RecomIndex other)
	{
		if(other == null || !tableName.equalsIgnoreCase(other.getTableName()) ||
				!getIndexName().equalsIgnoreCase(other.getIndexName()) || 
				!getIndexDesc().equalsIgnoreCase(other.getIndexDesc()) || other.getStmtList() == null)
			return;
		
		for(StmtExplain stmt : other.getStmtList())
			addStat(stmt);
	}
	

	public int getIndexLength() {
		return indexLength;
	}

	public void setIndexLength(int indexLength) {
		this.indexLength = indexLength;
	}

	public boolean isMeetEqual() {
		return meetEqual;
	}

	public void setMeetEqual(boolean meetEqual) {
		this.meetEqual = meetEqual;
	}

	public boolean isMeetJoin() {
		return meetJoin;
	}

	public void setMeetJoin(boolean meetJoin) {
		this.meetJoin = meetJoin;
	}

	public boolean isMeetSort() {
		return meetSort;
	}

	public void setMeetSort(boolean meetSort) {
		this.meetSort = meetSort;
	}

	public boolean isMeetScope() {
		return meetScope;
	}

	public void setMeetScope(boolean meetScope) {
		this.meetScope = meetScope;
	}

	public boolean isMeetSelect() {
		return meetSelect;
	}

	public void setMeetSelect(boolean meetSelect) {
		this.meetSelect = meetSelect;
	}

	public List<StmtExplain> getStmtList() {
		return stmtList;
	}

	public void setStmtList(List<StmtExplain> stmtList) {
		this.stmtList = stmtList;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getEqualColumnList() {
		return equalColumnList;
	}

	public List<String> getJoinColumnList() {
		return joinColumnList;
	}

	public List<String> getOrderColumnList() {
		return sortColumnList;
	}

	public List<String> getScopeColumnList() {
		return scopeColumnList;
	}

	public List<String> getSelectColumnList() {
		return selectColumnList;
	}

	public String getIndexDesc() {
		return indexDesc;
	}

	public String getIndexName() {
		return indexName;
	}
	
	public String toString()
	{
		return "TABLE["+tableName+"]  INDEX["+indexName+"]  "+indexDesc;
	}

	public int getMaxScanRows() {
		return maxScanRows;
	}
	
	
	private String getOptimizeDesc()
	{
		String desc = "";
		if(meetJoin && meetEqual && meetSort && meetScope && meetSelect)
			desc += "Using Index";
		if(!meetScope)
			desc += "File sort";
		if(!meetJoin || !meetEqual || !meetScope)
			desc += "Using where";
		return desc;
	}

	public int getUsedTimes() {
		return usedTimes;
	}
}
