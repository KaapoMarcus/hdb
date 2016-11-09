package com.netease.backend.db.common.sql;


public class SAlterTableComment extends SAlterTableOp {

	private static final long serialVersionUID = 5688497053503533299L;
	
	private String newComment;
	
	public SAlterTableComment(String comment){
		if (null != comment)
			this.newComment = comment;
		else 
			this.newComment = "";
		
	}
	
	public String getNewTableComment() {
		return this.newComment;
	}
	
	public String toString() {
	    	return "comment='" + this.newComment + "'";
	    }
	
	public boolean equals(Object obj) {
    	if (!(obj instanceof SAlterTableComment))
    		return false;
    	return this.newComment.equals(((SAlterTableComment) obj).newComment);
    }
}
