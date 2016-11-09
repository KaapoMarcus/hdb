package com.netease.backend.db.common.schema;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.definition.Definition;
import com.netease.backend.db.common.definition.LoadBalanceType;
import com.netease.backend.db.common.exceptions.DBInfoException;
import com.netease.backend.db.common.exceptions.LoadBalanceException;
import com.netease.backend.db.common.utils.Validator;
import com.netease.lb.DBNLoadBalancer;
import com.netease.lb.LoadBalancer;
import com.netease.lb.LoadBalancerIterator;


public abstract class Database implements Serializable, Cloneable,
		Comparable<Database> {
	private static final long serialVersionUID = 8139654102880727548L;

	
	public static final byte STATUS_DEAD = 0;

	
	public static final byte STATUS_NORMAL = 1;

	
	public static final byte REP_STATUS_DEAD = 0;

	
	public static final byte REP_STATUS_NORMAL = 1;

	
	public static final byte REP_TIMEOUT_DEAD = 0;

	
	public static final byte REP_TIMEOUT_NORMAL = 1;

	public static final int MAX_WEIGHT = 20;

	
	public static final byte STATUS_DISABLE = 2;

	
	public static final byte ERROR_NONE = 0;

	
	public static final byte ERROR_TIME_OUT = 1;

	
	public static final byte ERROR_OTHER = 2;

	
	public static final byte STATUS_REP_FAIL = -1;

	
	public static final int DIRTY_NORMAL = 0;

	
	public static final int DIRTY_MODIFY = 1;

	
	public static final int DIRTY_ALL = 2;

	
	private String name;

	
	private String ipHost;

	
	private int port;

	
	private String url;

	
	private String managementUserName;

	
	private String domainSchemaName;

	
	private String defaultTablespace;

	
	private int id;

	
	private int agentServerPort;

	
	private long hbTime;

	
	private byte status;

	
	private String database;

	
	private boolean enabled = true;

	
	private int checkFailTimes = 0;

	
	private byte lastError = Database.ERROR_NONE;

	
	private String sshUser = "";

	
	private int sshPort = 22;

	
	private boolean isInnodbFilePerTable = false;

	
	private boolean isMaster;

	
	private ArrayList<Database> slaveList;

	
	private int masterDbId;

	
	private int repDelay = Integer.MAX_VALUE;

	
	private int weight;

	
	private transient LoadBalancer<Database> loadBalancer;

	
	private Set<String> unRepTables;

	
	private int checkRepFailTimes = 0;

	
	private int checkRepTimeoutTimes = 0;

	
	private byte checkRepStatus = Database.REP_STATUS_NORMAL;

	
	private byte checkRepTimeoutStatus = Database.REP_TIMEOUT_NORMAL;

	
	private boolean isSwitchingRep = false;

	
	private String configFile;

	
	private int isDirty = DIRTY_NORMAL;

	
	private Set<String> dirtyPlys = null;

	
	private String testQuery = null;
	
	
	private boolean isAutoSwitchSlave = false;

	
	private List<String> connectionInitStatements = new LinkedList<String>();
	
	private List<String> connectionCleanupStatements = new LinkedList<String>();

	

	
	@Deprecated
	protected Database(final int id, final String dbName, final String IP,
			final int jPort, final String aURL, final int aPort,
			final String domainSchemaName, final String defaultTablespace) {
		this.id = id;
		this.name = dbName;
		this.parseUrl(aURL);
		this.url = aURL;
		if (!this.ipHost.equals(IP)) {
			throw new IllegalArgumentException("�ṩ���ݿ�������ַ(" + IP
					+ ")��URL�е�������ַ(" + this.ipHost + ")��һ��");
		}
		if (jPort != this.port) {
			throw new IllegalArgumentException("�ṩ���ݿ�˿�(" + jPort
					+ ")��URL�еĶ˿�(" + this.port + ")��һ��");
		}
		this.agentServerPort = aPort;
		this.hbTime = System.currentTimeMillis();
		this.status = Database.STATUS_NORMAL;
		this.domainSchemaName = domainSchemaName;
		this.defaultTablespace = defaultTablespace;

		
		this.isMaster = true;
		this.slaveList = new ArrayList<Database>();
		this.masterDbId = 0;
		this.weight = 1;
		this.unRepTables = new HashSet<String>();
		this.loadBalancer = null;
	}

	
	protected Database(final int id, final String name, final String url,
			final String domainSchemaName, final String defaultTablespace)
			throws IllegalArgumentException {
		this.id = id;
		this.name = name;
		this.url = url;
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("���ݿ�ڵ����Ʋ���Ϊnull����ַ���");
		}
		this.parseUrl(url);
		if (!Validator.isIpAddress(this.ipHost)
				&& !Validator.isHostname(this.ipHost)) {
			throw new IllegalArgumentException("����ȷ��IP��ַ��������: " + this.ipHost);
		}
		this.agentServerPort = 0;
		this.status = Database.STATUS_NORMAL;
		if ("".equals(domainSchemaName)) {
			this.domainSchemaName = this.getDatabase();
		} else {
			this.domainSchemaName = domainSchemaName;
		}
		if ("".equals(defaultTablespace)) {
			this.defaultTablespace = Definition.DEFAULT_TABLESPACE_NAME;
		} else {
			this.defaultTablespace = defaultTablespace;
		}

		
		this.isMaster = true;
		this.slaveList = new ArrayList<Database>();
		this.masterDbId = 0;
		this.weight = 1;
		this.unRepTables = new HashSet<String>();
		this.loadBalancer = null;
		this.testQuery = "select 1 from dual";
	}

	
	
	public abstract String getRealUserName(String userName);

	
	public abstract List<String> generateCreateUserStatements(final User user);

	
	public abstract List<String> generateDropUserStatements(final User user);

	
	public abstract List<String> generateAddHostStatements(final User user,
			Set<ConnectingHost> connectingHosts);

	
	public abstract List<String> generateDeleteHostStatements(final User user,
			Set<ConnectingHost> connectingHosts);

	
	public abstract List<String> generateGrantStatementsForUser(final User user);

	
	public abstract List<String> generateGrantStatementsForHosts(
			final User user, final Set<ConnectingHost> connectingHosts);

	
	public abstract List<String> generateGrantStatementsForPrivileges(
			final User user, final Set<EntityPrivilege> privileges);

	
	public abstract List<String> generateRevokeStatementsForUser(
			final User users);

	
	public abstract List<String> generateRevokeStatementsForHosts(
			final User user, final Set<ConnectingHost> connectingHosts);

	
	public abstract List<String> generateRevokeStatementsForPrivileges(
			final User user, final Set<EntityPrivilege> privileges);

	
	public abstract List<String> generateUserPwdChangeStatements(User user,
			String newPassword);

	
	
	public abstract DbnDataSource getDataSource(int socketTimeoutMs,
			int loginTimeoutMs) throws SQLException;

	
	public abstract DbnDataSource getDataSource(Properties properties,
			int socketTimeoutMs, int loginTimeoutMs) throws SQLException;

	
	public abstract DbnXADataSource getXADataSource(int socketTimeoutMs,
			int loginTimeoutMs) throws SQLException;

	
	public abstract DbnXADataSource getXADataSource(Properties properties,
			int socketTimeoutMs, int loginTimeoutMs) throws SQLException;

	
	@Override
	public Object clone() {
		try {
			final Database cloned = (Database) super.clone();
			return cloned;
		} catch (final CloneNotSupportedException e) {
			
			return null;
		}
	}

	public abstract Database copy();

	public void copyFrom(final Database anotherDb) {
		this.ipHost = anotherDb.ipHost;
		this.name = anotherDb.name;
		this.port = anotherDb.port;
		this.url = anotherDb.url;
		this.database = anotherDb.database;
	}

	
	@Override
	public boolean equals(final Object otherObject) {
		if (this == otherObject) {
			return true;
		}

		if (otherObject == null) {
			return false;
		}

		if (this.getClass() != otherObject.getClass()) {
			return false;
		}

		final Database other = (Database) otherObject;

		return this.url.equals(other.url);
	}

	
	@Override
	public int hashCode() {
		return this.url.hashCode();
	}

	
	public String getMySQLUrl() {
		final int slashslash = this.url.indexOf("
		final int slash = this.url.indexOf("/", slashslash + 2);
		return this.url.substring(0, slash) + "/mysql";
	}

	public String getDatabase() {
		return this.database;
	}

	
	protected void setDatabase(final String database) {
		this.database = database;
	}

	
	synchronized public String getName() {
		return this.name;
	}

	
	synchronized public String getIP() {
		return this.ipHost;
	}

	
	synchronized public int getPort() {
		return this.port;
	}

	
	synchronized public String getURL() {
		return this.url;
	}

	
	public String getManagementUserName() {
		return this.managementUserName;
	}

	
	public void setManagementUserName(final String managementUserName) {
		this.managementUserName = managementUserName;
	}

	
	synchronized public long getHbTime() {
		return this.hbTime;
	}

	
	synchronized public void setHbTime(final long hbTime) {
		this.hbTime = hbTime;
	}

	
	synchronized public byte getStatus() {
		return this.status;
	}

	
	synchronized public void setStatus(final byte status) {
		this.status = status;
	}

	
	synchronized public void setIp(final String ip) {
		this.ipHost = ip;
	}

	
	synchronized public void setName(final String name) {
		this.name = name;
	}

	
	synchronized public void setPort(final int port) {
		this.port = port;
	}

	
	synchronized public void setURL(final String url) {
		this.url = url;
	}

	
	public void setAgentServerPort(final int agentServerPort) {
		this.agentServerPort = agentServerPort;
	}

	
	public int getAgentServerPort() {
		return this.agentServerPort;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	
	protected abstract void parseUrl(String url)
			throws IllegalArgumentException;

	public int compareTo(final Database another) {
		if (another == null) {
			return -1;
		}
		return this.name.compareTo(another.name);
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public int getCheckFailTimes() {
		return this.checkFailTimes;
	}

	public void setCheckFailTimes(final int checkFailTimes) {
		this.checkFailTimes = checkFailTimes;
	}

	public int getSshPort() {
		return this.sshPort;
	}

	public void setSshPort(final int sshPort) {
		this.sshPort = sshPort;
	}

	public String getSshUser() {
		return this.sshUser;
	}

	public void setSshUser(final String sshUser) {
		this.sshUser = sshUser;
	}

	public void setInnodbFilePerTable(final boolean isInnodbFilePerTable) {
		this.isInnodbFilePerTable = isInnodbFilePerTable;
	}

	
	public void setMaster(final boolean isMaster) {
		this.isMaster = isMaster;
	}

	
	public void setSlaveList(final ArrayList<Database> slaveList)
			throws DBInfoException {
		

		this.slaveList = slaveList;
	}

	
	public ArrayList<Database> getSlaveList() {
		
		return this.slaveList;
	}

	
	public int getMasterDbId() {
		return (this.isMaster) ? -1 : this.masterDbId;
	}

	
	public void setMasterDb(final int masterDbId) throws DBInfoException {
		
		this.masterDbId = masterDbId;
	}

	
	public int getRepDelay() {
		
		return this.repDelay;
	}

	
	public void setRepDelay(final int repDelay) throws DBInfoException {
		
		this.repDelay = repDelay;
	}

	
	public int getWeight() {
		return this.weight;
	}

	
	public void setWeight(final int weight) throws DBInfoException {
		this.weight = weight;
	}

	
	public Database getLBNode(final LoadBalanceType lbType,
			final int validDelay, final String tableName)
			throws LoadBalanceException {
		

		if (this.slaveList.size() == 0) {
			if (lbType == LoadBalanceType.SLAVE_ONLY) {
				throw new LoadBalanceException("�޷��������Ҫ���slave�ڵ㣬slave�ڵ��б�Ϊ��");
			} else {
				return this; 
			}
		}

		Database dbChoice = null;

		
		dbChoice = this.loadBalancer.choose(null);
		if (dbChoice.isMaster()
				&& (lbType == LoadBalanceType.SLAVE_ONLY || lbType == LoadBalanceType.SLAVE_PREFER)) {
			
			dbChoice = this.loadBalancer.choose(null);
		}

		
		if (this.checkLBNodeValid(dbChoice, validDelay, tableName, lbType)
				&& dbChoice.isEnabled()) {
			return dbChoice;
		} else {
			
			final LoadBalancerIterator<Database> iter = this.loadBalancer
					.iterator();
			while (iter.hasNext()) {
				dbChoice = iter.next();
				if (this.checkLBNodeValid(dbChoice, validDelay, tableName,
						lbType)
						&& dbChoice.isEnabled()) {
					break;
				} else {
					dbChoice = null;
				}
			}
		}

		if (dbChoice != null) {
			return dbChoice; 
		} else {
			
			if (lbType == LoadBalanceType.SLAVE_ONLY) {
				throw new LoadBalanceException("�޷��������Ҫ���slave�ڵ�");
			} else {
				return this; 
			}
		}
	}

	
	private boolean checkLBNodeValid(final Database dbNode,	final int validDelay, final String tableName,
			final LoadBalanceType lbType) {
		if (lbType == LoadBalanceType.MASTER_ONLY) {
			return dbNode.isMaster();
		} else if (lbType == LoadBalanceType.LOAD_BALANCE) {
			if (dbNode.isMaster()) {
				return true;
			} else {
				return (dbNode.getRepDelay() < validDelay
						&& dbNode.getRepDelay() != STATUS_REP_FAIL && !dbNode
						.getUnRepTables().contains(tableName));
			}
		} else { 
			if (dbNode.isMaster()) {
				return false;
			} else {
				return (dbNode.getRepDelay() < validDelay
						&& dbNode.getRepDelay() != STATUS_REP_FAIL && !dbNode
						.getUnRepTables().contains(tableName));
			}
		}
	}

	
	public void disableSlave() throws DBInfoException {
		
		this.repDelay = Database.STATUS_REP_FAIL;
	}

	
	public Set<String> getUnRepTables() {
		
		return this.unRepTables;
	}

	
	public void setUnRepTables(final Set<String> unRepTables)
			throws DBInfoException {
		
		this.unRepTables = unRepTables;
	}

	
	@SuppressWarnings("unchecked")
	public void rebuildLoadBalancer() throws DBInfoException {
		
		final ArrayList<Database> dbList = (ArrayList<Database>) (this.slaveList
				.clone());
		dbList.add(this);
		final ArrayList<Integer> weights = new ArrayList<Integer>();
		for (final Database db : dbList) {
			final int weight = db.getWeight();
			if (weight <= 0) {
				throw new DBInfoException(
						"rebuildLoadBalancer fail, data node :" + db.getURL()
								+ "'s weight is:" + weight);
			}
			weights.add(weight);
		}
		this.loadBalancer = new DBNLoadBalancer<Database>("dbnLoadBalancer",
				dbList, weights);
	}

	
	
	
	public int getCheckRepFailTimes() {
		
		return this.checkRepFailTimes;
	}

	
	public void setCheckRepFailTimes(final int checkRepFailTimes) {
		
		this.checkRepFailTimes = checkRepFailTimes;
	}

	
	public byte getCheckRepStatus() {
		return this.checkRepStatus;
	}

	
	public void setCheckRepStatus(final byte checkRepStatus) {
		this.checkRepStatus = checkRepStatus;
	}

	public void setSwitchingRep(final boolean isSwitchingRep) {
		this.isSwitchingRep = isSwitchingRep;
	}

	
	public void initSlave() {
		
		this.disableSlave(); 
		this.setCheckFailTimes(0);
		this.setCheckRepStatus(Database.STATUS_NORMAL);
	}

	public String getConfigFile() {
		return this.configFile;
	}

	public void setConfigFile(final String configFile) {
		this.configFile = configFile;
	}

	
	public void setDirty(final int isDirty, Set<String> dirtyPlys) {
		this.isDirty = isDirty;
		if (isDirty == DIRTY_NORMAL) {
			dirtyPlys = null;
		} else {
			this.dirtyPlys = dirtyPlys;
		}
	}

	public byte getCheckRepTimeoutStatus() {
		return this.checkRepTimeoutStatus;
	}

	public void setCheckRepTimeoutStatus(final byte checkRepTimeoutStatus) {
		this.checkRepTimeoutStatus = checkRepTimeoutStatus;
	}

	public int getCheckRepTimeoutTimes() {
		return this.checkRepTimeoutTimes;
	}

	public void setCheckRepTimeoutTimes(final int checkRepTimeoutTimes) {
		this.checkRepTimeoutTimes = checkRepTimeoutTimes;
	}

	public byte getLastError() {
		return this.lastError;
	}

	public void setLastError(final byte lastError) {
		this.lastError = lastError;
	}

	public String getStatusDescEn() {
		if (this.status == Database.STATUS_NORMAL) {
			return "ALIVE";
		} else if (this.status == Database.STATUS_DEAD) {
			return "DEAD";
		} else if (this.status == Database.STATUS_DISABLE) {
			return "DEAD&DISABLE";
		} else {
			return "UNKNOWN";
		}
	}

	public String getStatusDescCn() {
		if (this.status == Database.STATUS_NORMAL) {
			return "����";
		} else if (this.status == Database.STATUS_DEAD) {
			return "�޷�����";
		} else if (this.status == Database.STATUS_DISABLE) {
			return "���ӳ�ʱ������";
		} else {
			return "δ֪״̬";
		}
	}

	public Set<String> getDirtyPlys() {
		return this.dirtyPlys;
	}

	public void setDirtyPlys(final Set<String> dirtyPlys) {
		this.dirtyPlys = dirtyPlys;
	}

	public void setTestQuery(final String query) {
		this.testQuery = query;
	}

	public String getTestQuery() {
		return this.testQuery;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	

	public int isDirty() {
		return this.isDirty;
	}

	public boolean inNormalStatus() {
		return this.status == Database.STATUS_NORMAL;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean isInnodbFilePerTable() {
		return this.isInnodbFilePerTable;
	}

	
	public boolean isMaster() {
		return this.isMaster;
	}

	
	public abstract boolean isSameDBType(Database another);

	public boolean isSwitchingRep() {
		return this.isSwitchingRep;
	}

	

	
	public abstract DbnType getDbnType();

	
	public List<String> getConnectionInitStatements() {
		return this.connectionInitStatements;
	}

	
	public void addConnectionInitStatements(String statement) {
		this.connectionInitStatements.add(statement);
	}

	
	public void removeConnectionInitStatements(String statement) {
		this.connectionInitStatements.remove(statement);
	}

	
	protected void setConnectionInitStatements(
			final List<String> connectionInitStatements) {
		this.connectionInitStatements = connectionInitStatements;
	}

	
	public List<String> getConnectionCleanupStatements() {
		return this.connectionCleanupStatements;
	}

	
	public void addConnectionCleanupStatements(String statement) {
		this.connectionCleanupStatements.add(statement);
	}

	
	public void removeConnectionCleanupStatements(String statement) {
		this.connectionCleanupStatements.remove(statement);
	}

	
	protected void setConnectionCleanupStatements(
			final List<String> connectionCleanupStatements) {
		this.connectionCleanupStatements = connectionCleanupStatements;
	}

	
	public String getDomainSchemaName() {
		return this.domainSchemaName;
	}

	
	public void setDomainSchemaName(final String domainSchemaName) {
		this.domainSchemaName = domainSchemaName.toUpperCase();
	}

	
	public String getDefaultTablespace() {
		return this.defaultTablespace;
	}

	
	public void setDefaultTablespace(final String defaultTablespace) {
		this.defaultTablespace = defaultTablespace;
	}
	
	
	public boolean isAutoSwitchSlave() {
		return isAutoSwitchSlave;
	}

	public void setAutoSwitchSlave(boolean isAutoSwitchSlave) {
		if (this.isMaster && isAutoSwitchSlave)
			throw new IllegalStateException("master dbn can't set to be autoswitch slave.");
		this.isAutoSwitchSlave = isAutoSwitchSlave;
	}
	
	
	public Database getAutoSwitchSlave() {
		if (isMaster && this.getSlaveList() != null) {
			for (Database slave : this.getSlaveList()) {
				if (slave.isAutoSwitchSlave())
					return slave;
			}
		}
		return null;
	}

}
