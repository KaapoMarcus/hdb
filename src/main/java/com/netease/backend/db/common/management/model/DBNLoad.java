package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class DBNLoad implements Serializable {
	private static final long serialVersionUID = -2270212208481160324L;
	
	
	private String dbnIP;
	
	
	private float sysCpuBusyRate;
	
	
	private float mysqlCpuUsedRate;
	
	
	private float sysMemUsedRate;
	
	
	private long sysTotalMem;
	
	
	private long sysFreeMem;
	
	
	private float mysqlMemUsedRate;
	
	
	private long mysqlUsedMem;
	
	
	private long sysNetRecRate;
	
	
	private long sysNetTranRate;
	
	
	private String sysDiskPath;
	
	
	private long sysDiskTotal;
	
	
	private long sysDiskFree;
	
	
	private float sysDiskUsedRate;
	
	
	private float sysDiskReadRate;
	
	
	private float sysDiskWriteRate;
	
	
	private long time;

	public String getDbnIP() {
		return dbnIP;
	}

	public void setDbnIP(String dbnIP) {
		this.dbnIP = dbnIP;
	}

	public float getMysqlCpuUsedRate() {
		return mysqlCpuUsedRate;
	}

	public void setMysqlCpuUsedRate(float mysqlCpuUsedRate) {
		this.mysqlCpuUsedRate = mysqlCpuUsedRate;
	}

	public float getMysqlMemUsedRate() {
		return mysqlMemUsedRate;
	}

	public void setMysqlMemUsedRate(float mysqlMemUsedRate) {
		this.mysqlMemUsedRate = mysqlMemUsedRate;
	}

	public long getMysqlUsedMem() {
		return mysqlUsedMem;
	}

	public void setMysqlUsedMem(long mysqlUsedMem) {
		this.mysqlUsedMem = mysqlUsedMem;
	}

	public float getSysCpuBusyRate() {
		return sysCpuBusyRate;
	}

	public void setSysCpuBusyRate(float sysCpuBusyRate) {
		this.sysCpuBusyRate = sysCpuBusyRate;
	}

	public long getSysDiskFree() {
		return sysDiskFree;
	}

	public void setSysDiskFree(long sysDiskFree) {
		this.sysDiskFree = sysDiskFree;
	}

	public String getSysDiskPath() {
		return sysDiskPath;
	}

	public void setSysDiskPath(String sysDiskPath) {
		this.sysDiskPath = sysDiskPath;
	}

	public float getSysDiskReadRate() {
		return sysDiskReadRate;
	}

	public void setSysDiskReadRate(float sysDiskReadRate) {
		this.sysDiskReadRate = sysDiskReadRate;
	}

	public long getSysDiskTotal() {
		return sysDiskTotal;
	}

	public void setSysDiskTotal(long sysDiskTotal) {
		this.sysDiskTotal = sysDiskTotal;
	}

	public float getSysDiskUsedRate() {
		return sysDiskUsedRate;
	}

	public void setSysDiskUsedRate(float sysDiskUsedRate) {
		this.sysDiskUsedRate = sysDiskUsedRate;
	}

	public float getSysDiskWriteRate() {
		return sysDiskWriteRate;
	}

	public void setSysDiskWriteRate(float sysDiskWriteRate) {
		this.sysDiskWriteRate = sysDiskWriteRate;
	}

	public long getSysFreeMem() {
		return sysFreeMem;
	}

	public void setSysFreeMem(long sysFreeMem) {
		this.sysFreeMem = sysFreeMem;
	}

	public float getSysMemUsedRate() {
		return sysMemUsedRate;
	}

	public void setSysMemUsedRate(float sysMemUsedRate) {
		this.sysMemUsedRate = sysMemUsedRate;
	}

	public long getSysNetRecRate() {
		return sysNetRecRate;
	}

	public void setSysNetRecRate(long sysNetRecRate) {
		this.sysNetRecRate = sysNetRecRate;
	}

	public long getSysNetTranRate() {
		return sysNetTranRate;
	}

	public void setSysNetTranRate(long sysNetTranRate) {
		this.sysNetTranRate = sysNetTranRate;
	}

	public long getSysTotalMem() {
		return sysTotalMem;
	}

	public void setSysTotalMem(long sysTotalMem) {
		this.sysTotalMem = sysTotalMem;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
