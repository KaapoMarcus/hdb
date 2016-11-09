package com.netease.backend.db.common.schema.cloud;

import java.io.Serializable;
import java.util.List;


public class TableDetail implements Serializable {

	private static final long serialVersionUID = -7022838861410300056L;

	
	private Table table;

	
	private List<Column> columnList;

	
	private List<Index> indexList;

	public TableDetail(Table table, List<Column> columnList,
			List<Index> indexList) {
		this.table = table;
		this.columnList = columnList;
		this.indexList = indexList;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public List<Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<Column> columnList) {
		this.columnList = columnList;
	}

	public List<Index> getIndexList() {
		return indexList;
	}

	public void setIndexList(List<Index> indexList) {
		this.indexList = indexList;
	}
}
