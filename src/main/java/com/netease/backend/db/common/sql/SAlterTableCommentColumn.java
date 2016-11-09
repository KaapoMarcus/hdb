package com.netease.backend.db.common.sql;


public class SAlterTableCommentColumn extends SAlterTableOp {
	private static final long serialVersionUID = 4863249091645838395L;
	
	private String columnName;
	private String newComment;
	
	public SAlterTableCommentColumn(String clnName, String newCmt) {
		this.columnName = clnName;
		this.newComment = newCmt;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getComment() {
		return newComment;
	}
	
	public void setComment(String comment) {
		this.newComment = comment;
	}
	
	public String toString() {
		return "comment on column " + columnName + " is '" + newComment + "'";
	}
}
