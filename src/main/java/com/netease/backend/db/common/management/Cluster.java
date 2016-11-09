package com.netease.backend.db.common.management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.transaction.xa.Xid;

import com.netease.backend.db.common.Disposable;
import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.definition.Definition;
import com.netease.backend.db.common.definition.LoadBalanceType;
import com.netease.backend.db.common.enumeration.SQLType;
import com.netease.backend.db.common.exceptions.CryptException;
import com.netease.backend.db.common.exceptions.DBSQLException;
import com.netease.backend.db.common.exceptions.LoadBalanceException;
import com.netease.backend.db.common.exceptions.LoadClassException;
import com.netease.backend.db.common.exceptions.MSException;
import com.netease.backend.db.common.exceptions.SysDBException;
import com.netease.backend.db.common.management.dbi.DBIConfig;
import com.netease.backend.db.common.management.model.DBUserOp;
import com.netease.backend.db.common.schema.AlarmInfo;
import com.netease.backend.db.common.schema.BucketInfo;
import com.netease.backend.db.common.schema.ColumnInfo;
import com.netease.backend.db.common.schema.DataValidateDb;
import com.netease.backend.db.common.schema.DataValidateTask;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnCluster;
import com.netease.backend.db.common.schema.Hash;
import com.netease.backend.db.common.schema.HashFunction;
import com.netease.backend.db.common.schema.IndexColumn;
import com.netease.backend.db.common.schema.IndexInfo;
import com.netease.backend.db.common.schema.MigTaskInfo;
import com.netease.backend.db.common.schema.Model;
import com.netease.backend.db.common.schema.OnlineAlterTaskInfo;
import com.netease.backend.db.common.schema.OnlineMigStartId;
import com.netease.backend.db.common.schema.OnlineMigTaskInfo;
import com.netease.backend.db.common.schema.Policy;
import com.netease.backend.db.common.schema.Routine;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.TableMigResult;
import com.netease.backend.db.common.schema.Trigger;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.schema.XABInfo;
import com.netease.backend.db.common.sql.SAlterTable;
import com.netease.backend.db.common.sql.SAlterTableAddColumn;
import com.netease.backend.db.common.sql.SAlterTableAddIndex;
import com.netease.backend.db.common.sql.SAlterTableChangeColumn;
import com.netease.backend.db.common.sql.SAlterTableComment;
import com.netease.backend.db.common.sql.SAlterTableCommentColumn;
import com.netease.backend.db.common.sql.SAlterTableDropColumn;
import com.netease.backend.db.common.sql.SAlterTableDropIndex;
import com.netease.backend.db.common.sql.SAlterTableModifyColumn;
import com.netease.backend.db.common.sql.SAlterTableOp;
import com.netease.backend.db.common.sql.SAlterTableRename;
import com.netease.backend.db.common.sql.SAlterTableRenameColumn;
import com.netease.backend.db.common.sql.plan.SPlan;
import com.netease.backend.db.common.stat.StatTask;
import com.netease.backend.db.common.utils.DDBClassLoader;
import com.netease.backend.db.common.utils.Message;
import com.netease.backend.db.common.utils.RWLock;
import com.netease.backend.db.common.utils.StringUtils;


public class Cluster implements DDBStatus, Serializable, Cloneable, Disposable {

	private static final long					serialVersionUID			= 5063630628195027791L;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final int						REASON_DB_FAULT				= 1;
	
	public static final int						REASON_DB_UPDATE			= 2;
	
	public static final int						REASON_DB_AS_FAULT			= 3;
	
	public static final int						MIG_STATUS_NORMAL			= 1;
	
	public static final int						MIG_STATUS_MIGRATING		= 2;
	
	public static final int						MIG_STATUS_REQ_HANGING		= 3;
	
	public static final int						MIG_STATUS_MIG_HANGING		= 4;
	
	public static final int						MIG_STATUS_FIN_HANGING		= 5;
	
	public static final int						MIG_STATUS_FAIL_HANGING		= 6;
	
	public static final int						MIG_STATUS_MIG_INTERRUPT	= 7;
	
	public static final int						MIG_XA_FINISH				= 1;
	
	public static final int						MIG_XA_COMMIT				= 2;
	
	public static final int						MIG_XA_ROLLBACK				= 3;
	
	public static final int						MIG_XA_SELECT				= 4;
	
	public static final byte					RM_OPT_ADD					= 1;
	
	public static final byte					RM_OPT_DEL					= 2;

	
	
	
	private long								baseStatTime;
	
	private int									status;
	
	transient private Key						key;
	
	transient private String					sysUserName;
	
	private List<AlarmInfo>						alarmList					= null;
	
	private Map<String, Database>				dbMap						= null;
	
	private Map<String, TableInfo>				tableMap					= null;
	
	private Map<String, Model>					modelMap					= null;
	
	private Map<String, Policy>					policyMap					= null;
	
	private Map<String, HashFunction>			hashFunctionMap				= null;
	
	private Map<String, DbnCluster>				dbnClusterMap				= null;
	
	transient private Map<Xid, XABInfo>			xabMap						= null;
	
	transient private List<String>				qsIpList					= null;
	
	private MasterStatus						masterStatus;
	
	private List<MigTaskInfo>					migTaskList					= null;
	
	transient private List<StatTask>			statTaskList				= null;
	
	transient private final RWLock				loginLock					= new RWLock();			;
	
	
	
	transient private DBUserOp					dbUserOp					= null;
	
	private Map<String, Trigger>				triggerMap					= null;
	
	private Map<String, Routine>				routineMap					= null;

	
	private DBIConfig							clientConfig				= null;
	
	private DDBConfig							config						= null;

	
	transient private volatile Map<String, User> userMap = null;


	
	private volatile Map<String, SPlan>			plans						= null;

	
	
	
	

	
	transient private ClusterAccess			access						= null;
	
	transient private ClusterPersist			persist						= null;

	
	public ClusterAccess getAccess() {
		return access;
	}

	
	public void setAccess(
		ClusterAccess access)
	{
		this.access = access;
	}

	
	public ClusterPersist getPersist() {
		return persist;
	}

	
	public void setPersist(
		ClusterPersist persist)
	{
		this.persist = persist;
	}

	


	
	public Cluster() {
	}

	
	public DDBURL constructConnURL() {
		final DDBURL connURL = new DDBURL(this.getHost(), this.getPort(), this
				.getName());

		final String sysUserName = this.getSysUsername();
		String password = "";
		final User user;
		if ((user = this.getUser(sysUserName)) != null) {
			if (user.getPassword() == null) {
				try {
					user.decrypt(this.getKey());
				} catch (CryptException ex) {
					throw new RuntimeException(ex);
				}
			}
			password = user.getPassword();
			
			connURL.setUserName(sysUserName);
			connURL.setPassword(password);
		}
		return connURL;
	}

	
	public void dispose() {
	}

	
	public void reloadContext()
		throws MSException
	{
		
		this.reloadConfig();

		
		
		final ClusterAccess access = this.getAccess();
		if (access == null)
			throw new IllegalStateException(
					"DDB context access DAO service not set in");
		try {
			
			userMap = access.getUsers();
			
			dbMap = access.getDatabases();
			
			for (final Database db : dbMap.values()) {
				if (db.isMaster() && (db.getSlaveList().size() > 0)) {
					db.rebuildLoadBalancer();
				}
			}
			
			final Map<Integer, Database> databases = new HashMap<Integer, Database>();
			for (final Database db : dbMap.values()) {
				databases.put(db.getId(), db);
			}

			
			policyMap = access.getPolicies(dbMap);
			
			recoverPolicyBuckets(policyMap);
			
			hashFunctionMap = access.getHashFunctions();
			
			
			testHashFunction(hashFunctionMap);
			

			
			tableMap = access.getTables(policyMap);
			
			for (final TableInfo table : tableMap.values()) {
				policyMap.get(table.getBalancePolicy().getName()).addTableInfo(
						table);
			}
			
			triggerMap = access.getTriggers(tableMap, databases);
			
			routineMap = access.getRoutines(databases);
			
			modelMap = access.getModels(tableMap);
			
			dbnClusterMap = access.getDbnClusters();

			
			xabMap = new HashMap<Xid, XABInfo>();
			
			migTaskList = access.getMigTasks();
			
			masterStatus = access.getMasterStatus();
			
			if (this.getMigStatus() == MIG_STATUS_NORMAL) {
				this.status = DDBStatus.STATUS_NORMAL;
			} else { 
				this.status = DDBStatus.STATUS_MIGRATION_FAILED; 
				if (this.isMigrating()) {
					this.setMigStatus(MIG_STATUS_MIG_HANGING);
				}
				
				if (this.getMigType() == MigTaskInfo.MIG_TYPE_ONLINE) {
					final Policy migPly = this.getMigPolicy();
					if (migPly != null) {
						migPly.addDb(migPly.getDesDb());
					}
				}
			}
			
			statTaskList = access.getStatTasks();
			
			this.setBaseStatTime(System.currentTimeMillis());
			
			alarmList = access.getAlarms();
			
			dbUserOp = DBUserOp.readOp(Definition.DBUSER_ERR_FILE);

		} catch (final SysDBException ex) {
			throw new MSException("Reload DDB context from system DB error", ex);
		}
	}

	
	public void recoverContextOnDBI()
		throws MSException
	{
		this.recoverPolicyBuckets();
		this.reloadHashPolicies();
		
		
		for (Database db : dbMap.values()) {
			if (db.isMaster() && db.getSlaveList().size() > 0) {
				db.rebuildLoadBalancer();
			}
		}
		
		
		if (getMigStatus() != Cluster.MIG_STATUS_NORMAL)
			setMigStatus(Cluster.MIG_STATUS_MIGRATING);

		
		reSetTableID();

		
		setBaseStatTime(System.currentTimeMillis());

	}
	
	
	public void reSetTableID() {
		for (TableInfo table : tableMap.values())
			table.setIDRange(0, 0);
	}


