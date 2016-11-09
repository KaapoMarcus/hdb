
package com.netease.backend.db.common.ha;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;


public class ZooKeeperLeaderMonitor extends ZooKeeperNodeMonitor {
	private static final Logger LOG;

	
	private static enum CONTEXT {
		CTX_CHILDREN_UP
	};

	static {
		LOG = Logger.getLogger(ZooKeeperLeaderMonitor.class);
	}

	private ZooKeeperLeaderChangeHandler _leaderChangeHandler;
	private String _prevLeaderPath;
	private byte[] _prevLeaderData;

	private DataCallback _dataCallback = new DataCallbackImpl();
	private ChildrenCallback _childrenCallback = new ChildrenCallbackImpl();

	
	public ZooKeeperLeaderMonitor(ZooKeeperProvider zkProvider,
			String parentPath, ZooKeeperLeaderChangeHandler changeHandler) {
		super(zkProvider, parentPath);

		assert changeHandler != null;

		this._leaderChangeHandler = changeHandler;
	}

	
	@Override
	public void startWatching() {
		setWatching(true);

		
		getZooKeeper().getChildren(getParentPath(), false, _childrenCallback,
				null);
	}

	
	@Override
	public void stopWatching() {
		setWatching(false);
	}

	
	public void process(WatchedEvent event) {
		assert event != null;

		LOG.trace("ZooKeeper event received:");
		LOG.trace("event state: " + event.getState());
		LOG.trace("event type: " + event.getType());
		LOG.trace("event path: " + event.getPath());

		if (!isWatching()) {
			LOG.debug("this monitor had been marked as stoped, "
					+ "no further event processing.");
			return;
		}

		if (event.getType() == Event.EventType.None) {
			
			switch (event.getState()) {
			case SyncConnected:
				
				break;
			case Disconnected:
			case Expired:
			default:
				
				getZKProvider().addToNotificationPool(this);
				break;
			}
			return;
		}

		String eventPath = event.getPath();
		switch (event.getType()) {
		case NodeChildrenChanged:
			if (eventPath.equals(getParentPath())) {
				
				getZooKeeper().getChildren(getParentPath(), false,
						_childrenCallback, null);
			}
			break;
		case NodeDeleted:
			if (eventPath.equals(getParentPath())) {
				
				LOG.error("Parent node '" + getParentPath()
						+ "' had been deleted! ZooKeeper HA service exited.");
			} else {
				
				LOG.info("ѡ���µ�zookeeper-leader...");
				getZooKeeper().getChildren(getParentPath(), false,
						_childrenCallback, null);

			}
			break;
		case NodeDataChanged:
		default:
			if (eventPath.equals(_prevLeaderPath)) {
				
				LOG.info("Leader's data changed.");
				getZooKeeper().getData(eventPath, this, _dataCallback, null);
			}
			break;
		}
	}

	private class DataCallbackImpl implements DataCallback {

		private short _retryCount = 0;

		
		public void processResult(int rc, String path, Object ctx, byte[] data,
				Stat stat) {
			Code code = Code.get(rc);
			CONTEXT context = ctx == null ? null : (CONTEXT) ctx;

			LOG.trace("ZK data callback, Code:[" + code + "], path:'" + path
					+ "', ctx:'" + context + "'");

			
			if (!isWatching()) {
				LOG.debug("this monitor had been marked as stoped, "
						+ "no further data processing.");
				return;
			}

			switch (code) {
			case OK:
				this._retryCount = 0;
				if ((data == null && data != _prevLeaderData)
						|| (data != null && !Arrays.equals(_prevLeaderData,
								data))) {
					_prevLeaderData = data;
				}
				
				if (_leaderChangeHandler != null) {
					_leaderChangeHandler.leaderChanged(path, data);
				}
				break;
			case NONODE:
				LOG.warn("Node:'" + path + "' unavailable!");
				
				break;
			case SESSIONEXPIRED:
			case NOAUTH:
				
				stopWatching();
				getZKProvider().addToNotificationPool(
						ZooKeeperLeaderMonitor.this);
				break;
			default:
				
				if (this._retryCount >= 3) {
					hibernate();
				} else {
					LOG
							.warn("Error: [" + code.toString()
									+ "], retry again...");
					getZooKeeper().getData(path, ZooKeeperLeaderMonitor.this,
							_dataCallback, null);
				}
				break;
			}
		}
	}

	
	private class ChildrenCallbackImpl implements ChildrenCallback {

		
		public void processResult(int rc, String path, Object ctx,
				List<String> children) {
			Code code = Code.get(rc);
			CONTEXT context = (ctx == null ? null : (CONTEXT) ctx);

			LOG.trace("ZK children callback, rc:[" + rc + "], path:'" + path
					+ "', ctx:'" + context + "'");

			
			if (!isWatching()) {
				LOG.debug("this monitor had been marked as stoped, "
						+ "no further data processing.");
				return;
			}

			switch (code) {
			case OK:
				if (children.size() > 0) {
					_prevLeaderPath = path + "/"
							+ ZooKeeperDefinition.electLeader(children);
					LOG.debug("Leader acknowledged, path: '" + _prevLeaderPath
							+ "'");
					
					getZooKeeper().getData(_prevLeaderPath,
							ZooKeeperLeaderMonitor.this, _dataCallback, null);
				} else {
					
					LOG.warn("Current path:'" + getNodePath()
							+ "' has no children to follow!");
					LOG.warn("Watching on parent node:'" + getNodePath()
							+ "', awaiting children to come up...");
					getZooKeeper().getChildren(path,
							ZooKeeperLeaderMonitor.this,
							(ChildrenCallback) null, null);
				}
				break;
			case NONODE:
				
				String cause = "Unrecoverable error: Parent node '" + path
						+ "' not exists!";
				LOG.error(cause);
				if (_leaderChangeHandler != null) {
					_leaderChangeHandler.gotException(cause);
				}
				break;
			case SESSIONEXPIRED:
			case NOAUTH:
				
				LOG
						.warn("ZooKeeper unavailable or un-connectable, current "
								+ "connection will hibernate until service is available.");
				getZKProvider().addToNotificationPool(
						ZooKeeperLeaderMonitor.this);
				break;
			default:
				
				LOG
						.warn("Error while inspecting parent node, will retry again.");
				getZooKeeper().getChildren(getParentPath(), false,
						_childrenCallback, null);
				break;
			}
		}
	}

	public String getParentPath() {
		return this.getNodePath();
	}

	
	public byte[] getData() {
		return this._prevLeaderData;
	}

	public ZooKeeper getZooKeeper() {
		return this.getZKProvider().getZooKeeper();
	}
}
