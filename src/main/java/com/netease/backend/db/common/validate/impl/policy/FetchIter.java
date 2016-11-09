package com.netease.backend.db.common.validate.impl.policy;

import java.util.Iterator;


public interface FetchIter extends Iterator<FetchOptions> {
	
	int getRowsFetched();

	
	void updateLastRowKey(String rowKey);

}
