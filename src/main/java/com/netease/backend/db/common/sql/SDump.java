package com.netease.backend.db.common.sql;

import java.util.List;


public class SDump extends Statement {
    private static final long serialVersionUID = 1L;
    private String dir;
	private List<String> tables;
	private String file;
	private String where;
	private String orderBy;
	private long limit;
	private boolean parallel;
	private String select;
	private DLOption dlOption;

	public SDump(DLOption dlOption) {
		this.dlOption = dlOption;
		limit = Long.MAX_VALUE;
	}

	public String getDir() {
		return dir;
	}

	public String getFile() {
		return file;
	}

	public List<String> getTables() {
		return tables;
	}

	public String getWhere() {
		return where;
	}

	public String getOrderBy() {
		return orderBy;
	}
	
	public boolean isParallel() {
		return parallel;
	}

	public long getLimit() {
		return limit;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	public DLOption getDlOption() {
		return dlOption;
	}
}
