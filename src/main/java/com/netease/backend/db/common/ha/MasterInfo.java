package com.netease.backend.db.common.ha;


public class MasterInfo {
	
	private String ip;

	
	private int dbiport;

	
	private int dbaport;

	public MasterInfo(String ip, int dbiport, int dbaport) {
		super();
		this.ip = ip;
		this.dbiport = dbiport;
		this.dbaport = dbaport;
	}

	public MasterInfo() {
		super();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getDbiport() {
		return dbiport;
	}

	public void setDbiport(int dbiport) {
		this.dbiport = dbiport;
	}

	public int getDbaport() {
		return dbaport;
	}

	public void setDbaport(int dbaport) {
		this.dbaport = dbaport;
	}

}
