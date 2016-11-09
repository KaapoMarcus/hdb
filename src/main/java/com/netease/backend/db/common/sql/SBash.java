package com.netease.backend.db.common.sql;


public class SBash extends Statement {
	private static final long serialVersionUID = -944332290599068464L;
	
	private String commands;
	
	public SBash(String commands) {
		super();
		this.commands = commands;
	}
	
	public String getCommands() {
		return this.commands;
	}
}
