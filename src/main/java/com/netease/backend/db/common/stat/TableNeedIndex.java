package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class TableNeedIndex implements Serializable{

	private static final long serialVersionUID = 1862770139757036337L;

	
	private String tableName;
	
	
	private HashMap<String, RecomIndex> indexes = new HashMap<String, RecomIndex> ();
	
	
	public TableNeedIndex(String table)
	{
		this.tableName = table;
	}
	
	
	public void addRecomIndex(RecomIndex index)
	{
		if(index == null || !index.getTableName().equals(tableName))
			return;
		
		RecomIndex oldIndex = indexes.get(index.getIndexDesc());
		if(oldIndex == null)	
			indexes.put(index.getIndexDesc(), index);
		else
			oldIndex.merge(index);
	}

	public List<RecomIndex> getIndexes() {
		List<RecomIndex> indexList = new LinkedList<RecomIndex>();
		indexList.addAll(indexes.values());
		Collections.sort(indexList, new Comparator<RecomIndex>() {
			public int compare(RecomIndex index1, RecomIndex index2) {
				return index2.getIndexName().compareTo(index1.getIndexName());
		      }
		    });
		return indexList;
	}

	public String getTableName() {
		return tableName;
	}
	
	public String toString()
	{
		String str = "table="+tableName;
		for(RecomIndex index : getIndexes())
			str += "\n"+index.toString();		
		return str;
	}
	
	
	public boolean equals(Object otherObject)
	{
		if(this==otherObject) return true;
		
		if(otherObject==null) return false;
		
		if(this.getClass()!=otherObject.getClass()) return false;
		
		TableNeedIndex other = (TableNeedIndex)otherObject;
		
		return tableName.equals(other.tableName) ;
	}

}
