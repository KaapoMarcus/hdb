package com.netease.backend.db.common.sql;


public class SSetQuota extends Statement {
    private static final long serialVersionUID = -8201803065037681353L;
    
    private String user;
	private long quota;
	private long slaveQuota;
	
	public SSetQuota(String user, long quota) {
		this.user = user;
		this.quota = quota;
		this.slaveQuota = -1;
	}

	public String getUser() {
		return user;
	}

	public long getQuota() {
		return quota;
	}
	
	public long getSlaveQuota() {
		return this.slaveQuota;
	}
	
	public void setSlaveQuota(long q) {
		this.slaveQuota = q;
	}
}
