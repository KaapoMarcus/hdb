package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class ColumnUsage implements Serializable {
	private static final long serialVersionUID = 8334687300244469304L;
	
	
	private List<String> columnList = new LinkedList<String>();
	
	private List<String> selectColumnList = new LinkedList<String>();
	
	private List<String> joinColumnList = new LinkedList<String>();
	
	private List<String> equalColumnList = new LinkedList<String>();
	
	private List<String> rangeColumnList = new LinkedList<String>();
	
	private List<String> groupByColumnList = new LinkedList<String>();
	
	private List<String> orderByColumnList = new LinkedList<String>();
	
	public ColumnUsage() {
		columnList = new LinkedList<String>();
		selectColumnList = new LinkedList<String>();
		joinColumnList = new LinkedList<String>();
		equalColumnList = new LinkedList<String>();
		rangeColumnList = new LinkedList<String>();
		groupByColumnList = new LinkedList<String>();
		orderByColumnList = new LinkedList<String>();
	}
	
	public List<String> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<String> columns) {
		this.columnList = columns;
	}
	public List<String> getSelectColumnList() {
		return selectColumnList;
	}
	public void setSelectColumnList(List<String> selectColumnList) {
		this.selectColumnList = selectColumnList;
	}
	public List<String> getJoinColumnList() {
		return joinColumnList;
	}
	public void setJoinColumnList(List<String> joinColumnList) {
		this.joinColumnList = joinColumnList;
	}
	public List<String> getEqualColumnList() {
		return equalColumnList;
	}
	public void setEqualColumnList(List<String> equalColumnList) {
		this.equalColumnList = equalColumnList;
	}
	public List<String> getRangeColumnList() {
		return rangeColumnList;
	}
	public void setRangeColumnList(List<String> rangeColumnList) {
		this.rangeColumnList = rangeColumnList;
	}
	public List<String> getGroupByColumnList() {
		return groupByColumnList;
	}
	public void setGroupByColumnList(List<String> groupByColumnList) {
		this.groupByColumnList = groupByColumnList;
	}
	public List<String> getOrderByColumnList() {
		return orderByColumnList;
	}
	public void setOrderByColumnList(List<String> orderByColumnList) {
		this.orderByColumnList = orderByColumnList;
	}
}
