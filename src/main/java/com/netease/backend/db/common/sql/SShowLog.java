package com.netease.backend.db.common.sql;

import com.netease.backend.db.common.management.model.LogFileReadDescriptor;


public class SShowLog extends Statement {
	private static final long serialVersionUID = -2725276364873014756L;
	
	private LogFileReadDescriptor fileDescriptor;
	
	public SShowLog(LogFileReadDescriptor descriptor) {
		this.fileDescriptor = descriptor;
	}
	
	public LogFileReadDescriptor getFileDescriptor() {
		return fileDescriptor;
	}
}
