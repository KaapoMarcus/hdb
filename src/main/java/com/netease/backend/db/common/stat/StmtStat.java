package com.netease.backend.db.common.stat;

import java.io.Serializable;
import java.util.List;


public abstract class StmtStat implements Serializable {
	private static final long serialVersionUID = 139169632641759882L;

	
	protected StatSig signature;

	
	protected long count;

	
	protected long time = 0;

	
	protected List<String> tableList;

	
	protected int lockWaitTimeoutCount = 0;

	
	protected int deadLockCount = 0;

	StmtStat() {
		this.count = 1;
	}

	StmtStat(StatSig sig) {
		signature = sig;
		this.count = 1;
	}

	public void merge(StmtStat s) {
		count += s.count;
		time += s.time;
		lockWaitTimeoutCount += s.lockWaitTimeoutCount;
		deadLockCount += s.deadLockCount;
	}

	public long getCount() {
		return count;
	}

	public long getTime() {
		return time;
	}

	public double getAvgTime() {
		if (count == 0)
			return 0;
		else
			return (double) time / count;
	}

	public StatSig getSignature() {
		return signature;
	}

	public String getSql() {
		return signature.getSqlSig();
	}

	public String getPlan() {
		return signature.getPlanSkeleton();
	}

	public abstract String getTypeStr();

	public void setTime(long executeTime) {
		this.time = executeTime;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public String getTableListStr() {
		String s = null;
		for (String tableName : tableList) {
			if (s != null)
				s += ", " + tableName;
			else
				s = tableName;
		}
		return s;
	}

	
	public void setStatSig(StatSig sig) {
		this.signature = sig;
	}

	public long getDeadLockCount() {
		return deadLockCount;
	}

	public void setDeadLockCount(int deadLockCount) {
		this.deadLockCount = deadLockCount;
	}

	public long getLockWaitTimeoutCount() {
		return lockWaitTimeoutCount;
	}

	public void setLockWaitTimeoutCount(int lockWaitTimeoutCount) {
		this.lockWaitTimeoutCount = lockWaitTimeoutCount;
	}

}
