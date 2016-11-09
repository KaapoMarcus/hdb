package com.netease.backend.db.common.sql;

import java.util.List;


public class SShowStatResults extends Statement {
	private static final long serialVersionUID = 42749674399589566L;
	
	private List<Integer> taskIds;
	
	public SShowStatResults(List<Integer> taskIDs) {
		this.taskIds = taskIDs;
	}
	
	public List<Integer> getTaskIds() {
		return taskIds;
	}
}
