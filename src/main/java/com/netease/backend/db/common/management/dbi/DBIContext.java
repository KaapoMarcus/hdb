
package com.netease.backend.db.common.management.dbi;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import com.netease.backend.db.common.Disposable;
import com.netease.backend.db.common.exceptions.SQLExceptionWithCause;
import com.netease.backend.db.common.management.Cluster;
import com.netease.backend.db.common.management.DDBURL;
import com.netease.backend.db.common.management.dbi.memcached.IMemConnManager;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.utils.SynUtils;


public class DBIContext implements Serializable, Cloneable, Disposable {

	
	private static final long serialVersionUID = 8154556165225782135L;

	
	private final DBIConfig config;

	
	volatile private DDBURL masterURL = null;
	
	transient volatile private Cluster cluster = null;

	
	
	private User user = null;
	
	private String sid = null;

	
	
	volatile private boolean initd = false;
	
	volatile private boolean identified = false;
	
	volatile private boolean connected = false;
	
	volatile private long connectedTime = 0;
	
	volatile private long lastAccessedTime = 0;
	
	volatile private boolean active = false;

	
	

	
	private Map<String, User> clientUsers = null;

	
	transient private IDBIUserManager clientUserManager = null;

	
	transient private IDBITablesPIDManager tablesPIDManager = null;
	
	
	transient private IMemConnManager memConnManager = null;

	
	public DBIContext(DBIConfig config, DDBURL masterURL) {
		this.config = config;
		this.setMasterURL(masterURL);
	}

	
	public IDBITablesPIDManager getTablesPIDManager() {
		return tablesPIDManager;
	}

	
	public void setTablesPIDManager(IDBITablesPIDManager tablesPIDManager) {
		this.tablesPIDManager = tablesPIDManager;
	}
	
	public IMemConnManager getMemConnManager() {
		return memConnManager;
	}

	public void setMemConnManager(
			IMemConnManager memConnManager) {
		this.memConnManager = memConnManager;
	}

	@Override
	public String toString() {
		return this.getConfig().toString();
	}

	
	public DDBURL getMasterURL() {
		return masterURL;
	}

	
	public DDBURL setMasterURL(DDBURL masterURL) {
		
		
		final DDBURL origURL = this.masterURL;
		
		this.masterURL = masterURL;
		
		return origURL;
	}

	
	public boolean isInitd() {
		return initd;
	}

	
	private static void checkIfInitd(DBIContext context) {
		if (!context.isInitd())
			throw new IllegalStateException("DBI not initialized");
	}

	
	public boolean isIdentified() {
		return identified;
	}

	
	private static void checkIfIdentified(DBIContext context) {
		if (!context.isIdentified())
			throw new IllegalStateException("ASID not assigned/validated");
	}

	
	public void invalidateSession() {
		
		connected = false;
		
		user = null;
		sid = null;
	}

	
	public boolean isConnected() {
		return connected;
	}

	
	private static void checkIfConnected(DBIContext context) {
		if (!context.isConnected())
			throw new IllegalStateException("Not connected to master");
	}

	
	public long getConnectedTime() {
		checkIfConnected(this);
		return connectedTime;
	}

	
	public long getLastAccessedTime() {
		checkIfConnected(this);
		return lastAccessedTime;
	}

	
	public void setLastAccessedTime(long lastAccessedTime) {
		checkIfConnected(this);
		this.lastAccessedTime = lastAccessedTime;
	}

	
	public User getUser() {
		checkIfConnected(this);
		return user;
	}

	
	public String getSid() {
		checkIfConnected(this);
		return sid;
	}

	
	public int getId() {
		
		checkIfIdentified(this);
		return this.getConfig().getId();
	}

	
	public int setId(int id) {
		
		if (!DBIConfig.isIdValid(id))
			throw new IllegalArgumentException("Illegal ID for DBI");

		final DBIConfig config = this.getConfig();
		
		final int oldId = config.getId();
		
		config.setId(id);
		
		identified = true;
		
		return oldId;
	}

	
	public int releaseId() {
		
		identified = false;
		final int oldId = config.getId();
		config.setId(DBIConfig.INVALID_ID);
		return oldId;
	}

	
	public void setSessionInfo(User user, String sid) {
		if (user == null)
			throw new NullPointerException("DBI session user must NOT be null");
		if (sid == null)
			throw new NullPointerException("DBI session ID must NOT be null");
		
		this.user = user;
		
		this.sid = sid;
		
		connectedTime = System.currentTimeMillis();
		
		this.connected = true;
	}

	
	public boolean isActive() {
		return active;
	}

	
	private static void checkIfActive(DBIContext context) {
		if (!context.active)
			throw new IllegalStateException("DBI not active");
	}

	
	synchronized public void activate() {
		
		checkIfInitd(this);
		this.active = true;
	}

	
	synchronized public void deactivate() {
		if (this.isActive()) {
			final Collection<TableInfo> tables = this.getCluster()
					.getTableMap().values();
			
			
			
			SynUtils.getWriteLock(tables);
			try {
				this.active = false;
			} finally {
				
				SynUtils.releaseLock(tables);
			}
		}
	}

	
	public void dispose() {
		
		this.deactivate();
		
		this.disposeServices();
		
		this.disposeCluster();
	}

	
	private void disposeCluster() {
		final Cluster cluster = this.cluster;
		
		this.cluster = null;
		
		if (cluster != null) {
			cluster.dispose();
		}
	}

	
	private void disposeServices() {
		this.disposeClientUserManager();
		this.disposeTablePIDManagers();
		this.disposeMemcachedClientManager();
	}

