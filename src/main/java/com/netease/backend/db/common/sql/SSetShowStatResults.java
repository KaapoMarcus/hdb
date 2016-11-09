package com.netease.backend.db.common.sql;

import java.util.List;


public class SSetShowStatResults extends Statement {
	private static final long serialVersionUID = 1;
	
	private List<Integer> taskIds;
	private List<List<Integer>> resultIds;
	
	public SSetShowStatResults(List<Integer> taskIds, List<List<Integer>> resultIds) {
		this.taskIds = taskIds;
		this.resultIds = resultIds;
	}
	
	public List<Integer> getTaskIds() {
		return taskIds;
	}
	
	public List<List<Integer>> getResultIds() {
		return resultIds;
	}
}
