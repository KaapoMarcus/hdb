package com.netease.backend.db.common.sql;


public class SChangeColumnComment extends Statement {
	private static final long serialVersionUID = 4577847603664803622L;
	
	private String tableName;
	private String columnName;
	private String newComment;
	
	public SChangeColumnComment(String tblName, String clnName, String newCmt) {
		this.tableName = tblName;
		this.columnName = clnName;
		this.newComment = newCmt;
	}
	
	public String getTblName() {
		return tableName;
	}
	public String getClnName() {
		return columnName;
	}
	public String getComment() {
		return newComment;
	}

}
