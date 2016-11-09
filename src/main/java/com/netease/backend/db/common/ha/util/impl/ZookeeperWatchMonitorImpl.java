package com.netease.backend.db.common.ha.util.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.netease.backend.db.common.ha.HAException;
import com.netease.backend.db.common.ha.ZooKeeperProvider;
import com.netease.backend.db.common.ha.util.ZookeeperDataSchema;
import com.netease.backend.db.common.ha.util.ZookeeperWatchMonitor;
import com.netease.backend.db.common.ha.util.ZookeeperWatcher;


public class ZookeeperWatchMonitorImpl implements ZookeeperWatchMonitor {
	Logger log = Logger.getLogger(ZookeeperWatchMonitorImpl.class);

	
	private ZooKeeperProvider zkProvider;

	
	private String zkAddress;

	
	private ZookeeperWatcher watcher;

	
	private String watchPath;

	private boolean watching = false;

	public ZookeeperWatchMonitorImpl(String zkAddress,
			ZookeeperWatcher watcher, String watchPath) {
		super();
		this.zkAddress = zkAddress;
		this.watcher = watcher;
		this.watchPath = watchPath;
	}

	public ZookeeperDataSchema startWatch() throws HAException {
		if (watching)
			throw new HAException("zookeeper watcher is already running");

		zkProvider = new ZooKeeperProvider(zkAddress);

		try {
			zkProvider.syncConnect(0, null, null);
		} catch (Exception e) {
			throw new HAException(
					"start zookeeper watch failed, can not connect to server",
					e);
		}

		try {
			List<String> childrenPath = zkProvider.getZooKeeper().getChildren(
					watchPath, new ZKWatcher());

			watching = true;

			byte[] dataBytes = zkProvider.getZooKeeper().getData(watchPath,
					false, null);

			return new ZookeeperDataSchema(watchPath, new String(dataBytes),
					getChildren(childrenPath));
		} catch (Exception e) {
			throw new HAException(
					"start zookeeper watch failed, can not attach listener", e);
		}

	}

	public void stopWatch() throws HAException {
		if (!watching)
			return;

		try {
			zkProvider.close();
		} catch (InterruptedException e) {
			throw new HAException("close zookeeper failed", e);
		}

		watching = false;
	}

	class ZKWatcher implements Watcher {

		public void process(WatchedEvent event) {
			List<String> childrenPath = null;
			String value = "";
			try {
				try {
					childrenPath = zkProvider.getZooKeeper().getChildren(
							watchPath, this);
					byte[] dataBytes = zkProvider.getZooKeeper().getData(watchPath,
							false, null);
					value = new String(dataBytes);
				} catch (KeeperException ke) {
					if (ke.code() == KeeperException.Code.NONODE) {
						childrenPath = new ArrayList<String>();
					}
				}
				if(childrenPath == null) {
					childrenPath = new ArrayList<String>();
				}
				watcher.process(event, new ZookeeperDataSchema(
						watchPath, value, getChildren(childrenPath)));
			} catch (Exception e) {
				log.error("can not get zookeeper data or children list", e);
			}

		}
	}

	private List<ZookeeperDataSchema> getChildren(List<String> paths)
			throws HAException {
		List<ZookeeperDataSchema> children = new ArrayList<ZookeeperDataSchema>();
		for (String path : paths) {
			String childPath = watchPath + "/" + path;
			byte[] dataBytes;
			try {
				dataBytes = zkProvider.getZooKeeper().getData(childPath, false,
						null);
				children.add(new ZookeeperDataSchema(childPath, new String(
						dataBytes), null));
			} catch (Exception e) {
				throw new HAException("get zookeeper data failed", e);
			}
		}

		return children;
	}

	public String getZkAddress() {
		return zkAddress;
	}

	public String getWatchPath() {
		return watchPath;
	}

	public boolean isWatching() {
		return watching;
	}

}
