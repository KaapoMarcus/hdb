package com.netease.backend.db.common.ha.util;

import org.apache.zookeeper.WatchedEvent;


public interface ZookeeperWatcher {
	
	public void process(WatchedEvent event, ZookeeperDataSchema info);

}
