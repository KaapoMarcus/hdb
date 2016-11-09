package com.netease.backend.db.common.sql;

import java.util.List;


public class SRemoveStatResult extends Statement {
	private static final long serialVersionUID = 3493123946322650664L;
	
	private int taskId;
	private List<Integer> resultIds;
	
	public SRemoveStatResult(int taskId, List<Integer> resultIds) {
		this.taskId = taskId;
		this.resultIds = resultIds;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public List<Integer> getResultIds() {
		return resultIds;
	}
}
