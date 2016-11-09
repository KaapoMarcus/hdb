package com.netease.backend.db.common.schema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.netease.backend.db.common.exceptions.OnlineAlterTableException;
import com.netease.backend.db.common.sql.SAlterTable;
import com.netease.backend.db.common.sql.Statement;


public class OnlineAlterTaskInfo extends Statement {
	private static final long serialVersionUID = 1L;
	
	
	public static final int STATUS_BEGIN = 1;

	
	public static final int STATUS_PREPARE = 2;

	
	public static final int STATUS_PAUSE = 3;

	
	public static final int STATUS_CANCEL = 4;

	
	public static final int STATUS_COPYING = 5;

	
	public static final int STATUS_FAILED = 6;

	
	public static final int STATUS_COPIED = 7;

	
	public static final int STATUS_ONLINING = 8;

	
	public static final int STATUS_ONLINE_FAILED = 9;

	
	public static final int STATUS_FINISH = 10;
	
	
	public static final String GHOST_TABLE_PREFIX = "__oak_";

	
	private long id = -1;

	
	private int trunksize = 1000;

	
	private int sleeptime = 100;

	
	private String sql = "";

	
	private boolean ignoreConnErr = true;

	
	private boolean ignoreClientErr = false;

	
	private String tableName = "";

	
	private int status = STATUS_BEGIN;

	
	private List<OnlineAlterTaskDb> dbList = new ArrayList<OnlineAlterTaskDb>();

	
	private boolean ignore = false;
	
	
	private boolean isBinLog = false;
	
	
	private Date createTime;
	
	
	private Date lastUpdateTime;
	
	public OnlineAlterTaskInfo(int trunksize, int sleeptime, String sql,
			boolean ignoreConnErr, boolean ignoreClientErr, int status,
			String tableName, List<OnlineAlterTaskDb> dbList, boolean ignore,
			boolean isBinLog) {
		super();
		this.trunksize = trunksize;
		this.sleeptime = sleeptime;
		this.sql = sql;
		this.ignoreConnErr = ignoreConnErr;
		this.ignoreClientErr = ignoreClientErr;
		this.status = status;
		this.tableName = tableName;
		this.dbList = dbList;
		this.ignore = ignore;
		this.isBinLog = isBinLog;
	}

	public OnlineAlterTaskInfo() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTrunksize() {
		return trunksize;
	}

	public void setTrunksize(int trunksize) {
		this.trunksize = trunksize;
	}

	public int getSleeptime() {
		return sleeptime;
	}

	public void setSleeptime(int sleeptime) {
		this.sleeptime = sleeptime;
	}

	public boolean isIgnoreConnErr() {
		return ignoreConnErr;
	}

	public void setIgnoreConnErr(boolean ignoreConnErr) {
		this.ignoreConnErr = ignoreConnErr;
	}

	public boolean isIgnoreClientErr() {
		return ignoreClientErr;
	}

	public void setIgnoreClientErr(boolean ignoreClientErr) {
		this.ignoreClientErr = ignoreClientErr;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTableName() {
		return tableName;
	}
	
	public String getGhostTableName() {
		return GHOST_TABLE_PREFIX + tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<OnlineAlterTaskDb> getDbList() {
		return dbList;
	}

	public void setDbList(List<OnlineAlterTaskDb> dbList) {
		this.dbList = dbList;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
	public String getSql() {
		return this.sql;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public boolean isBinLog() {
		return isBinLog;
	}

	public void setBinLog(boolean isBinLog) {
		this.isBinLog = isBinLog;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	
	public String getDbnDesc(Map<String, Integer> processStatus) {
		if (dbList == null || dbList.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		String line = System.getProperty("line.separator");
		for (OnlineAlterTaskDb db : dbList) {
			Integer status = null;
			if (processStatus != null) {
				status = processStatus.get(db.getUrl());
			}
			sb.append(db.getDesc(status));
			sb.append(line);
		}

		return sb.toString();
	}

	
	public static String getStatusDesc(int status) {
		String statusStr;
		switch (status) {
		case OnlineAlterTaskInfo.STATUS_BEGIN: {
			statusStr = "δ��ʼ";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_PREPARE: {
			statusStr = "׼���������";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_COPIED: {
			statusStr = "�������";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_FINISH: {
			statusStr = "��������";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_PAUSE: {
			statusStr = "������ͣ";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_CANCEL: {
			statusStr = "��ȡ��";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_COPYING: {
			statusStr = "����������";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_ONLINING: {
			statusStr = "���ϲ���������";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_FAILED: {
			statusStr = "ʧ��";
			break;
		}
		case OnlineAlterTaskInfo.STATUS_ONLINE_FAILED: {
			statusStr = "���ϲ���ʧ��";
			break;
		}
		default: {
			statusStr = "δ֪״̬";
		}
		}
		return statusStr;
	}

	
	public void convertFromSAT(SAlterTable sat) {
		tableName = sat.getTableName();
		ignore = sat.isIgnore();
		sql = sat.getSql();
		this.setDdbName(sat.getDdbName());
	}
	
	
	public void convertDbList(List<Database> dbs){
		for (Database db : dbs) {
			dbList.add(new OnlineAlterTaskDb(db.getURL(), STATUS_BEGIN));
		}
	}


	public void check() throws OnlineAlterTableException {
		if (trunksize <= 0) {
			throw new OnlineAlterTableException(
					"check OnlineAlterTable task failed, trunksize must be positive");
		}
		if (sleeptime <= 0) {
			throw new OnlineAlterTableException(
					"check OnlineAlterTable task failed, sleeptime must be positive");
		}
		if (sql == null || sql.length() < 1) {
			throw new OnlineAlterTableException(
					"check OnlineAlterTable task failed, alter table sql statement can not be empty");
		}
		if (tableName.length() == 0) {
			throw new OnlineAlterTableException(
					"check OnlineAlterTable task failed, tableName can not be empty");
		}
		if (status < STATUS_BEGIN) {
			throw new OnlineAlterTableException(
					"check OnlineAlterTable task failed, status not qulified");
		}
		if (dbList == null || dbList.size() == 0) {
			throw new OnlineAlterTableException(
					"check OnlineAlterTable task failed, dblist can not be empty");
		}

	}
}
