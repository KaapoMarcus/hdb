package com.netease.backend.db.common.sql;


public class SSetSlaveAutoSwitch extends Statement {

	private static final long serialVersionUID = 4167013430781456069L;

	private String slaveName;
	private boolean isAutoSwitch;
	
	public SSetSlaveAutoSwitch(String name, boolean isAutoSwitch) {
		super();
		this.slaveName = name;
		this.isAutoSwitch = isAutoSwitch;
	}

	public String getSlaveName() {
		return slaveName;
	}

	public void setSlaveName(String slaveName) {
		this.slaveName = slaveName;
	}

	public boolean isAutoSwitch() {
		return isAutoSwitch;
	}

	public void setAutoSwitch(boolean isAutoSwitch) {
		this.isAutoSwitch = isAutoSwitch;
	}
	
}
