package com.netease.backend.db.common.validate.impl.policy.impl;


public class OrderedFetchPolicy extends LinearFetchPolicyBase {

	
	public OrderedFetchPolicy(String startRowKey, long rowsCount, long rowsLimit,
			int chunkSize) {
		super(startRowKey, rowsCount, rowsLimit, chunkSize);
	}

	@Override
	protected int getFetchLimit() {
		return DEFAULT_FETCH_LIMIT;
	}

	@Override
	protected int getCursorOffset(int fetchLimit) {
		return 0;
	}

	@Override
	protected int getCursorLimit(int fetchLimit) {
		return this.getChunkSize();
	}

}