	public void recoverPolicyBuckets() {
		recoverPolicyBuckets(this.getPolicyMap());
	}

	
	public static void recoverPolicyBuckets(
		Map<String, Policy> policies)
	{
		if (policies != null) {
			for (final Policy policy : policies.values()) {
				policy.recoverBucketList();
			}
		}
	}

	private void reloadHashPolicies()
		throws MSException
	{
		reloadHashPolicies(this.getPolicyMap(), this.getHashFunctionMap());
	}

	private static void reloadHashPolicies(
		Map<String, Policy> policies, Map<String, HashFunction> hashFunctions)
		throws MSException
	{
		
		testHashFunction(hashFunctions);

		for (final Policy policy : policies.values()) {
			if (policy.getPolicyType() == Policy.TYPE_DYNAMIC_HASH) {
				final String hashName = policy.getHashName();
				final HashFunction hashFunction = hashFunctions.get(hashName);
				final Hash hash;
				try {
					hash = (Hash) DDBClassLoader.newObjectInstance(hashFunction
							.getClassName());
					hash.setBucketCount(policy.getBucketCount());

				} catch (final LoadClassException ex) {
					throw new MSException("Load hash function '" + hashName
							+ "' failed!" + ex.getMessage());
				}
				policy.setHash(hash);
			}
		}
	}

	
	private static void testHashFunction(
		Map<String, HashFunction> hashFunctions)
		throws MSException
	{
		String hashName = null;
		try {
			for (final HashFunction hash : hashFunctions.values()) {
				hashName = hash.getName();
				DDBClassLoader.loadClassDefinition(hash.getClassName(), hash
						.getClassBytes());
			}
		} catch (final LoadClassException ex) {
			throw new MSException("Load hash function '" + hashName
					+ "' failed!" + ex.getMessage());
		}
	}


	
	public boolean existUser(
		String userName)
	{
		return this.getUserMap().containsKey(userName);
	}

	
	public User getUser(
		String userName)
	{
		return this.getUserMap().get(userName);
	}

	
	public User getSysDBUser() {
		return this.getUser(this.getSysDBUserName());
	}

	
	public Map<String, User> getUserMap() {
		return userMap;
	}

	

	
	public Map<String, SPlan> getPlans() {
		return this.plans;
	}

	
	public SPlan getPlan(
		String name)
	{
		return this.getPlans().get(name);
	}

	
	public void setPlans(
		Map<String, SPlan> plans)
	{
		this.plans = plans;
	}

	

	
	public DDBConfig getConfig() {
		return config;
	}

	
	protected void setConfig(
		DDBConfig config)
	{
		this.config = config;
	}

	
	public DDBConfig getDDBConfig() {
		return this.getConfig();
	}

	
	public void reloadConfig()
		throws MSException
	{
		
		this.setSysUsername(Definition.DEFAULT_SYS_USER);

		final ClusterAccess access = this.getAccess();
		if (access == null)
			throw new IllegalStateException("DDB�����ķ���DAO���񲻿��ã�");

		
		try {
			this.setConfig(access.getDDBConfig());
			this.setClientConfig(access.getClientConfig());
		} catch (final SysDBException ex) {
			throw new MSException("�������ļ���ϵͳ���ȡDDB����ʧ�ܣ�", ex);
		}
	}

	
	public String getName() {
		return this.getConfig().getName();
	}

	
	public String getHost() {
		return this.getConfig().getHost();
	}

	
	public int getPort() {
		return this.getConfig().getPort();
	}

	
	public int getDbaPort() {
		return this.getConfig().getDbaPort();
	}

	public String getPidPath() {
		return this.getConfig().getPidPath();
	}

	
	public int getSocketTimeout() {
		return this.getConfig().getSocketTimeout();
	}

	
	
	
	
	
	

	
	public String[] getToList() {
		return this.getConfig().getToList();
	}

	
	
	
	
	

	
	public String[] getCCList() {
		return this.getConfig().getCCList();
	}

	
	
	
	
	

	
	public String getEmailAddress() {
		return this.getConfig().getEmailAddress();
	}

	
	
	
	
	

	
	public String getEmailPass() {
		return this.getConfig().getEmailPass();
	}

	
	
	
	
	

	
	public String[] getMobleList() {
		return this.getConfig().getMobileList();
	}

	
	
	
	
	

	
	public String getSmsUrl() {
		return this.getConfig().getSmsUrl();
	}

	
	public String getSmtpServer() {
		return this.getConfig().getSmtpServer();
	}

	
	
	
	
	

	
	public boolean isCheckHB() {
		return this.getConfig().isCheckHB();
	}

	
	public boolean isCheckDBN() {
		return this.getConfig().isCheckDbn();
	}

	
	
	
	
	

	
	public boolean isCheckSuspendXA() {
		return this.getConfig().isCheckSuspendXA();
	}

	
	
	
	
	

	
	public boolean isCheckSysDB() {
		return this.getConfig().isCheckSysDB();
	}

	
	
	
	
	

	
	public long getDbnHBInterval() {
		return this.getConfig().getDbnHBInterval();
	}

	
	public long getDbnReportInterval() {
		return this.getConfig().getDbnReportInterval();
	}

	
	public long getDeadCheckInterval() {
		return this.getConfig().getDeadCheckInterval();
	}

	
	
	
	
	
	

