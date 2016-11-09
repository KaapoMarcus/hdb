package com.netease.backend.db.common.ha.util;

import com.netease.backend.db.common.ha.HAException;


public interface ZookeeperWatchMonitor {
	
	public ZookeeperDataSchema startWatch() throws HAException;

	
	public void stopWatch() throws HAException;

	
	public String getZkAddress();

	
	public String getWatchPath();

	
	public boolean isWatching();
}
