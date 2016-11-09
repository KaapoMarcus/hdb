package com.netease.backend.db.common.management.dbi.memcached;

import com.netease.backend.db.common.Disposable;


public interface IMemConnManager extends Disposable {

	
	public MemcachedConnection getMemcachedConnection();

	
	public void updateConfig(MemcachedConfig newConfig);

}