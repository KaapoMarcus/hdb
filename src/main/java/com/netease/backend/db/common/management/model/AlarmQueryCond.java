package com.netease.backend.db.common.management.model;

import java.io.Serializable;


public class AlarmQueryCond extends QueryCond implements Serializable {
    private static final long serialVersionUID = -4824065904212823763L;
    
    
	private Operation[] alarmID;
	
	
	private Operation[] level;
	
	
	private Operation[] type;
	
	
	private Operation[] desc;
	
	
	private Operation[] time;
   
    public AlarmQueryCond() {
        super();
    }

	public Operation[] getAlarmID() {
		return alarmID;
	}

	public void setAlarmID(Operation[] alarmID) {
		this.alarmID = alarmID;
	}

	public Operation[] getLevel() {
		return level;
	}

	public void setLevel(Operation[] level) {
		this.level = level;
	}

	public Operation[] getTime() {
		return time;
	}

	public void setTime(Operation[] time) {
		this.time = time;
	}

	public Operation[] getType() {
		return type;
	}

	public void setType(Operation[] type) {
		this.type = type;
	}

	public Operation[] getDesc() {
		return desc;
	}

	public void setDesc(Operation[] desc) {
		this.desc = desc;
	}
    
}
