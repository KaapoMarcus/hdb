package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class OnlineMigDBInfo implements Serializable {
	private static final long serialVersionUID = 7101654806531483957L;

	
	private String url;

	
	private String databaseDir;

	
	private String binlogFile = "";

	
	private String binlogPos = "";

	public String getDatabaseDir() {
		return databaseDir;
	}

	public void setDatabaseDir(String databaseDir) {
		this.databaseDir = databaseDir;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public OnlineMigDBInfo(String url, String databaseDir) {
		super();
		this.url = url;
		this.databaseDir = databaseDir;
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof OnlineMigDBInfo))
			return false;
		OnlineMigDBInfo o = (OnlineMigDBInfo) other;
		return url.equals(o.url) && databaseDir.equals(o.databaseDir);
	}

	public String getBinlogFile() {
		return binlogFile;
	}

	public void setBinlogFile(String binlogFile) {
		this.binlogFile = binlogFile;
	}

	public String getBinlogPos() {
		return binlogPos;
	}

	public void setBinlogPos(String binlogPos) {
		this.binlogPos = binlogPos;
	}

}
