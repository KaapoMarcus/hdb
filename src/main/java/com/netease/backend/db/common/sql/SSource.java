package com.netease.backend.db.common.sql;


public class SSource extends Statement {
    private static final long serialVersionUID = 1L;
    
    private boolean quiet;
	
	private int batchSize;
	private String file;

	public SSource(String file, boolean quiet, int batchSize) {
		super();
		this.file = file;
		this.quiet = quiet;
		this.batchSize = batchSize;
	}

	public String getFile() {
		return file;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public boolean isQuiet() {
		return quiet;
	}
}
