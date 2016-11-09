package com.netease.backend.db.common.ha.util;

import org.apache.zookeeper.ZooKeeper;

import com.netease.backend.db.common.ha.HAException;
import com.netease.backend.db.common.ha.MasterInfo;
import com.netease.backend.db.common.ha.util.impl.ZookeeperDataImpl;


public class ZookeeperData {
	
	public static String getNodeData(String zkAddress, String path)
			throws HAException {
		return ZookeeperDataImpl.getNodeData(zkAddress, path);
	}

	
	public static ZookeeperDataSchema getNodeDataWithChildren(String zkAddress,
			String path) throws HAException {
		return ZookeeperDataImpl.getNodeDataWithChildren(zkAddress, path);
	}

	
	public static ZookeeperDataSchema getNodeDataWithChildren(
			ZooKeeper zooKeeper, String path) throws HAException {
		return ZookeeperDataImpl.getNodeDataWithChildren(zooKeeper, path);
	}

	
	public static MasterInfo getMasterInfo(String zkAddress, String ddbName)
			throws HAException {
		return ZookeeperDataImpl.getMasterInfo(zkAddress, ddbName);
	}

}
