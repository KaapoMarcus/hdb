package com.netease.backend.db.common.sql;

import java.util.List;


public class SAlterTableDropPartition extends SAlterTableOp {
	private static final long serialVersionUID = 8890559182059400633L;
	
	private List<String> pnames;
	
	public SAlterTableDropPartition(List<String> names) {
		this.pnames = names;
	}
	
	public List<String> getPnames() {
		return pnames;
	}
	
	public String toString() {
		return "drop partition " + pnames;
	}
}
