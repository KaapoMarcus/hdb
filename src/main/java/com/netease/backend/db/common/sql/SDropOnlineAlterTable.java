package com.netease.backend.db.common.sql;

import java.util.List;


public class SDropOnlineAlterTable extends Statement {
	private static final long serialVersionUID = 6133079526703271653L;
	private List<Long> taskIDs;
	
	public SDropOnlineAlterTable(List<Long> ids) {
		super();
		this.taskIDs = ids;
	}
	
	public List<Long> getTaskIDs() {
		return this.taskIDs;
	}
}

