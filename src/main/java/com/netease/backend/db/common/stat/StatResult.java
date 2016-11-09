package com.netease.backend.db.common.stat;

import java.io.Serializable;



public class StatResult implements Serializable {

	private static final long serialVersionUID = -3534548504008740754L;

	
	private int taskId;
	
	
	private int resultId;
	
	
	private long startTime;
	
	
	private long produceTime;
	
	
	private String desc = "";
	
	public StatResult(int taskId, int resultId, long startTime, long produceTime)
	{
		this.taskId = taskId;
		this.resultId = resultId;
		this.startTime = startTime;
		this.produceTime = produceTime;
	}
	
	public String toString()
	{
		return "("+taskId+","+resultId+")";
	}
	
	public boolean equals(Object otherObject) {
		if (this == otherObject) {
			return true;
		}
		if (otherObject == null) {
			return false;
		}
		if (this.getClass() != otherObject.getClass()) {
			return false;
        }
		return ((StatResult) otherObject).taskId == taskId && ((StatResult) otherObject).resultId == resultId;
    }

	public long getProduceTime() {
		return produceTime;
	}

	public void setProduceTime(long produceTime) {
		this.produceTime = produceTime;
	}

	public int getResultId() {
		return resultId;
	}

	public int getTaskId() {
		return taskId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getStartTime() {
		return startTime;
	}
}
