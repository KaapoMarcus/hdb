package com.netease.backend.db.common.sql;


public class SAlterTriggerSetEnabled extends SAlterTrigger {
	private static final long serialVersionUID = 1964036195157870333L;
	
	private boolean enabled;
	
	public SAlterTriggerSetEnabled(String name) {
		super(name);
	}
	
	public boolean getEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean b) {
		this.enabled = b;
	}
	
}
