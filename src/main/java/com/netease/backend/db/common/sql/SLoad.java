package com.netease.backend.db.common.sql;

import java.util.List;


public class SLoad extends Statement {
    private static final long serialVersionUID = 1L;
    
    private String file;
	private String table;
	private boolean skipLoad;
	private boolean smart;
	private String tempDir;
	private boolean ignoreError;
	private boolean replaceOnDuplicates;
	private boolean ignoreOnDuplicates;
	private DLOption dlOption;
	private List<String> columns;
		
	public SLoad(DLOption dlOption) {
		this.dlOption = dlOption;
		tempDir = "load.temp";
	}

	public DLOption getDlOption() {
		return dlOption;
	}

	public boolean isIgnoreError() {
		return ignoreError;
	}

	public boolean isSkipLoad() {
		return skipLoad;
	}

	public boolean isSmart() {
		return smart;
	}

	public String getTempDir() {
		return tempDir;
	}

	public String getFile() {
		return file;
	}
	
	public String getTable() {
		return table;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void setSkipLoad(boolean skipLoad) {
		this.skipLoad = skipLoad;
	}

	public void setSmart(boolean smart) {
		this.smart = smart;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public void setIgnoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
	}

	public boolean isReplaceOnDuplicates() {
		return replaceOnDuplicates;
	}

	public void setReplaceOnDuplicates(boolean replaceOnDuplicates) {
		this.replaceOnDuplicates = replaceOnDuplicates;
	}

	public boolean isIgnoreOnDuplicates() {
		return ignoreOnDuplicates;
	}

	public void setIgnoreOnDuplicates(boolean ignoreOnDuplicates) {
		this.ignoreOnDuplicates = ignoreOnDuplicates;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
}
