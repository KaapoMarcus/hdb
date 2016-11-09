package com.netease.backend.db.common.sql;

public class SStopDbn extends Statement {
    private static final long serialVersionUID = 7587882495427888176L;
    
    private String name;
	private String reason;
	
	public SStopDbn(String name, String reason) {
		super();
		this.name = name;
		this.reason = reason;
	}
	
	public String getName() {
		return name;
	}
	public String getReason() {
		return reason;
	}
	
}
