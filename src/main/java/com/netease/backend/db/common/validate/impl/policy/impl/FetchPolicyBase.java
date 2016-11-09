package com.netease.backend.db.common.validate.impl.policy.impl;

import com.netease.backend.db.common.validate.impl.policy.FetchPolicy;


public abstract class FetchPolicyBase implements FetchPolicy {

	
	protected static final int	DEFAULT_FETCH_LIMIT	= 10000;
	
	protected static final int	DEFAULT_CHUNK_SIZE	= 10;

	
	private final long			rowsCount;
	
	private final long			rowsLimit;
	
	private final int			chunkSize;
	
	private final String		startRowKey;

	
	public FetchPolicyBase(String startRowKey, long rowsCount, long rowsLimit,
			int chunkSize) {
		if (startRowKey == null)
			throw new NullPointerException();
		if (rowsCount <= 0)
			throw new IllegalArgumentException("Rows count should > 0");
		if ((rowsLimit <= 0) || (rowsLimit > rowsCount)) {
			rowsLimit = rowsCount;
		}
		this.startRowKey = startRowKey;
		chunkSize = Math.max((int) (Math.min(chunkSize, rowsLimit)),
				DEFAULT_CHUNK_SIZE);
		this.rowsLimit = rowsLimit;
		this.rowsCount = rowsCount;
		this.chunkSize = chunkSize;
	}

	
	public String getStartRowKey() {
		return startRowKey;
	}

	
	public long getRowsCount() {
		return rowsCount;
	}

	
	public long getRowsLimit() {
		return rowsLimit;
	}

	
	public int getChunkSize() {
		return chunkSize;
	}
}
