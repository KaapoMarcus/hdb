package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.management.BackupConfig;

public class SBackup extends Statement {
	private static final long serialVersionUID = 5506295475074523475L;
	private BackupConfig config;

	public SBackup(BackupConfig config) {
		super();
		this.config = config;
	}

	public BackupConfig getConfig() {
		return config;
	}
}
