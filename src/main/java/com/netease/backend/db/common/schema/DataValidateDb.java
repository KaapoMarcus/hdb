package com.netease.backend.db.common.schema;

import java.io.Serializable;

public class DataValidateDb implements Serializable {
	
	private static final long serialVersionUID = 7732482036071336400L;

	private String url;

	
	private String lastRowKeyValue = null;
	
	
	private long validatedRows = 0;
	
	
	private int status = 0;
	
	public DataValidateDb(String url, int status) {
		this.url = url;
		this.status = status;
	}
	
	public DataValidateDb(String url, int status, String value, long rows) {
		this.url = url;
		this.status = status;
		this.lastRowKeyValue= value;
		this.validatedRows = rows;
	}

	
	public void updateProgress(String value, int rows) {
		this.lastRowKeyValue = value;
		this.validatedRows += rows;
	}
	
	public String getUrl() {
		return url;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
