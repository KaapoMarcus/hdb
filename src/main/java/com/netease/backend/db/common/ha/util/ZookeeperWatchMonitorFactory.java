package com.netease.backend.db.common.ha.util;

import org.apache.zookeeper.WatchedEvent;

import com.netease.backend.db.common.ha.HAException;
import com.netease.backend.db.common.ha.util.impl.ZookeeperWatchMonitorImpl;


public class ZookeeperWatchMonitorFactory {

	
	public static ZookeeperWatchMonitor generateMonitor(String zkAddress,
			ZookeeperWatcher watcher, String watchPath) {
		return new ZookeeperWatchMonitorImpl(zkAddress, watcher, watchPath);
	}

	
	public static void main(String args[]) {
		ZookeeperWatchMonitor monitor = ZookeeperWatchMonitorFactory
				.generateMonitor("172.31.130.38:2181", new ZookeeperWatcher() {

					public void process(WatchedEvent event, ZookeeperDataSchema info) {
						switch (event.getType()) {
						case NodeChildrenChanged:
							System.out.println("NodeChildrenChanged:");
							break;
						case NodeDeleted:
							System.out.println("NodeDeleted:");
							break;
						case NodeDataChanged:
							System.out.println("NodeDataChanged:");
							break;
						default:
							System.out.println("default:");

						}
						System.out.println(info.toString());
					}
				}, "/test");

		try {
			ZookeeperDataSchema info = monitor.startWatch();
			System.out.println(info.toString());
		} catch (HAException e1) {
			
			e1.printStackTrace();
		}

		try {
			Thread.sleep(500000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

}