	public long getDeadAssureInterval() {
		return this.getConfig().getDeadAssureInterval();
	}

	
	
	
	
	

	
	public String getSysDBPass() {
		return this.getConfig().getSysDBPass();
	}

	
	
	
	
	
	

	
	public String getSysDBURL() {
		return this.getConfig().getSysDBURL();
	}

	
	
	
	
	
	

	
	public String getSysDBUserName() {
		return this.getConfig().getSysDBUser();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	

	
	public int getStatus() {
		return status;
	}

	public boolean isClosed() {
		final int status = this.getStatus();
		return status == DDBStatus.STATUS_STOP;
	}

	public boolean isStatusNormal() {
		final int status = this.getStatus();
		return status == DDBStatus.STATUS_NORMAL;
	}

	
	public void setStatus(
		int status)
	{
		this.status = status;
	}

	
	synchronized public boolean setSysStatus(
		int sysStatus)
	{
		final int status = this.getStatus();
		switch (sysStatus) {
		case STATUS_NORMAL: 
			this.setStatus(sysStatus);

		case STATUS_STOP: 
			if ((status != STATUS_STOPPING) && (status != STATUS_NORMAL))
				return false;
			this.setStatus(sysStatus);
			return true;

		case STATUS_MIGRATION: 
			if ((status != STATUS_NORMAL)
					&& (status != STATUS_MIGRATION_FAILED))
				return false;
			this.setStatus(sysStatus);
			return true;

		case STATUS_MIGRATION_FAILED: 
			if (status == STATUS_MIGRATION) {
				this.setStatus(sysStatus);
			} else
				return false;

		case STATUS_BACKUP: 
			if (status != STATUS_NORMAL)
				return false;
			this.setStatus(sysStatus);
			return true;

		case STATUS_CONFIG: 
		case STATUS_STOPPING: 
		case STATUS_DUMP: 
		case STATUS_UPDATE: 
			if (status != STATUS_NORMAL)
				return false;
			this.setStatus(sysStatus);
			return true;
		default:
			return false;
		}
	}

	
	public int getSysStatus() {
		return this.getStatus();
	}

	
	public String getStatusDesc() {
		final int status = this.getStatus();
		switch (status) {
		case STATUS_NORMAL: 
			return "��������";
		case STATUS_STOP: 
			return "��ֹͣ";
		case STATUS_STOPPING: 
			return "���ڹر�";
		case STATUS_MIGRATION: 
			return "����Ǩ������";
		case STATUS_MIGRATION_FAILED: 
			return "Ǩ�Ʋ��ɹ�";
		case STATUS_BACKUP: 
			return "���ڱ�������";
		case STATUS_DUMP: 
			return "���ڵ�������";
		case STATUS_CONFIG: 
			return "���ڸ�������";
		case STATUS_UPDATE: 
			return "��������";

		default:
			return "״̬����";
		}
	}

	
	public String getStatusENDesc() {
		final int status = this.getStatus();
		switch (status) {
		case STATUS_NORMAL: 
			return "in normal status";
		case STATUS_STOP: 
			return "stopped";
		case STATUS_STOPPING: 
			return "closing";
		case STATUS_MIGRATION: 
			return "migrating data";
		case STATUS_BACKUP: 
			return "backuping data";
		case STATUS_DUMP: 
			return "dumping data";
		case STATUS_CONFIG: 
			return "altering configuation";
		case STATUS_UPDATE: 
			return "updating";

		default:
			return "in wrong status";
		}
	}

	
	public Policy getMigPolicy() {
		return this.policyMap.get(masterStatus.getMigPolicy());
	}

	
	public void setMigPolicy(
		String policyName)
	{
		masterStatus.setMigPolicy(policyName);
	}

	
	public int getDbCount() {
		if (this.dbMap == null)
			return 0;
		else
			return this.dbMap.size();
	}

	
	public HashMap<String, Database> getDbMap() {
		return (HashMap<String, Database>) dbMap;
	}

	
	public int getTableCount() {
		if (this.tableMap == null)
			return 0;
		else
			return tableMap.size();
	}

	
	public Map<String, TableInfo> getTableMap() {
		return tableMap;
	}

	
	public Map<String, TableInfo> getTableMapByDbnType(
		DbnType dbnType)
	{
		final Map<String, TableInfo> newTableMap = new HashMap<String, TableInfo>();
		newTableMap.putAll(this.tableMap);
		final Set<Entry<String, TableInfo>> entrySet = newTableMap.entrySet();
		final Iterator<Entry<String, TableInfo>> entrySetIterator = entrySet
				.iterator();
		while (entrySetIterator.hasNext()) {
			final Entry<String, TableInfo> entry = entrySetIterator.next();
			if (entry.getValue().getDbnType() != dbnType) {
				entrySetIterator.remove();
			}
		}

		return newTableMap;
	}

	
	public Map<String, TableInfo> getTableMapByType(
		String type)
	{
		final Map<String, TableInfo> newTableMap = new HashMap<String, TableInfo>();
		newTableMap.putAll(this.tableMap);
		final Set<Entry<String, TableInfo>> entrySet = newTableMap.entrySet();
		final Iterator<Entry<String, TableInfo>> entrySetIterator = entrySet
				.iterator();
		while (entrySetIterator.hasNext()) {
			final Entry<String, TableInfo> entry = entrySetIterator.next();
			if (!entry.getValue().getType().equals(type)) {
				entrySetIterator.remove();
			}
		}

		return newTableMap;
	}

	
	public Map<String, Model> getModelMap() {
		return modelMap;
	}

	
	public Model getModel(
		String mdlName)
	{
		return modelMap.get(mdlName);
	}

	
	public void updateModelMap(
		String mdlName, String tblName)
	{
		final TableInfo table = this.getTableInfo(tblName);
		
		if (null != table.getModelName()) {
			final Model oldModel = this.getModel(table.getModelName());
			if (null != oldModel) {
				oldModel.removeTable(tblName);
				if (oldModel.isEmpty()) {
					this.getModelMap().remove(oldModel.getName());
				}
			}
		}
		
		if ((null != mdlName) && !("".equals(mdlName))) {
			if (null != this.getModel(mdlName)) {
				final Model model = this.getModel(mdlName);
				model.addTable(tblName);
			} else {
				final Model model = new Model(mdlName, tblName);
				this.getModelMap().put(mdlName, model);
			}
		}
	}

	
	public List<String> getTableNameList() {
		final List<String> result = new LinkedList<String>();
		for (final TableInfo t : tableMap.values()) {
			if (!t.isView()) {
				result.add(t.getName());
			}
		}
		return result;
	}

	
	synchronized public int getNewMigTaskID()
		throws MSException
	{
		int assignedMigTaskID = masterStatus.getAssignedMigTaskID();
		if (assignedMigTaskID == Long.MAX_VALUE) {
			assignedMigTaskID = 1;
		} else {
			assignedMigTaskID += 1;
		}

		masterStatus.setAssignedMigTaskID(assignedMigTaskID);
		try {
			this.getPersist().updateAssignedMigTaskID(assignedMigTaskID);
		} catch (final SysDBException se) {
			throw new MSException("�־û��ѷ����Ǩ������idʧ�ܣ�" + se.getMessage());
		}
		return assignedMigTaskID;
	}

	
	synchronized public int getAssignedMigTaskID() {
		return masterStatus.getAssignedMigTaskID();
	}

	
	public List<AlarmInfo> getAlarmList() {
		return alarmList;
	}

	
	public void setAlarmList(
		List<AlarmInfo> alarms)
	{
		this.alarmList = alarms;
	}

	
	public Map<String, Policy> getPolicyMap() {
		return policyMap;
	}

	
	public Map<String, HashFunction> getHashFunctionMap() {
		return hashFunctionMap;
	}

	
	public Map<String, DbnCluster> getDbnClusterMap() {
		return dbnClusterMap;
	}

	
	public void setDbnClusterMap(
		Map<String, DbnCluster> dbnClusterMap)
	{
		this.dbnClusterMap = dbnClusterMap;
	}

	
	public DbnCluster getDbnCluster(
		String clusterName)
	{
		return dbnClusterMap.get(clusterName);
	}

	

	public TableInfo getTableInfo(
		String tableName)
	{
		return tableMap.get(tableName);
	}

	

	public Policy getPolicy(
		String policyName)
	{
		return policyMap.get(policyName);
	}

	
	public HashFunction getHashFunction(
		String name)
	{
		return hashFunctionMap.get(name);
	}

	
	public Database getDbInfo(
		String dbURL)
	{
		return dbMap.get(dbURL);
	}

	
	public Database getDbInfoById(
		int id)
	{
		synchronized (dbMap) {
			final Iterator<Database> itr = dbMap.values().iterator();
			while (itr.hasNext()) {
				final Database db = itr.next();
				if (db.getId() == id)
					return db;
			}
		}
		return null;
	}

	public Database getDbInfoByName(
		String name)
	{
		synchronized (dbMap) {
			final Iterator<Database> itr = dbMap.values().iterator();
			while (itr.hasNext()) {
				final Database db = itr.next();
				if (db.getName().equals(name))
					return db;
			}
		}
		return null;
	}

	

	public Policy getTablePolicy(
		String tableName)
	{
		final TableInfo table = this.tableMap.get(tableName);
		if (table == null)
			return null;
		else
			return table.getBalancePolicy();
	}

	
	public byte getTableStatus(
		String tableName)
	{
		final TableInfo table = this.tableMap.get(tableName);
		final Policy ply = table.getBalancePolicy();
		return ply.getStatus();
	}

	
	synchronized public void setMigStart(
		MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		
		final Policy ply = this.getPolicy(task.getPolicy());
		final Database desDb = this.getDbInfo(task.getDesDBUrl());
		ply.setDesDb(desDb);
		ply.addDb(desDb);
		ply.setMigBuckets(task.getBucketNos());
		ply.setStatus(Policy.STATUS_MOVING);

		
		this.setMigStatus(MIG_STATUS_MIGRATING);
		this.setMigType(task.getType());
		this.setMigPolicy(ply.getName());
		ply.setMigBuckets(task.getBucketNos());

		
		task.setStatus(MigTaskInfo.STATUS_REQUEST);
		task.setStartTime(new Date());
		task.setFinishedRecord(0);
		task.setTotalRecord(0);

		
		if (!isPersist)
			return;

		try {
			this.clusterPersist(this, ply, null);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("��������Ǩ��ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public void setMigReqRecovery(
		MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		
		this.setMigStatus(MIG_STATUS_REQ_HANGING);

		
		task.setFinishTime(new Date());
		task.setStatus(MigTaskInfo.STATUS_REQUEST_HANGING);

		
		if (!isPersist)
			return;

		try {
			this.clusterPersist(this, null, null);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("Ǩ����������ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public void setMigRecovery(
		int migStatus, MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		this.setMigStatus(migStatus);

		
		task.setFinishTime(new Date());
		if (migStatus == Cluster.MIG_STATUS_MIG_INTERRUPT) {
			task.setStatus(MigTaskInfo.STATUS_MENUAL_INTERRUPTED);
		} else {
			task.setStatus(migStatus);
		}

		
		if (!isPersist)
			return;
		try {
			this.clusterPersist(this, null, null);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("Ǩ���쳣����ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public void setMigFailedRecovery(
		MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		this.setMigStatus(MIG_STATUS_FAIL_HANGING);

		
		task.setFinishTime(new Date());
		task.setStatus(MigTaskInfo.STATUS_TRANSFER_HANGING);

		
		if (!isPersist)
			return;
		try {
			this.clusterPersist(this, null, null);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("Ǩ��ʧ��ȷ������ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public void setMigFinishRecovery(
		MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		
		this.setMigStatus(MIG_STATUS_FIN_HANGING);

		
		task.setStatus(MigTaskInfo.STATUS_FEEDBACK_HANGING);
		task.setFinishTime(new Date());

		
		if (!isPersist)
			return;
		try {
			this.clusterPersist(this, null, null);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("Ǩ��ȷ������ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public void setMigFinish(
		MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		
		final Policy ply = this.getPolicy(task.getPolicy());
		final ArrayList<BucketInfo> buckets = ply.getBuckets();
		final Database srcDB = this.getDbInfo(task.getSrcDBUrl());
		final BucketInfo[] migBuckets = new BucketInfo[task.getBucketNos().length];
		for (int i = 0; i < task.getBucketNos().length; i++) {
			migBuckets[i] = buckets.get(task.getBucketNos()[i]);
			migBuckets[i].setSrcDB(ply.getDesDb());
		}
		ply.setDesDb(null);

		
		if (!ply.isUseDB(srcDB)) {
			ply.delDb(srcDB);
		}

		ply.setStatus(Policy.STATUS_NORMAL);
		ply.setMigBuckets(null);

		
		this.setMigStatus(MIG_STATUS_NORMAL);
		this.setMigPolicy("");
		this.setMigXAOpt(MIG_XA_FINISH);

		
		task.setStatus(MigTaskInfo.STATUS_SUCCESS_FINISHED);
		task.setFinishTime(new Date());

		
		if (!isPersist)
			return;
		try {
			this.clusterPersist(this, ply, migBuckets);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("Ǩ����������ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public void setMigFailed(
		MigTaskInfo task, boolean isPersist)
		throws MSException
	{
		
		final Policy ply = this.getPolicy(task.getPolicy());
		final ArrayList<BucketInfo> buckets = ply.getBuckets();
		final BucketInfo[] migBuckets = new BucketInfo[task.getBucketNos().length];
		for (int i = 0; i < task.getBucketNos().length; i++) {
			migBuckets[i] = buckets.get(task.getBucketNos()[i]);
		}

		
		final Database desDB = ply.getDesDb();
		ply.setDesDb(null);
		if (!ply.isUseDB(desDB)) {
			ply.delDb(desDB);
		}

		ply.setStatus(Policy.STATUS_NORMAL);
		ply.setMigBuckets(null);

		
		this.setMigStatus(MIG_STATUS_NORMAL);
		this.setMigPolicy("");
		this.setMigXAOpt(MIG_XA_FINISH);

		
		task.setStatus(MigTaskInfo.STATUS_FAIL_FINISHED);
		task.setFinishTime(new Date());

		
		if (!isPersist)
			return;
		try {
			this.clusterPersist(this, ply, migBuckets);
			persist.updateMigTask(task);
		} catch (final SysDBException e) {
			throw new MSException("����Ǩ��ʧ��ʱǨ��״̬�־û��쳣��" + e.getMessage());
		}
	}

	
	synchronized public boolean isMigNormal() {
		return this.getMigStatus() == MIG_STATUS_NORMAL;

	}

	
	synchronized public boolean isMigrating() {
		return this.getMigStatus() == MIG_STATUS_MIGRATING;
	}

	
	synchronized public boolean isMigRequsting() {
		return this.getMigStatus() == MIG_STATUS_REQ_HANGING;

	}

	
	synchronized public boolean isMigHanging() {
		return this.getMigStatus() == MIG_STATUS_MIG_HANGING;
	}

	
	synchronized public boolean isMigFinHang() {
		return this.getMigStatus() == MIG_STATUS_FIN_HANGING;
	}

	
	synchronized public boolean isMigFailedHang() {
		return this.getMigStatus() == MIG_STATUS_FAIL_HANGING;
	}

	
	synchronized public List<TableInfo> getMigTables() {
		if (!this.isMigrating())
			return null;

		final Policy ply = this.policyMap.get(this.getMigPolicy());

		if (ply == null)
			return null;
		else
			return ply.getTableList();
	}

	
	synchronized public Database getMigDesDb() {
		if (!this.isMigrating())
			return null;

		final Policy ply = this.policyMap.get(this.getMigPolicy());
		if (ply == null)
			return null;
		else
			return ply.getDesDb();
	}

	
	public long getBaseStatTime() {
		return baseStatTime;
	}

	
	public void setBaseStatTime(
		long baseStatTime)
	{
		this.baseStatTime = baseStatTime;
	}

	
	public void clearBucketStat() {
		for (final Policy ply : policyMap.values()) {
			for (final BucketInfo bucket : ply.getBuckets()) {
				bucket.clear();
			}
		}
	}

	
	synchronized public int getMigStatus() {
		return masterStatus.getMigStatus();
	}

	
	synchronized public void setMigStatus(
		int migStatus)
	{
		masterStatus.setMigStatus(migStatus);
	}

	
	synchronized public int getMigType() {
		return masterStatus.getMigType();
	}

	
	synchronized public void setMigType(
		int migType)
	{
		masterStatus.setMigType(migType);
	}

	
	public int getMigXAOpt() {
		return masterStatus.getMigXAOpt();
	}

	
	public void setMigXAOpt(
		int migXAOpt)
	{
		masterStatus.setMigXAOpt(migXAOpt);
	}

	
	synchronized public boolean setDBHbTime(
		String dbIP, int dbPort)
	{
		boolean result = false;
		final String dbAddr = dbIP + ":" + dbPort;

		
		synchronized (dbMap) {
			for (final Database db : dbMap.values()) {
				if ((db.getIP().equals(dbIP) && (db.getPort() == dbPort))
						|| db.getURL().contains(dbAddr)) {
					db.setHbTime(System.currentTimeMillis());
					result = true;
				}
			}
		}
		return result;
	}

	
	public void alterAlarmConfig(
		User user, List<AlarmInfo> alarms)
		throws MSException
	{
		final Iterator<AlarmInfo> itr1 = alarms.iterator();
		while (itr1.hasNext()) {
			final AlarmInfo alarm1 = itr1.next();
			final Iterator<AlarmInfo> itr2 = this.alarmList.iterator();
			while (itr2.hasNext()) {
				final AlarmInfo alarm2 = itr2.next();
				if (alarm2.getType() == alarm1.getType()) {
					alarm2.setInSysDB(alarm1.isInSysDB());
					alarm2.setSendMail(alarm1.isSendMail());
					alarm2.setSendSM(alarm1.isSendSM());
					alarm2.setInvokeScript(alarm1.isInvokeScript());
					break;
				}
			}
		}

		if (alarms.size() > 0) {
			try {
				persist.alarmPersist(this.alarmList);
			} catch (final SysDBException se) {
				throw new MSException("�޸ı�������ʱ�־û�ʧ��: " + se.getMessage());
			}
		}

	}

	
	public void clusterPersist(
		Cluster cluster, Policy ply, BucketInfo[] buckets)
		throws SysDBException
	{
		persist.clusterPersist(cluster, ply, buckets);
	}

	
	public void insertTablePersist(
		TableInfo table)
		throws SysDBException
	{
		persist.insertTableInfo(table);
	}

	
	public void deleteTablePersist(
		String tableName)
		throws SysDBException
	{
		persist.deleteTableInfo(tableName);
	}

	


	
	public void insertPolicyPersist(
		Policy ply)
		throws SysDBException
	{
		persist.insertPolicy(ply);
	}

	
	public void deletePolicyPersist(
		Policy ply)
		throws SysDBException
	{
		persist.deletePolicy(ply);
	}

	
	public void insertDBPersist(
		Database db)
		throws SysDBException
	{
		persist.insertDBInfo(db);
	}

	
	public void deleteDBPersist(
		String dbName)
		throws SysDBException
	{
		persist.deleteDBInfo(dbName);
	}

	
	public int getStatTaskMaxID()
		throws SysDBException
	{
		return persist.getStatTaskMaxID();
	}

	
	public void deleteAlarms(
		String time)
		throws SysDBException
	{
		persist.deleteAlarms(time);
	}

	
	public void deleteDBNLoad(
		String time)
		throws SysDBException
	{
		persist.deleteDBNLoad(time);
	}

	
	public void deleteXATran(
		String time)
		throws SysDBException
	{
		persist.deleteXATran(time);
	}

	
	public void deleteBucketLoad(
		String time)
		throws SysDBException
	{
		persist.deleteBucketLoad(time);
	}

	
	public void deleteResourceClose(
		String time)
		throws SysDBException
	{
		persist.deleteResourceClose(time);
	}

	
	public void modifyXABStatus(
		String gid, byte status)
		throws SysDBException
	{
		persist.modifyXABStatus(gid, status);
	}

	
	
	
	
	

	
	public List<String> getPartitionsDump(
		String tableName)
		throws SysDBException
	{
		return this.getAccess().getPartitionsDump(tableName);
	}

	
	public void deletePartitionsDump(
		String tableName, List<String> partitions)
		throws SysDBException
	{
		persist.deletePartitionsDump(tableName, partitions);
	}

	
	public MigTaskInfo getMigTask(
		int id)
		throws SysDBException
	{
		return this.getAccess().getMigTask(id);
	}

	
	public List<MigTaskInfo> getAllMigTasksInDB()
		throws SysDBException
	{
		return persist.getAllMigTasks();
	}

	
	public void removeMigTasksInDB(
		int[] ids)
		throws SysDBException
	{
		persist.removeMigTasks(ids);
	}

	
	public void modifyMigTaskStatus(
		int id, int status)
		throws SysDBException
	{
		persist.modifyMigTaskStatus(id, status);
	}

	
	public void cleanSysdb(
		long alarmTime, long dbnTime, long xaTime)
		throws SysDBException
	{
		persist.cleanSysdb(alarmTime, dbnTime, xaTime);
	}

	public Map<Xid, XABInfo> getXabMap() {
		return xabMap;
	}
	
	
	public HashMap<String, List<List<Object>>> getOptDBList(
		String tableName, List<List<Object>> keyValuesList, SQLType optType, boolean isLock,
		LoadBalanceType lbType, Map<Integer, List<Database>> usedDBMap,
		boolean useTransaction, int delayHint)
		throws SQLException, LoadBalanceException
	{
		
		final TableInfo table = this.getTableInfo(tableName);
		if (table == null)
			throw new SQLException("Table '" + tableName + "' does not exist.");

		
		if ((table.isWriteEnabled() == false)
				&& ((optType != SQLType.SELECT) || isLock))
			throw new SQLException("Write operation on Table '" + tableName
					+ "' is disabled.");

		
		final Policy ply = table.getBalancePolicy();
		if (ply == null)
			throw new SQLException(
					"Cannot found the balance policy for table '" + tableName
							+ "'.");

		HashMap<String, List<List<Object>>> optDbMap = null;

		
		if ((keyValuesList == null) || (keyValuesList.size() == 0)) 
		{
			
			if (optType == SQLType.INSERT)
				throw new SQLException(
						"Value of balance field cannot be null where execute insert operation.");

			
			if ((this.getMigStatus() == MIG_STATUS_MIGRATING)
					&& !ply.isTransactional() && this.isMigPolicy(ply))
				throw new DBSQLException(
						Message.CONFLICT_WITH_MIGRATION,
						"Operation is denied since operation may involve migrating records in MYISAM table.");
			else if ((this.getMigStatus() == MIG_STATUS_MIGRATING)
					&& ((this.getMigType() == MigTaskInfo.MIG_TYPE_OFFLINE) || (this
							.getMigType() == MigTaskInfo.MIG_TYPE_CONOFFLINE))
					&& this.isMigPolicy(ply))
				
				throw new DBSQLException(Message.CONFLICT_WITH_MIGRATION,
						"Operation is denied since operation may involve offline migrating records.");
			else 
			{
				
				if (BucketInfo.isStatEnabled()) {
					ply.addOptCount(optType);
				}

				
				final List<Database> dbList = ply.getDbList();
				optDbMap = new HashMap<String, List<List<Object>>>();
				for (final Database db : dbList) {
					if (db.isSwitchingRep())
						throw new SQLException(
								"Opertion is denied since it is switching master/slave.");
					optDbMap.put(db.getURL(), null);
				}
			}

		} else 
		{
			
			if (table.isView() && (table.getBalanceFieldColumns() == null 
					|| table.getBalanceFieldColumns().size() == 0))
				throw new SQLException(
						"View '"+ table.getName()
								+ "' has no balance field. Cannot specifiy balance field value.");

			
			optDbMap = new HashMap<String, List<List<Object>>>();

			
			final Iterator<List<Object>> itr = keyValuesList.iterator();
			while (itr.hasNext()) {
				int bucketNo;

				
				final List<Object> values = itr.next();
				if (table.getBalanceFieldType() == TableInfo.BALANCE_FIELD_TYPE_LONG) 
				{
					final Long longValue = ((Long) values.get(0)).longValue();
					bucketNo = ply.getBucketByKey(longValue.longValue());
				} else if (table.getBalanceFieldType() == TableInfo.BALANCE_FIELD_TYPE_STRING) 
				{
					final String stringValue = (String) values.get(0);
					bucketNo = ply.getBucketByKey(stringValue);
				} else if (table.getBalanceFieldType() == TableInfo.BALANCE_FIELD_TYPE_LIST) 
				{
					bucketNo = ply.getBucketByKey(values);
				} else
					throw new SQLException("Unsuppoted balance field type " 
							+ table.getBalanceFieldType());

				
				final ArrayList<BucketInfo> buckets = ply.getBuckets();
				if (bucketNo < 0 || bucketNo >= buckets.size())
					throw new SQLException("The bucket " + bucketNo
							+ " in policy '" + ply.getName()
							+ "' does not exist.");
				final BucketInfo bucket = (BucketInfo) buckets.get(bucketNo);
				

				
				if ((this.getMigStatus() == MIG_STATUS_MIGRATING)
						&& !ply.isTransactional()
						&& this.isMigBucket(ply, bucketNo))
					throw new DBSQLException(
							Message.CONFLICT_WITH_MIGRATION,
							"Operation is denied, since operation may involve migrating records in MYISAM table.");
				else if ((this.getMigStatus() == MIG_STATUS_MIGRATING 
				)
						&& ((this.getMigType() == MigTaskInfo.MIG_TYPE_OFFLINE) || (this
								.getMigType() == MigTaskInfo.MIG_TYPE_CONOFFLINE))
						&& this.isMigPolicy(ply))
					
					throw new DBSQLException(Message.CONFLICT_WITH_MIGRATION,
							"Operation is denied, since operation may involve offline migrating records.");
				else if ((this.getMigStatus() == MIG_STATUS_MIGRATING)
						&& (optType == SQLType.INSERT)
						&& this.isMigBucket(ply, bucketNo)) {
					
					final Database db = ply.getDesDb();
					if (db == null)
						throw new SQLException(
								"The destination DB of policy '"
										+ ply.getName()
										+ "' is invalid when migration is going on. Insert operation cannot be done.");

					if (db.isSwitchingRep())
						throw new SQLException(
								"Opertion is denied since it is switching master/slave.");

					List<List<Object>> keysList = optDbMap.get(db.getURL());

					if (keysList == null) 
					{
						keysList = new ArrayList<List<Object>>();
						keysList.add(values);
						optDbMap.put(db.getURL(), keysList);
					} else if (!keysList.contains(values)) {
						keysList.add(values);
					}
					bucket.insertAdd();
				} 
				else 
				{
					if (bucket.getSrcDB().isSwitchingRep())
						throw new SQLException(
								"Opertion is denied since it is switching master/slave.");

					
					final String url = bucket.getSrcDB().getURL();
					List<List<Object>> keysList = optDbMap.get(url);

					if (keysList == null) 
					{
						keysList = new ArrayList<List<Object>>();
						keysList.add(values);
						optDbMap.put(bucket.getSrcDB().getURL(), keysList);
					} else if (!keysList.contains(values)) {
						keysList.add(values);
					}

					
					if (BucketInfo.isStatEnabled()) {
						bucket.addOptCount(optType);
					}

					
					if ((this.getMigStatus() == MIG_STATUS_MIGRATING)
							&& this.isMigBucket(ply, bucketNo)) {
						
						final Database db = ply.getDesDb();
						if (db == null)
							throw new SQLException(
									"The destination DB of policy '"
											+ ply.getName()
											+ "' is invalid when migration is going on. Operation cannot be done.");

						if (db.isSwitchingRep())
							throw new SQLException(
									"Opertion is denied since it is switching master/slave.");

						keysList = optDbMap.get(db.getURL());

						if (keysList == null) 
						{
							keysList = new ArrayList<List<Object>>();
							keysList.add(values);
							optDbMap.put(bucket.getSrcDB().getURL(), keysList);
						} else if (!keysList.contains(values)) {
							keysList.add(values);
						}
					}
				}
			}

			if (optDbMap.size() == 0)
				throw new SQLException(
						"Cannot find any DBN for query of table '" + tableName
								+ "'");

		}

		
		if (usedDBMap == null)
			throw new IllegalArgumentException("usedDBMap����Ϊ��");

		final HashMap<String, List<List<Object>>> loadBalanceMap = this
				.getLoadBalanceList(optDbMap, lbType, usedDBMap,
						useTransaction, delayHint, optType, tableName);

		
		Iterator<String> iter = loadBalanceMap.keySet().iterator();
		while (iter.hasNext()) {
			final String dbUrl = iter.next();
			final Database db = dbMap.get(dbUrl);
			if (db != null) { 
				if (db.isEnabled() == false)
					throw new SQLException(
							"Opertion is denied since it involves disused dbn.");
			}
		}

		
		iter = loadBalanceMap.keySet().iterator();
		while (iter.hasNext()) {
			final String dbUrl = iter.next();
			final Database db = dbMap.get(dbUrl);
			if (db != null) { 
				final int masterId = db.isMaster() ? db.getId() : db
						.getMasterDbId();
				if (!usedDBMap.containsKey(masterId)) {
					
					final ArrayList<Database> newList = new ArrayList<Database>();
					newList.add(db);
					usedDBMap.put(masterId, newList);
				} else {
					
					final List<Database> dbList = usedDBMap.get(masterId);
					if (!dbList.contains(db)) {
						dbList.add(db);
					}
				}
			}
		}

		return loadBalanceMap;
	}

	
	private HashMap<String, List<List<Object>>> getLoadBalanceList(
		HashMap<String, List<List<Object>>> masterDbMap, LoadBalanceType lbType,
		Map<Integer, List<Database>> usedDBMap, boolean useTransaction,
		int delayHint, SQLType optType, String tableName)
		throws LoadBalanceException, SQLException
	{

		
		if (lbType == LoadBalanceType.MASTER_ONLY)
			return masterDbMap;

		
		
		if ((optType != SQLType.SELECT)
				|| !this.getTableInfo(tableName).isLoadBalance()) {
			if (lbType == LoadBalanceType.SLAVE_ONLY)
				throw new LoadBalanceException("�޷��������Ҫ���slave�ڵ�");
			else
				return masterDbMap;
		}

		
		if ((delayHint < this.getDDBConfig().getCheckRepInterval())
				&& (lbType != LoadBalanceType.SLAVE_ONLY))
			return masterDbMap;

		
		final int validDelay = delayHint;

		
		final HashMap<String, List<List<Object>>> loadBalanceMap = new HashMap<String, List<List<Object>>>();
		final Iterator<Entry<String, List<List<Object>>>> iter = masterDbMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			final Entry<String, List<List<Object>>> mapEntry = iter.next();
			final String masterDbUrl = mapEntry.getKey();
			final Database masterDb = dbMap.get(masterDbUrl);
			if (masterDb == null)
				throw new SQLException(
						"Opertion is denied since it involves disused dbn.");

			final List<Database> usedDbs = usedDBMap.get(masterDb.getId());
			if (usedDbs == null) {
				
				loadBalanceMap.put(masterDb.getLBNode(lbType, validDelay,
						tableName).getURL(), mapEntry.getValue());
			} else {
				boolean getOldDb = false;
				final Iterator<Database> dbIter = usedDbs.iterator();
				final ArrayList<Database> deleteList = new ArrayList<Database>();
				while (dbIter.hasNext()) {
					final Database usedDb = dbIter.next();

					if (dbMap.get(usedDb.getURL()) == null) {
						
						deleteList.add(usedDb);
						continue;
					}

					
					if ((usedDb.isMaster()
							&& (lbType != LoadBalanceType.SLAVE_ONLY) && (lbType != LoadBalanceType.SLAVE_PREFER))
							|| ((!usedDb.isMaster()
									&& (lbType != LoadBalanceType.MASTER_ONLY)
									&& (usedDb.getRepDelay() < validDelay)
									&& ((usedDb.getRepDelay() != Database.STATUS_REP_FAIL) || (validDelay == Integer.MAX_VALUE)) && !usedDb
									.getUnRepTables().contains(tableName)) && usedDb
									.isEnabled())) {
						loadBalanceMap
								.put(usedDb.getURL(), mapEntry.getValue());
						getOldDb = true;
						break;
					}
				}

				for (final Database db : deleteList) {
					usedDbs.remove(db);
				}

				
				if (!getOldDb) {
					loadBalanceMap.put(masterDb.getLBNode(lbType, validDelay,
							tableName).getURL(), mapEntry.getValue());
				}
			}
		}
		return loadBalanceMap;
	}

	
	public String[] getRandomDb() {
		final String[] dbUrls = new String[1];
		dbUrls[0] = "";
		if ((dbMap == null) || (dbMap.size() == 0))
			return dbUrls;

		final Collection<Database> cl = dbMap.values();
		Iterator<Database> itr = cl.iterator();
		while (itr.hasNext()) {
			final Database db = itr.next();
			if (db.isMaster() && db.inNormalStatus()) {
				dbUrls[0] = db.getURL();
				return dbUrls;
			}
		}
		itr = dbMap.values().iterator();
		if (itr.hasNext()) {
			dbUrls[0] = itr.next().getURL();
		}
		return dbUrls;
	}

	
	public void checkup()
		throws MSException
	{
	}

	
	@Override
	synchronized public Object clone() {
		try {
			final Cluster cloned = (Cluster) super.clone();
			
			
			
			
			
			
			
			
			
			return cloned;
		} catch (final CloneNotSupportedException e) {
			return null;
		}
	}

	
	synchronized public Cluster copy() {
		try {
			final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			final ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(this);
			final ByteArrayInputStream byteIn = new ByteArrayInputStream(
					byteOut.toByteArray());
			final ObjectInputStream in = new ObjectInputStream(byteIn);
			return (Cluster) in.readObject();
		} catch (final Exception e) {
			return null;
		}
	}

	public Key getKey() {
		return key;
	}

	public void setKey(
		Key key)
	{
		this.key = key;
	}

	public String getSysUsername() {
		return sysUserName;
	}

	public void setSysUsername(
		String username)
	{
		this.sysUserName = username;
	}

	public List<MigTaskInfo> getMigTaskList() {
		return migTaskList;
	}

	public void setMigTaskList(
		List<MigTaskInfo> migTaskList)
	{
		this.migTaskList = migTaskList;
	}

	
	private boolean isMigPolicy(
		Policy ply)
	{
		if (ply == null)
			return false;

		if ((this.getMigType() == MigTaskInfo.MIG_TYPE_OFFLINE)
				|| (this.getMigType() == MigTaskInfo.MIG_TYPE_ONLINE))
			return ply.equals(this.getMigPolicy());
		else if (this.getMigType() == MigTaskInfo.MIG_TYPE_CONOFFLINE) {
			if ((this.getMigTaskList() == null)
					|| (this.getMigTaskList().size() == 0))
				return false;
			for (final MigTaskInfo task : this.getMigTaskList()) {
				if (ply.equals(this.getPolicy(task.getPolicy())))
					return true;
			}
			return false;
		} else
			return false;

	}

	
	private boolean isMigBucket(
		Policy ply, int bucketNo)
	{
		if (ply == null)
			return false;

		if ((this.getMigType() == MigTaskInfo.MIG_TYPE_OFFLINE)
				|| (this.getMigType() == MigTaskInfo.MIG_TYPE_ONLINE))
			return ply.equals(this.getMigPolicy()) && ply.isMig(bucketNo);
		else if (this.getMigType() == MigTaskInfo.MIG_TYPE_CONOFFLINE) {
			if ((this.getMigTaskList() == null)
					|| (this.getMigTaskList().size() == 0))
				return false;
			for (final MigTaskInfo task : this.getMigTaskList()) {
				if (ply.equals(this.getPolicy(task.getPolicy()))
						&& task.isInTask(bucketNo))
					return true;
			}
			return false;
		} else
			return false;
	}

	
	public void getLoginLock() {
		this.loginLock.getWriteLock();
	}

	
	public void releaseLoginLock() {
		this.loginLock.releaseLock();
	}

	
	public ColumnInfo getColumnInfo(
		String tableName, String columnName)
	{
		final TableInfo table = this.getTableInfo(tableName);
		if (table == null)
			return null;
		return table.getColumnInfo(columnName);
	}

	
	public ArrayList<TableInfo> getTablesOnDbn(
		Database db)
	{
		final ArrayList<TableInfo> tables = new ArrayList<TableInfo>();
		for (final Policy ply : this.policyMap.values()) {
			for (final TableInfo table : ply.getTableList())
				if (!tables.contains(table)) {
					tables.add(table);
				}
		}

		return tables;
	}

	
	public void clearBucketStats() {
		for (final Policy ply : this.policyMap.values()) {
			for (final BucketInfo bucket : ply.getBuckets()) {
				bucket.clear();
			}
		}
	}

	
	public IndexInfo getIndexInfo(
		String tableName, String indexName)
	{
		final TableInfo table = this.getTableInfo(tableName);
		if (table == null)
			return null;
		return table.getIndexInfo(indexName);
	}

	
	public StatTask getStatTask(
		int id)
	{
		for (final StatTask task : statTaskList) {
			if (task.getId() == id)
				return task;
		}
		return null;
	}

	
	public boolean hasEqualMigTasks(
		List<MigTaskInfo> desMigList)
	{
		if (migTaskList == desMigList)
			return true;
		if ((migTaskList == null) && (desMigList == null))
			return true;

		if ((migTaskList != null) && (desMigList != null)) {
			for (final MigTaskInfo task : migTaskList) {
				if (!desMigList.contains(task))
					return false;
			}

			for (final MigTaskInfo task : desMigList) {
				if (!migTaskList.contains(task))
					return false;
			}

			return true;
		}

		return false;
	}

	public List<String> getQsIpList() {
		return qsIpList;
	}

	public void setQsIpList(
		List<String> qsIpList)
	{
		this.qsIpList = qsIpList;
	}

	public void writeDBUserOp(
		DBUserOp op)
		throws IOException
	{
		DBUserOp.writeOp(op, Definition.DBUSER_ERR_FILE);
		this.dbUserOp = op;
	}

	public DBUserOp getDBUserOp() {
		return this.dbUserOp;
	}

	public DBIConfig getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(
		DBIConfig clientConf)
	{
		this.clientConfig = clientConf;
	}

	public List<StatTask> getStatTaskList() {
		return statTaskList;
	}

	
	public void alterTableConfig(
		SAlterTable alterTable)
		throws Exception
	{
		final TableInfo table = this.getTableInfo(alterTable.getTableName());
		if (table == null)
			throw new Exception("Table" + alterTable.getTableName()
					+ " doesn't exist.");

		if (table.isView())
			throw new Exception("Cannot alter table on View '"
					+ table.getName() + "'.");
		if (alterTable.getOps() != null) {
			int lastFieldNumber = table.getLastFieldNumber();
			for (final SAlterTableOp op : alterTable.getOps()) {
				if (op instanceof SAlterTableRename) {
					final SAlterTableRename tableRename = (SAlterTableRename) op;
					final String oldTableName = table.getName();
					table.setName(tableRename.getNewTableName());
					this.tableMap.remove(oldTableName);
					this.tableMap.put(table.getName(), table);
					for (final IndexInfo index : table.getIndexMap().values()) {
						index.setTableName(table.getName());
					}
					
					if (null != table.getModelName()) {
						final Model mdl = this.modelMap.get(table
								.getModelName());
						if (null != mdl) {
							mdl.removeTable(oldTableName);
							mdl.addTable(tableRename.getNewTableName());
						}
					}
				} else if (op instanceof SAlterTableComment) {
					
					final SAlterTableComment tableComment = (SAlterTableComment) op;
					String newComment = tableComment.getNewTableComment();
					if ("".equals(newComment)) {
						newComment = null;
					}
					table.setComment(newComment);
				} else if (op instanceof SAlterTableCommentColumn) { 
					final SAlterTableCommentColumn clmComment = (SAlterTableCommentColumn) op;
					String newComment = clmComment.getComment();
					if ("".equals(newComment)) {
						newComment = null;
					}
					table.getColumnInfo(clmComment.getColumnName()).setComment(
							newComment);
				} else if (op instanceof SAlterTableAddColumn) {
					lastFieldNumber++;
					final SAlterTableAddColumn addColumnOp = (SAlterTableAddColumn) op;
					table.addColumn(addColumnOp.getColumn(), addColumnOp
							.isSpecifiedPosition(), addColumnOp
							.getPreviousColumnName());
				} else if (op instanceof SAlterTableModifyColumn) {
					final SAlterTableModifyColumn modifyColumnOp = (SAlterTableModifyColumn) op;
					table.modifyColumn(modifyColumnOp.getColumn(), modifyColumnOp
							.isSpecifiedPosition(), modifyColumnOp
							.getPreviousColumnName());
				} else if (op instanceof SAlterTableChangeColumn) {
					final SAlterTableChangeColumn changeColumnOp = (SAlterTableChangeColumn) op;
					table.changeColumn(changeColumnOp.getNewColumn(), changeColumnOp.getOldColumnName(), 
							changeColumnOp.isSpecifiedPosition(), changeColumnOp.getPreviousColumnName());
					
					
					if (changeColumnOp.isBalanceField()) {
						List<String> originalNames = table.getBalanceFields();
						List<String> newNames = StringUtils.replaceElememtIgnoreCase(originalNames,
										changeColumnOp.getOldColumnName(),
										changeColumnOp.getNewColumn().getName());
						table.setBalanceFields(newNames);
					}
					
					for (final IndexInfo index : table.getIndexMap().values()) {
						for (final IndexColumn indexColumn : index
								.getColumnList())
							if (indexColumn.getColumnName().equalsIgnoreCase(
									changeColumnOp.getOldColumnName())) {
								indexColumn.rename(changeColumnOp
										.getNewColumn().getName());
							}
					}
				} else if (op instanceof SAlterTableRenameColumn) { 
					final SAlterTableRenameColumn renameColumn = (SAlterTableRenameColumn) op;
					final ColumnInfo column = table.getColumnInfo(renameColumn
							.getOldName());
					column.rename(renameColumn.getNewName());
					if (renameColumn.isBalanceField()) {
						
						List<String> originalNames = table.getBalanceFields();
						List<String> newNames = StringUtils
								.replaceElememtIgnoreCase(originalNames,
										renameColumn.getOldName(),
										renameColumn.getNewName());
						table.setBalanceFields(newNames);
					}
					
					for (final IndexInfo index : table.getIndexMap().values()) {
						for (final IndexColumn indexColumn : index
								.getColumnList())
							if (indexColumn.getColumnName().equalsIgnoreCase(
									renameColumn.getOldName())) {
								indexColumn.rename(renameColumn.getNewName());
							}
					}
				} else if (op instanceof SAlterTableAddIndex) {
					final SAlterTableAddIndex addIndex = (SAlterTableAddIndex) op;
					table.addIndex(addIndex.getIndex(), true);
					
					if (addIndex.getIndex().isUnique()) {
						table.checkUniqueIndex();
					}
				} else if (op instanceof SAlterTableDropIndex) {
					final SAlterTableDropIndex dropIndex = (SAlterTableDropIndex) op;
					final IndexInfo rmIndex = table.getIndexInfo(dropIndex
							.getIndexName());
					table.removeIndex(dropIndex.getIndexName());
					
					if ((rmIndex != null) && rmIndex.isUnique()) {
						table.checkUniqueIndex();
					}

				} else if (op instanceof SAlterTableDropColumn) {
					final SAlterTableDropColumn dropColumnOp = (SAlterTableDropColumn) op;
					final String column = dropColumnOp.getColumnName();
					int i = 0;
					for (final ColumnInfo c : table.getColumns()) {
						if (c.getName().equalsIgnoreCase(column)) {
							table.getColumns().remove(i);
							break;
						}
						i++;
					}
				} else
					throw new Exception("Operation is not supportted.");
			}
			table.setLastFieldNumber(lastFieldNumber);
		}

		if (alterTable.isSetDupKeyChk()) {
			table.setDupkeyChk(alterTable.isDupKeyChk());
		}

		if (table.isWriteEnabled() == false) {
			table.setWriteEnabled(true);
		}
	}

	public List<Database> getSlaveList() {
		final List<Database> slaveList = new ArrayList<Database>();
		synchronized (dbMap) {
			for (final Database db : dbMap.values()) {
				if (!db.isMaster()) {
					slaveList.add(db);
				}
			}
		}
		return slaveList;
	}

	public List<TableMigResult> getMigResults(
		int taskId)
		throws SysDBException
	{
		return this.getAccess().getTableMigResult(taskId);
	}

	public HashMap<String, Long> getDelStartIds(
		String policyName)
		throws SysDBException
	{
		return this.getAccess().getDelStartIds(policyName);
	}

	public OnlineMigTaskInfo getOnlineMigTask(
		long taskId)
		throws SysDBException
	{
		return this.getAccess().getOnlineMigTask(taskId);
	}

	public List<OnlineMigTaskInfo> getOnlineMigTasks()
		throws SysDBException
	{
		return this.getAccess().getOnlineMigTasks();
	}

	
	public long insertMigTask(
		OnlineMigTaskInfo task)
		throws SysDBException
	{
		return this.getPersist().insertMigTask(task);
	}

	
	public void updateMigTaskStatus(
		long migTaskId, int status)
		throws SysDBException
	{
		this.getPersist().updateMigTaskStatus(migTaskId, status);
	}

	
	public void updateMigStepStatus(
		long migTaskId, int stepStatus)
		throws SysDBException
	{
		this.getPersist().updateMigStepStatus(migTaskId, this.getStatus());
	}

	
	public long insertOnlineAlterTask(
		OnlineAlterTaskInfo task)
		throws SysDBException
	{
		return this.getPersist().insertOnlineAlterTask(task);
	}

	
	public List<OnlineAlterTaskInfo> getOnlineAlterTasks()
		throws SysDBException
	{
		return this.getAccess().getOnlineAlterTableTasks();
	}

	
	public OnlineAlterTaskInfo getOnlineAlterTableTask(
		long taskId)
		throws SysDBException
	{
		return this.getAccess().getOnlineAlterTableTask(taskId);
	}

	
	public void updateOnlineAlterStatus(
		long taskid, int status)
		throws SysDBException
	{
		this.getPersist().updateOnlineAlterStatus(taskid, status);
	}

	
	public void deleteOnlineAlterTask(
		long taskid)
		throws SysDBException
	{
		this.getPersist().deleteOnlineAlterTask(taskid);
	}

	
	public void updateOnlineAlterDbStatus(
		long taskid, String url, int status)
		throws SysDBException
	{
		this.getPersist().updateOnlineAlterDbStatus(taskid, url, status);
	}

	
	public void updateOnlineAlterParameter(
		long taskid, long chunksize, long sleeptime)
		throws SysDBException
	{
		this.getPersist().updateOnlineAlterParameter(taskid, chunksize,
				sleeptime);
	}

	
	public DataValidateTask getDataValidateTask(
		long oatID)
		throws SysDBException
	{
		return this.getAccess().getDataValidateTask(oatID);
	}

	
	public void updateDataValidateTask(
		DataValidateTask task)
		throws SysDBException
	{
		this.getPersist().updateDataValidateTask(task);
	}

	
	public void updateDataValidateSleepTime(
		long taskId, int sleepTime)
		throws SysDBException
	{
		this.getPersist().updateDataValidateSleepTime(taskId, sleepTime);
	}

	
	public void updateDataValidateTaskStatus(
		long taskId, int status)
		throws SysDBException
	{
		this.getPersist().updateDataValidateTaskStatus(taskId, status);
	}

	
	public void updateDataValidateDb(
		long taskId, DataValidateDb db)
		throws SysDBException
	{
		this.getPersist().updateDataValidateDb(taskId, db);
	}

	
	public Map<Long, Integer> getAllDataValidateTasksStatus()
		throws SysDBException
	{
		return this.getAccess().getAllDataValidateTasksStatus();
	}

	public Map<String, Trigger> getTriggerMap() {
		return triggerMap;
	}

	public void setTriggerMap(
		Map<String, Trigger> triggerMap)
	{
		this.triggerMap = triggerMap;
	}

	public Trigger getTrigger(
		String name)
	{
		return this.triggerMap.get(name);
	}

	public Map<String, Routine> getRoutineMap() {
		return routineMap;
	}

	public void setRoutineMap(
		Map<String, Routine> routineMap)
	{
		this.routineMap = routineMap;
	}

	public Routine getRoutine(
		String name)
	{
		return routineMap.get(name);
	}

	
	public void deleteMigTask(
		long taskId)
		throws SysDBException
	{
		this.getPersist().deleteMigTask(taskId);
	}

	public void setDbMap(
		Map<String, Database> dbMap)
	{
		this.dbMap = dbMap;
	}

	public void setPolicyMap(
		Map<String, Policy> policyMap)
	{
		this.policyMap = policyMap;
	}

	public void setHashFunctionMap(
		Map<String, HashFunction> hashFunctionMap)
	{
		this.hashFunctionMap = hashFunctionMap;
	}

	public void setTableMap(
		Map<String, TableInfo> tableMap)
	{
		this.tableMap = tableMap;
	}

	public void setUserMap(
		Map<String, User> userMap)
	{
		this.userMap = userMap;
	}

	public List<Database> getDbInfoByType(
		DbnType type)
	{
		final List<Database> dbList = new ArrayList<Database>();
		for (final Entry<String, Database> entry : dbMap.entrySet()) {
			final Database db = entry.getValue();
			if (db.getDbnType().equals(type)) {
				dbList.add(db);
			}
		}
		return dbList;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public void compressPolicyBuckets() {
		for (final Policy policy : this.policyMap.values()) {
			policy.compressBucketList();
		}
	}

	
	public void updateMigSourceLog(
		long migTaskId, String url, String newlog, String newpos)
		throws SysDBException
	{
		this.getPersist().updateMigSourceLog(migTaskId, url, newlog, newpos);
	}

	
	public OnlineMigStartId getOnlineMigStartId(
		long taskId)
		throws SysDBException
	{
		return this.getAccess().getOnlineMigStartId(taskId);
	}

	
	public void saveOnlineMigStartId(
		OnlineMigStartId migStartId)
		throws SysDBException
	{
		this.getPersist().saveOnlineMigStartId(migStartId);
	}

}
