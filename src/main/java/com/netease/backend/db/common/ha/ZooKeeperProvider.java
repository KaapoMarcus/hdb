package com.netease.backend.db.common.ha;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;


public class ZooKeeperProvider implements Watcher, ZooKeeperServerListListener {

	
	public static final int RETRY_TIMEOUT = 600000;

	private static final Logger LOG;
	static {
		LOG = Logger.getLogger(ZooKeeperProvider.class);
	}

	private int _retryCount = 0;
	private String _connectingStr;
	private int _sessionTimeout = 3000; 
	private volatile ZooKeeper _zk;
	
	private long _sessionId = 0;
	private byte[] _sessionPassword;
	
	private Object _connectMutex = new Object();
	private boolean _connected = false;
	private Watcher _chainedWatcher;
	private final String _serverListPath = "/serverlist";
	private volatile ZooKeeperServerListMonitor _serverListMonitor;
	private ZooKeeperStatusListener _statusListener;
	
	private List<ZooKeeperNodeMonitor> _nodeMonitors = Collections
			.synchronizedList(new LinkedList<ZooKeeperNodeMonitor>());

	
	public ZooKeeperProvider(String connectString) {
		assert connectString != null;

		this._connectingStr = connectString.trim();
	}

	
	public void connect(int sessionTimeout, Watcher chainedWatcher,
			ZooKeeperStatusListener zkStatusListener) throws IOException {
		this._sessionTimeout = (sessionTimeout == 0 ? this._sessionTimeout
				: sessionTimeout);
		this._chainedWatcher = chainedWatcher;
		this._statusListener = zkStatusListener;

		LOG.info("����zookeeper������...");
		this._zk = new ZooKeeper(this._connectingStr, this._sessionTimeout,
				this);
	}

	
	public void syncConnect(int sessionTimeout, Watcher chainedWatcher,
			ZooKeeperStatusListener zkStatusListener) throws IOException,
			InterruptedException {
		this.connect(sessionTimeout, chainedWatcher, zkStatusListener);

		
		this.waitOnConnectionStatus();
	}

	
	public void reconnect() throws IOException {
		LOG.info("����zookeeper������...");

		this._zk = new ZooKeeper(this._connectingStr, this._sessionTimeout,
				this, this._sessionId, this._sessionPassword);
	}

	
	public void freshReconnect() throws IOException {
		LOG.info("����zookeeper������������������...");

		this._zk = new ZooKeeper(this._connectingStr, this._sessionTimeout,
				this);
	}

	
	public void syncReconnect() throws IOException, InterruptedException {
		this.reconnect();

		
		synchronized (this._zk) {
			this._connectMutex.wait();
		}
	}

	private void zkUnavailable() {
		if (_statusListener != null)
			_statusListener.zkUnavailable(this);
	}

	
	public void process(WatchedEvent event) {
		assert event != null;

		LOG.trace("recieved event: ");
		LOG.trace("event state: " + event.getState());
		LOG.trace("event type: " + event.getType());
		LOG.trace("event path: " + event.getPath());

		if (event.getType() == Event.EventType.None) {
			
			
			switch (event.getState()) {
			case SyncConnected:
				
				
				
				this._sessionId = this._zk.getSessionId();
				this._sessionPassword = this._zk.getSessionPasswd();
				
				synchronized (this._connectMutex) {
					LOG.info("����zookeeper�������ɹ�.");
					this._connected = true;
					this._retryCount = 0;
					this._connectMutex.notifyAll();
					if (this._statusListener != null) {
						this._statusListener.zkAvailable(this);
					}
					
					for (ZooKeeperNodeMonitor nodeMonitor : this._nodeMonitors) {
						nodeMonitor.startWatching();
					}
					_nodeMonitors.clear();
				}
				
				if (_serverListMonitor == null) {
					_serverListMonitor = new ZooKeeperServerListMonitor(this,
							_serverListPath, this);
				}
				_serverListMonitor.startWatching();
				break;
			case Disconnected:
				this._connected = false;
				
				LOG.warn("��zookeeper���������ӶϿ�!");
				
				Timer timer = new Timer(true);
				timer.schedule(new TimerTask() {
					public void run() {
						if (!_connected)
							zkUnavailable();
					}
				}, RETRY_TIMEOUT);
				break;
			case Expired:
			default:
				this._connected = false;
				
				LOG.warn("ZooKeeper session expired!");
				while (true) {
					_retryCount += 1;
					try {
						if (_retryCount == 3 && this._statusListener != null) {
							this._statusListener.zkUnavailable(this);
						}

						freshReconnect();
						break;
					} catch (IOException e) {
						
						LOG.warn("Encounter network problem while refreshing"
								+ " ZooKeeper connection, will try again.");
						continue;
					}
				}
				
				break;
			}
		}

		if (this._chainedWatcher != null) {
			this._chainedWatcher.process(event);
		}
	}

	

	
	public void addToNotificationPool(ZooKeeperNodeMonitor nodeMonitor) {
		this._nodeMonitors.add(nodeMonitor);
	}

	
	public void serverListChanged(String connectingStr) {
		LOG.debug("ZooKeeper server list changed to: " + connectingStr);
		_connectingStr = connectingStr;
	}

	
	private void waitOnConnectionStatus() throws InterruptedException {
		synchronized (this._connectMutex) {
			LOG.debug("Waiting ZooKeeper connection to become ready...");
			this._connectMutex.wait();
		}
	}

	public void close() throws InterruptedException {
		this._zk.close();
	}

	
	
	public boolean isConnected() {
		return this._connected;
	}

	
	public ZooKeeper getZooKeeper() {
		return this._zk;
	}

	
	public String getConnectingStr() {
		return this._connectingStr;
	}

	
	public void setConnectingStr(String connectingStr) {
		this._connectingStr = connectingStr;
	}
}
