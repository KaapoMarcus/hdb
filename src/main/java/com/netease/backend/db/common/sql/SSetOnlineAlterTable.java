package com.netease.backend.db.common.sql;


public class SSetOnlineAlterTable extends Statement {
	private static final long serialVersionUID = -6793050432034062192L;
	
	private long taskID;
	
	private int trunksize;
	
	private int sleeptime;
	
	public SSetOnlineAlterTable(long taskID, int trunksize, int sleeptime) {
		super();
		this.taskID = taskID;
		this.trunksize = trunksize;
		this.sleeptime = sleeptime;
	}

	public long getTaskID() {
		return this.taskID;
	}

	public int getTrunksize() {
		return trunksize;
	}

	public int getSleeptime() {
		return sleeptime;
	}
}