	private void disposeClientUserManager() {
		final IDBIUserManager manager = this.clientUserManager;
		this.clientUserManager = null;
		if (manager != null) {
			if (manager instanceof Disposable) {
				((Disposable) manager).dispose();
			}
		}
	}

	private void disposeTablePIDManagers() {
		final IDBITablesPIDManager manager = this.tablesPIDManager;
		this.tablesPIDManager = null;
		if (manager != null) {
			if (manager instanceof Disposable) {
				((Disposable) manager).dispose();
			}
		}
	}
	
	private void disposeMemcachedClientManager() {
		final IMemConnManager manager = this.memConnManager;
		this.memConnManager = null;
		if (manager != null) {
			manager.dispose();
		}
	}

	
	public long getNextPID(String tableName) throws SQLException {
		try {
			return this.getTablesPIDManager().getPIDManager(tableName).genId();
		} catch (final PIDException ex) {
			throw new SQLExceptionWithCause("PID generatring error: ", ex);
		}
	}

	
	public Map<String, User> getClientUsers() {
		return this.clientUsers;
	}

	
	public void setClientUsers(Map<String, User> clientUsers) {
		this.clientUsers = clientUsers;
	}

	
	public boolean existClientUser(String userName) {
		return this.getClientUser(userName) != null;
	}

	
	public User getClientUser(String userName) {
		final Map<String, User> clientUsers = this.getClientUsers();
		return (clientUsers != null) ? clientUsers.get(userName) : null;
	}

	
	public DBIConfig getConfig() {
		return config;
	}

	
	public String getName() {
		return this.getConfig().getName();
	}

	
	public DBISignature getSignature() {
		return this.getConfig().getSignature();
	}

	
	public void setCluster(Cluster cluster) {
		if (cluster == null)
			throw new NullPointerException("DDB cluster should not be null");

		
		initd = false;
		if (!initd) {
			synchronized (this) {
				
				if (initd)
					return;
				
				
				final boolean active = this.isActive();
				boolean acted = false;
				try {
					
					this.cluster = cluster;
					
					acted = true;
				} finally {
					if (acted) {
						
						initd = true;
						
						if (active) {
							this.activate();
						}
					}
				}
			}
		}
	}

	
	public Cluster getCluster() {
		checkIfInitd(this);

		return cluster;
	}

	
	public IDBIUserManager getClientUserManager() {
		checkIfActive(this);

		final IDBIUserManager manager = this.clientUserManager;
		if (manager == null)
			throw new IllegalStateException("Client user manager not avialable");

		return clientUserManager;
	}

	
	public void setClientUserManager(IDBIUserManager manager) {
		this.clientUserManager = manager;
	}

	
	public String getHost() {
		return this.getConfig().getHost();
	}

	
	public int getType() {
		return this.getConfig().getType();
	}

	
	public String getTypeStr() {
		return this.getConfig().getTypeStr();
	}

	
	public String getStatusStr() {
		final StringBuilder ss = new StringBuilder(128);
		ss.append("initd:").append(this.isInitd());
		ss.append("identified:").append(this.isIdentified());
		ss.append("connected:").append(this.isConnected());
		ss.append("active:").append(this.isActive());
		return ss.toString();
	}

	
	public String getVersion() {
		return this.getConfig().getVersion();
	}

	
	public String getServiceHost() {
		return this.getConfig().getServiceHost();
	}

	
	public int getServicePort() {
		return this.getConfig().getServicePort();
	}

}
