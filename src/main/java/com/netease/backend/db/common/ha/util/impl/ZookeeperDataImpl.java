package com.netease.backend.db.common.ha.util.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import com.netease.backend.db.common.ha.HAException;
import com.netease.backend.db.common.ha.MasterInfo;
import com.netease.backend.db.common.ha.ZooKeeperDefinition;
import com.netease.backend.db.common.ha.ZooKeeperLeaderChangeHandler;
import com.netease.backend.db.common.ha.ZooKeeperLeaderMonitor;
import com.netease.backend.db.common.ha.ZooKeeperProvider;
import com.netease.backend.db.common.ha.ZooKeeperStatusListener;
import com.netease.backend.db.common.ha.util.ZookeeperDataSchema;


public class ZookeeperDataImpl {
	public static final int WAIT_TIME_OUT = 10000;

	private interface ZookeeperDataStatusListener extends
			ZooKeeperStatusListener {
		public Exception getException();
	}

	private static class ZKDataListener implements ZookeeperDataStatusListener {
		private Object barrier;

		private String path;

		private String result;

		private Exception exception = null;

		ZKDataListener(Object barrier, String path) {
			this.barrier = barrier;
			this.path = path;
		}

		public void zkAvailable(ZooKeeperProvider zkProvider) {
			try {
				byte[] serverListBytes = zkProvider.getZooKeeper().getData(
						path, false, null);
				result = new String(serverListBytes);
				synchronized (barrier) {
					barrier.notifyAll();
				}
			} catch (Exception e) {
				exception = e;
			}
		}

		public void zkUnavailable(ZooKeeperProvider zkProvider) {

		}

		public String getResult() {
			return result;
		}

		public Exception getException() {
			return exception;
		}

	}

	
	public static String getNodeData(String zkAddress, String path)
			throws HAException {
		Object barrier = new Object();
		ZKDataListener listener = new ZKDataListener(barrier, path);

		getData(zkAddress, listener, barrier);

		return listener.getResult();
	}

	private static class ZKLeaderChangeHandler implements
			ZooKeeperLeaderChangeHandler {
		private Object barrier;

		private String result;

		private Exception exception = null;

		ZKLeaderChangeHandler(Object barrier) {
			this.barrier = barrier;
		}

		public void leaderChanged(String newPath, byte[] newData) {
			result = new String(newData);
			synchronized (barrier) {
				barrier.notifyAll();
			}
		}

		public String getResult() {
			return result;
		}

		public Exception getException() {
			return exception;
		}

		public void gotException(String cause) {
			exception = new HAException(cause);
		}
	}

	private static class ZKMasterInfoListener implements
			ZookeeperDataStatusListener {
		private String path;

		private ZKLeaderChangeHandler handler;

		private Exception exception = null;

		ZKMasterInfoListener(Object barrier, String path) {
			this.path = path;
			handler = new ZKLeaderChangeHandler(barrier);
		}

		public void zkAvailable(ZooKeeperProvider zkProvider) {
			try {
				ZooKeeperLeaderMonitor zkLeaderMonitor = new ZooKeeperLeaderMonitor(
						zkProvider, path, handler);
				zkLeaderMonitor.startWatching();
			} catch (Exception e) {
				exception = e;
			}
		}

		public void zkUnavailable(ZooKeeperProvider zkProvider) {

		}

		public String getResult() {
			return handler.getResult();
		}

		public Exception getException() {
			if (exception != null)
				return exception;
			else
				return handler.getException();
		}

	}

	
	public static MasterInfo getMasterInfo(String zkAddress, String ddbName)
			throws HAException {
		Object barrier = new Object();
		String parentPath = ZooKeeperDefinition.getMasterPath(ddbName);

		ZKMasterInfoListener listener = new ZKMasterInfoListener(barrier,
				parentPath);

		getData(zkAddress, listener, barrier);

		return ZooKeeperDefinition.getMasterInfo(listener.getResult());
	}

	private static void getData(String zkAddress,
			ZookeeperDataStatusListener listener, Object barrier)
			throws HAException {
		ZooKeeperProvider zkProvider = new ZooKeeperProvider(zkAddress);

		try {
			zkProvider.connect(0, null, listener);
		} catch (IOException e) {
			throw new HAException(
					"get zookeeper data failed, can not connect to server", e);
		}
		try {
			synchronized (barrier) {
				try {
					barrier.wait(WAIT_TIME_OUT);
				} catch (Exception e) {
					throw new HAException(
							"get zookeeper master info failed, wait time out",
							listener.getException() == null ? e : listener
									.getException());
				}
			}

			if (listener.getException() != null) {
				throw new HAException("get zookeeper master info failed",
						listener.getException());
			}
		} finally {
			try {
				zkProvider.close();
			} catch (InterruptedException e) {
				throw new HAException(
						"get zookeeper master info failed, close error", e);
			}
		}
	}

	
	public static ZookeeperDataSchema getNodeDataWithChildren(String zkAddress,
			String path) throws HAException {
		Object barrier = new Object();
		ZKDataWithChildrenListener listener = new ZKDataWithChildrenListener(
				barrier, path);

		getData(zkAddress, listener, barrier);

		return listener.getResult();

	}

	
	public static ZookeeperDataSchema getNodeDataWithChildren(
			ZooKeeper zooKeeper, String path) throws HAException {
		try {
			List<String> childrenPath = null;
			childrenPath = zooKeeper.getChildren(path, null);

			byte[] dataBytes = zooKeeper.getData(path, false, null);
			String value = new String(dataBytes);

			List<ZookeeperDataSchema> children = new ArrayList<ZookeeperDataSchema>();
			for (String c : childrenPath) {
				String childPath = path + "/" + c;
				dataBytes = zooKeeper.getData(childPath, false, null);
				children.add(new ZookeeperDataSchema(childPath, new String(
						dataBytes), null));
			}

			return new ZookeeperDataSchema(path, value, children);
		} catch (KeeperException ke) {
			if (ke.code() == KeeperException.Code.NONODE) {
				return null;
			}
			throw new HAException("get zookeeper data failed");
		} catch (InterruptedException ke) {
			throw new HAException("get zookeeper interrupted");
		}

	}

	private static class ZKDataWithChildrenListener implements
			ZookeeperDataStatusListener {
		private Object barrier;

		private String path;

		private ZookeeperDataSchema result;

		private Exception exception = null;

		ZKDataWithChildrenListener(Object barrier, String path) {
			this.barrier = barrier;
			this.path = path;
		}

		public void zkAvailable(ZooKeeperProvider zkProvider) {
			try {
				result = getNodeDataWithChildren(zkProvider.getZooKeeper(),
						path);

				synchronized (barrier) {
					barrier.notifyAll();
				}
			} catch (Exception e) {
				exception = e;
			}
		}

		public void zkUnavailable(ZooKeeperProvider zkProvider) {

		}

		public ZookeeperDataSchema getResult() {
			return result;
		}

		public Exception getException() {
			return exception;
		}

	}

	public static void main(String args[]) {
		try {
			MasterInfo m = ZookeeperDataImpl.getMasterInfo(
					"172.31.130.38:2181", "ddb_demo");
			System.out.println(m.getIp());
			System.out.println(m.getDbiport());
			System.out.println(m.getDbaport());
		} catch (HAException e) {
			
			e.printStackTrace();
		}
	}

}
