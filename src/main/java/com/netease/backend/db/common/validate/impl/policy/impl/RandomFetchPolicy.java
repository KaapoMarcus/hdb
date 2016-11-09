package com.netease.backend.db.common.validate.impl.policy.impl;

import java.util.Random;


public class RandomFetchPolicy extends LinearFetchPolicyBase {
	
	public RandomFetchPolicy(String startRowKey, int rowsCount, int rowsLimit,
			int chunkSize) {
		super(startRowKey, rowsCount, rowsLimit, chunkSize);
	}

	
	private int	fetchLimit;

	
	@Override
	protected int getFetchLimit() {
		if (fetchLimit <= 0) {
			fetchLimit = (int) Math
					.min(DEFAULT_FETCH_LIMIT, this.getChunkSize()
							* ((this.getRowsLimit() > 0) ? 2 * (this
									.getRowsCount() / this.getRowsLimit()) : 2));
		}
		return fetchLimit;
	}

	
	final Random	rand	= new Random();

	
	@Override
	protected int getCursorOffset(int fetchLimit) {
		return rand.nextInt(fetchLimit);
	}

	
	@Override
	protected int getCursorLimit(int fetchLimit) {
		return this.getChunkSize();
	}
}
