package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.util.Date;



public class AlarmInfo implements Serializable {
	private static final long serialVersionUID = 437610954840852083L;

	
	public static final byte ALARM_TYPE_SYSDB = 1;

	
	public static final byte ALARM_TYPE_MASTER = 2;

	
	public static final byte ALARM_TYPE_XAB = 4;

	
	public static final byte ALARM_TYPE_DBN = 5;

	
	public static final byte ALARM_TYPE_SLAVE_OUTTIME = 10;
	
	
	public static final byte ALARM_TYPE_PLAN = 11;

	
	public static final byte ALARM_TYPE_REP_FAIL = 12;

	
	public static final byte ALARM_TYPE_PROCESSLIST = 13;
	
	
	public static final byte ALARM_TYPE_QS = 14;
	
	
	public static final byte ALARM_TYPE_AUTOREPSWITCH = 15;
	
	
	public static final byte ALARM_TYPE_ONLINE_ALTER_TABLE = 16;

	
	public static final byte ALARM_LEVEL_FATAL = 1;

	
	public static final byte ALARM_LEVEL_ALARM = 2;

	
	public static final byte ALARM_LEVEL_ERROR = 3;

	
	public static final byte ALARM_LEVEL_WARNING = 4;

	
	public static final byte ALARM_LEVEL_INFO = 5;

	
	private String name;

	
	private byte type;

	
	private boolean inSysDB;

	
	private boolean sendMail;

	
	private boolean sendSM;
	
	
    private boolean invokeScript;

	
	private Date lastAlarmTime = new Date(0);

	
	private String description;

	
	public AlarmInfo(String name, byte type, boolean inSysDB, boolean sendMail,
			boolean sendSM, boolean invokeScript, String desc) {
		this.name = name;
		this.type = type;
		this.inSysDB = inSysDB;
		this.sendMail = sendMail;
		this.sendSM = sendSM;
		this.invokeScript = invokeScript;
		this.description = desc;
	}

	
	public boolean equals(Object otherObject) {
		if (this == otherObject)
			return true;

		if (otherObject == null)
			return false;

		if (this.getClass() != otherObject.getClass())
			return false;

		AlarmInfo other = (AlarmInfo) otherObject;

		return (this.type == other.type);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isInSysDB() {
		return inSysDB;
	}

	public void setInSysDB(boolean inSysDB) {
		this.inSysDB = inSysDB;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public boolean isSendSM() {
		return sendSM;
	}

	public void setSendSM(boolean sendSM) {
		this.sendSM = sendSM;
	}
	
	public boolean isInvokeScript() {
		return invokeScript;
	}

	public void setInvokeScript(boolean invokeScript) {
		this.invokeScript = invokeScript;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public Date getLastAlarmTime() {
		return lastAlarmTime;
	}

	public void setLastAlarmTime(Date lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
	}

}
