package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class OnlineAlterTaskDb implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;

	private int status;
	
	
	private String lastRowKeyValue = null;
	
	
	private long validatedRows = 0;
	
	
	private int validateStatus = 0;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLastRowKeyValue() {
		return lastRowKeyValue;
	}

	public void setLastRowKeyValue(String lastRowKeyValue) {
		this.lastRowKeyValue = lastRowKeyValue;
	}
	
	public long getValidatedRows() {
		return validatedRows;
	}

	public void setValidatedRows(long validatedRows) {
		this.validatedRows = validatedRows;
	}

	public int getValidateStatus() {
		return validateStatus;
	}

	public void setValidateStatus(int validateStatus) {
		this.validateStatus = validateStatus;
	}

	public OnlineAlterTaskDb(String url, int status) {
		super();
		this.url = url;
		this.status = status;
	}

	public String getDesc(Integer processStatus) {
		return "���ݿ�url:" + url + "             ��ǰ״̬:" + OnlineAlterTaskInfo.getStatusDesc(status)
				+ (processStatus == null ? "" : "            ��ǰ����:" + processStatus);
	}
}
