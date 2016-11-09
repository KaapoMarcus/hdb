package com.netease.backend.db.common.sql;



public class SDiffStat extends Statement {
	private static final long serialVersionUID = 1L;
	
	private int resultId1;
	private int taskId1;
	
	private int resultId2;
	private int taskId2;
	
	public SDiffStat(int resultId1, int task1, int resultId2, int task2) {
		this.resultId1 = resultId1;
		this.taskId1 = task1;
		this.resultId2 = resultId2;
		this.taskId2 = task2;
	}
	
	public int getResultId1() {
		return resultId1;
	}
	
	public int getTaskId1() {
		return taskId1;
	}
	
	public int getResultId2() {
		return resultId2;
	}
	
	public int getTaskId2() {
		return taskId2;
	}
}
