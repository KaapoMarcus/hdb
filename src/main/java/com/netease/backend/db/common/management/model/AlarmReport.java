package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class AlarmReport implements Serializable {
	private static final long serialVersionUID = 4122031055243675232L;
	
	
	private int alarmID;
	
	
	private byte level;
	
	
	private byte type;
	
	
	private String description;
	
	
	private long time;
	
	public int getAlarmID() {
		return alarmID;
	}
	public void setAlarmID(int alarmID) {
		this.alarmID = alarmID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
}
