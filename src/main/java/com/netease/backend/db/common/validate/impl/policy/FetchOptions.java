package com.netease.backend.db.common.validate.impl.policy;


public class FetchOptions {
	private final String	startRowKey;
	private final int		fetchLimit;
	private final int		cursorOffset;
	private final int		rowslimit;

	
	public FetchOptions(String startRowKey, int fetchLimit, int cursorOffset,
			int rowslimit) {
		this.startRowKey = startRowKey;
		this.fetchLimit = fetchLimit;
		this.cursorOffset = cursorOffset;
		this.rowslimit = rowslimit;
	}

	
	public int getFetchLimit() {
		return fetchLimit;
	}

	
	public int getCursorOffset() {
		return cursorOffset;
	}

	
	public int getRowslimit() {
		return rowslimit;
	}

	
	public String getStartRowKey() {
		return startRowKey;
	}

}
