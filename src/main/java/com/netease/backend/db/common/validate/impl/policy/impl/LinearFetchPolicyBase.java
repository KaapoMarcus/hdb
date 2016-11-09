package com.netease.backend.db.common.validate.impl.policy.impl;

import com.netease.backend.db.common.validate.impl.policy.FetchIter;
import com.netease.backend.db.common.validate.impl.policy.FetchOptions;


public abstract class LinearFetchPolicyBase extends FetchPolicyBase {

	
	public LinearFetchPolicyBase(String startRowKey, long rowsCount,
			long rowsLimit, int chunkSize) {
		super(startRowKey, rowsCount, rowsLimit, chunkSize);
	}

	public FetchIter iterator() {
		return new Iter();
	}

	
	public final class Iter implements FetchIter {
		
		private int		rowsFetched	= 0;
		private String	lastRowKey	= LinearFetchPolicyBase.this
											.getStartRowKey();

		public int getRowsFetched() {
			return rowsFetched;
		}

		public boolean hasNext() {
			return this.getRemainings() > 0;
		}

		public void updateLastRowKey(String lastRowKey) {
			this.lastRowKey = lastRowKey;
		}

		public FetchOptions next() {
			final int fetchLimit = LinearFetchPolicyBase.this.getFetchLimit();
			final int offset = LinearFetchPolicyBase.this
					.getCursorOffset(fetchLimit);
			final int limit = (int) Math.min(LinearFetchPolicyBase.this
					.getCursorLimit(fetchLimit), this.getRemainings());
			try {
				return new FetchOptions(lastRowKey, fetchLimit, offset, limit);
			} finally {
				rowsFetched += limit;
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private long getRemainings() {
			return LinearFetchPolicyBase.this.getRowsLimit() - rowsFetched;
		}
	}

	
	abstract protected int getCursorOffset(int fetchLimit);

	
	abstract protected int getFetchLimit();

	
	abstract protected int getCursorLimit(int fetchLimit);

}
