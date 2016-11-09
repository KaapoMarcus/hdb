package com.netease.backend.db.common.management;

import java.io.Serializable;

import com.netease.backend.db.common.schema.MigTaskInfo;

public class MasterStatus implements Serializable{
	
	private static final long serialVersionUID = 5216689743983714898L;

	
	private int migStatus = Cluster.MIG_STATUS_NORMAL;
	
	
	private int migXAOpt = Cluster.MIG_XA_FINISH;
	
	
	private int migType = MigTaskInfo.MIG_TYPE_ONLINE;
	
	
	private String migPolicy = "";
	
	
	private long assignedASID = 1001;
	
	
	private int assignedMigTaskID = 0;

	
	public MasterStatus()
	{
		
	}
	
	public long getAssignedASID() {
		return assignedASID;
	}

	public void setAssignedASID(long assignedASID) {
		this.assignedASID = assignedASID;
	}

	public String getMigPolicy() {
		return migPolicy;
	}

	public void setMigPolicy(String migPolicy) {
		this.migPolicy = migPolicy;
	}

	public int getMigStatus() {
		return migStatus;
	}

	public void setMigStatus(int migStatus) {
		this.migStatus = migStatus;
	}

	public int getMigType() {
		return migType;
	}

	public void setMigType(int migType) {
		this.migType = migType;
	}

	public int getMigXAOpt() {
		return migXAOpt;
	}

	public void setMigXAOpt(int migXAOpt) {
		this.migXAOpt = migXAOpt;
	}

	public int getAssignedMigTaskID() {
		return assignedMigTaskID;
	}

	public void setAssignedMigTaskID(int assignedMigTaskID) {
		this.assignedMigTaskID = assignedMigTaskID;
	}
}
