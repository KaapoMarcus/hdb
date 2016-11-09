
package com.netease.backend.db.common.management;

import java.io.Serializable;

import com.netease.backend.db.common.utils.TimeUtils;


public class DBNQueryDisabledCondition implements Serializable {
	
	private static final long serialVersionUID = 8041734943744278741L;

	
	private String user = "";

	
	private String sqlExp = "";

	
	private int reportStartTime = 0;
	
	private int reportEndTime = 86399;

	private String desctiption;

	
	public DBNQueryDisabledCondition() {
	}

	
	public DBNQueryDisabledCondition(String str) {
		if (null == str)
			throw new IllegalArgumentException(
			"DBN Processlist disabled condition is invalid, condition is null.");
		String s = str.trim();
		String[] strArray = new String[5];
		int pos = s.indexOf(':');
		int i = 0;
		while (pos >= 0) {
			if (i == 5)
				break;
			strArray[i] = s.substring(0, pos);
			s = s.substring(pos + 1);
			pos = s.indexOf(':');
			i++;
		}
		if (i == 4)
			strArray[4] = s;
		if (i < 4)
			throw new IllegalArgumentException(
					"DBN Processlist disabled condition '" + str + "' is invalid.");
		try {
			this.user = strArray[0];
			this.setReportStartTime(Integer.valueOf(strArray[1]));
			this.setReportEndTime(Integer.valueOf(strArray[2]));
			this.setDesctiption(strArray[3]);
			this.setSqlExp(strArray[4]);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"DBN Processlist disabled condition '" + str
					+ "' is invalid, " + e.getMessage());
		}
	}


	
	public String getUser() {
		return user == null ? "" : user;
	}

	
	public void setUser(String user) {
		this.user = user;
	}

	
	public String getSqlExp() {
		return sqlExp == null ? "(.)*" : sqlExp;
	}

	
	public void setSqlExp(String sqlExp) {
		if (null == sqlExp || "".equals(sqlExp))
			throw new IllegalArgumentException("Sql expression is empty.");
		this.sqlExp = sqlExp;
	}

	
	public int getReportStartTime() {
		return reportStartTime;
	}

	
	public void setReportStartTime(int reportStartTime) {
		if (reportStartTime < 0 || reportStartTime >= 86400)
			throw new IllegalArgumentException("����ʱ��ο�ʼʱ��ȡֵ��Χ��[0,86400)��Ŀǰ����Ϊ"
					+ reportStartTime);
		this.reportStartTime = reportStartTime;
	}

	
	public int getReportEndTime() {
		return reportEndTime;
	}

	
	public void setReportEndTime(int reportEndTime) {
		if (reportEndTime < 0 || reportEndTime >= 86400)
			throw new IllegalArgumentException("����ʱ��ν���ʱ��ȡֵ��Χ��[0,86400)��Ŀǰ����Ϊ"
					+ reportEndTime);
		this.reportEndTime = reportEndTime;
	}

	
	public String getDesctiption() {
		return desctiption == null ? "" : desctiption;
	}

	
	public void setDesctiption(String desctiption) {
		this.desctiption = desctiption;
	}

	
	public String toDisplayString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getUser()).append(":[").append(
				TimeUtils.getTimeFromInt(reportStartTime)).append("~").append(
				TimeUtils.getTimeFromInt(reportEndTime)).append("]:").append(
				sqlExp);
		return sb.toString();
	}
	
	public String toString() {
		return getUser() + ":" + reportStartTime + ":" + reportEndTime + ":" + getDesctiption() + ":" + sqlExp;
	}

	
	public boolean equals(Object o) {
		if (o instanceof DBNQueryDisabledCondition) {
			DBNQueryDisabledCondition obj = (DBNQueryDisabledCondition) o;
			return getUser().equalsIgnoreCase(obj.getUser())
			&& getSqlExp().equalsIgnoreCase(obj.getSqlExp())
			&& reportStartTime == obj.getReportStartTime()
			&& reportEndTime == obj.getReportEndTime();
		}
		return false;
	}
}
