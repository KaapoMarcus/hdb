package com.netease.backend.db.common.sql;


public class SCleanSysTables extends Statement {
	private static final long serialVersionUID = 9067373805791898695L;
	
	private long alarmTime;
	private long dbnTime;
	private long xaTime;
	
	public SCleanSysTables() {
		
	}
	
	public long getAlarmTime() {
		return alarmTime;
	}
	
	public void setAlarmTime(long t) {
		alarmTime = t;
	}
	
	public long getDbnTime() {
		return dbnTime;
	}
	
	public void setDbnTime(long t) {
		dbnTime = t;
	}
	
	public long getXaTime() {
		return xaTime;
	}
	
	public void setXaTime(long t) {
		xaTime = t;
	}
}
