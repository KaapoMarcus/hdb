package com.netease.backend.db.common.validate.impl.policy.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import com.netease.backend.db.common.validate.impl.policy.FetchIter;
import com.netease.backend.db.common.validate.impl.policy.FetchOptions;


public class EvenFetchPolicy extends FetchPolicyBase {

	
	public EvenFetchPolicy(long startRowKey, long rowsCount, long rowsLimit,
			int chunkSize) {
		super(String.valueOf(startRowKey), rowsCount, rowsLimit, chunkSize);

		this.init();
	}

	private List<String>	startRowKeys;

	private Iterator<String> iteratorOfStartRowKeys() {
		return startRowKeys.iterator();
	}

	private void init() {
		
		final int count = (int) (this.getRowsLimit() / this.getChunkSize()) + 1;
		
		final long adv = this.getRowsCount() / (count);
		
		fetchLimit = (int) Math.min(DEFAULT_FETCH_LIMIT, adv);
		
		startRowKeys = new ArrayList<String>(count);

		
		final Random rand = new Random();

		long key = Long.valueOf(this.getStartRowKey());
		for (int i = 0; i < count; i++) {
			startRowKeys.add(String.valueOf(key));
			key += adv;
			key += rand.nextInt((int) adv );
		}
	}

	public FetchIter iterator() {
		return new NumIter();
	}

	private int	fetchLimit	= 0;

	protected int getFetchLimit() {
		return fetchLimit;
	}

	public final class NumIter implements FetchIter {
		
		private int					rowsFetched	= 0;
		private long				lastRowKey;

		private Iterator<String>	rowKeyIter;

		public NumIter() {
			rowKeyIter = EvenFetchPolicy.this.iteratorOfStartRowKeys();
		}

		public int getRowsFetched() {
			return rowsFetched;
		}

		
		public boolean hasNext() {
			return rowKeyIter.hasNext();
		}

		public synchronized void updateLastRowKey(String lastRowKey) {
			Long v = Long.valueOf(lastRowKey);
			if (v > this.lastRowKey) {
				this.lastRowKey = v;
			}
		}

		public synchronized FetchOptions next() {
			String startRowKey;
			try {
				startRowKey = rowKeyIter.next();
			} catch (NoSuchElementException ex) {
				return null;
			}

			final long lastRowKey = this.lastRowKey;
			if (Long.valueOf(startRowKey) < lastRowKey) {
				startRowKey = String.valueOf(lastRowKey);
			}

			final int limit = (int) Math.min(EvenFetchPolicy.this
					.getChunkSize(), this.getRemainings());
			try {
				return new FetchOptions(startRowKey, fetchLimit, 0, limit);
			} finally {
				rowsFetched += limit;
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private long getRemainings() {
			return EvenFetchPolicy.this.getRowsLimit() - rowsFetched;
		}
	}

}
