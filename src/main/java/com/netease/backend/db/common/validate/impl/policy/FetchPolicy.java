package com.netease.backend.db.common.validate.impl.policy;


public interface FetchPolicy extends Iterable<FetchOptions> {

	
	FetchIter iterator();

	long getRowsCount();

	
	long getRowsLimit();

	
	int getChunkSize();
}
