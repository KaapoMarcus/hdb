package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class DBNQueryCond extends QueryCond implements Serializable {
    private static final long serialVersionUID = 7653000837561497961L;
    
    
	private Operation[] dbnIP;
	
	
	private Operation[] sysCPUBusyRate;
	
	
	private Operation[] mySqlCPUUsedRate;
	
	
	private Operation[] sysMemUsedRate;
	
	
	private Operation[] sysTotalMem;
	
	
	private Operation[] sysFreeMem;
	
	
	private Operation[] mySqlMemUsedRate;
	
	
	private Operation[] mySqlUsedMem;
	
	
	private Operation[] time;

	
    public DBNQueryCond() {
        super();
    }

	public Operation[] getDbnIP() {
		return dbnIP;
	}

	public void setDbnIP(Operation[] dbnIP) {
		this.dbnIP = dbnIP;
	}
	
	public Operation[] getMySqlCPUUsedRate() {
		return mySqlCPUUsedRate;
	}

	public void setMySqlCPUUsedRate(Operation[] mySqlCPUUsedRate) {
		this.mySqlCPUUsedRate = mySqlCPUUsedRate;
	}

	public Operation[] getMySqlMemUsedRate() {
		return mySqlMemUsedRate;
	}

	public void setMySqlMemUsedRate(Operation[] mySqlMemUsedRate) {
		this.mySqlMemUsedRate = mySqlMemUsedRate;
	}

	public Operation[] getMySqlUsedMem() {
		return mySqlUsedMem;
	}

	public void setMySqlUsedMem(Operation[] mySqlUsedMem) {
		this.mySqlUsedMem = mySqlUsedMem;
	}

	public Operation[] getSysCPUBusyRate() {
		return sysCPUBusyRate;
	}

	public void setSysCPUBusyRate(Operation[] sysCPUBusyRate) {
		this.sysCPUBusyRate = sysCPUBusyRate;
	}

	public Operation[] getSysFreeMem() {
		return sysFreeMem;
	}

	public void setSysFreeMem(Operation[] sysFreeMem) {
		this.sysFreeMem = sysFreeMem;
	}

	public Operation[] getSysMemUsedRate() {
		return sysMemUsedRate;
	}

	public void setSysMemUsedRate(Operation[] sysMemUsedRate) {
		this.sysMemUsedRate = sysMemUsedRate;
	}

	public Operation[] getSysTotalMem() {
		return sysTotalMem;
	}

	public void setSysTotalMem(Operation[] sysTotalMem) {
		this.sysTotalMem = sysTotalMem;
	}
	
	public Operation[] getTime() {
		return time;
	}
	
	public void setTime(Operation[] t) {
		this.time = t;
	}
}
