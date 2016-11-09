package com.netease.backend.db.common.management;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.xa.Xid;

import com.netease.backend.db.common.exceptions.SysDBException;
import com.netease.backend.db.common.management.dbi.DBIConfig;
import com.netease.backend.db.common.management.dbi.DBISignature;
import com.netease.backend.db.common.schema.AlarmInfo;
import com.netease.backend.db.common.schema.BucketInfo;
import com.netease.backend.db.common.schema.DataValidateDb;
import com.netease.backend.db.common.schema.DataValidateTask;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnCluster;
import com.netease.backend.db.common.schema.EntityPrivilege;
import com.netease.backend.db.common.schema.HashFunction;
import com.netease.backend.db.common.schema.MigTaskInfo;
import com.netease.backend.db.common.schema.OnlineAlterTaskInfo;
import com.netease.backend.db.common.schema.OnlineMigStartId;
import com.netease.backend.db.common.schema.OnlineMigTaskInfo;
import com.netease.backend.db.common.schema.Policy;
import com.netease.backend.db.common.schema.QueryServerInfo;
import com.netease.backend.db.common.schema.Routine;
import com.netease.backend.db.common.schema.TableInfo;
import com.netease.backend.db.common.schema.TableMigResult;
import com.netease.backend.db.common.schema.Trigger;
import com.netease.backend.db.common.schema.User;
import com.netease.backend.db.common.sql.SAlterTable;
import com.netease.backend.db.common.sql.SAlterTableAddColumn;
import com.netease.backend.db.common.sql.SSlowlogHistory;
import com.netease.backend.db.common.sql.plan.SPlan;
import com.netease.backend.db.common.stat.BucketStat;
import com.netease.backend.db.common.stat.ColumnStat;
import com.netease.backend.db.common.stat.IndexStat;
import com.netease.backend.db.common.stat.Stat;
import com.netease.backend.db.common.stat.StatTask;
import com.netease.backend.db.common.stat.StmtStat;
import com.netease.backend.db.common.stat.TableMemcachedStat;
import com.netease.backend.db.common.stat.TableStat;
import com.netease.backend.db.common.utils.ObjectTable;


public interface ClusterPersist {

	
	void insertDBI(
		int id, DBISignature sig)
		throws SysDBException;

	
	void deleteDBIs(
		int... idArray)
		throws SysDBException;

	
	void deleteDBI(
		int id)
		throws SysDBException;

	
	void deleteDBInfo(
		String dbUrl)
		throws SysDBException;

	
	void deletePolicy(
		Policy ply)
		throws SysDBException;

	
	void deleteTableInfo(
		String tableName)
		throws SysDBException;

	
	void deleteColumns(
		TableInfo table, String... columns)
		throws SysDBException;

	


	
	void insertDBInfo(
		Database info)
		throws SysDBException;

	void updateDBInfo(
		Database info)
		throws SysDBException;

	
	void updateDBInfo(
		String dbUrl, Database db)
		throws SysDBException;

	
	void insertPolicy(
		Policy policy)
		throws SysDBException;

	
	void insertTableInfo(
		TableInfo table)
		throws SysDBException;

	
	void setTableWriteEnable(
		List<String> tableNames, boolean isWriteEnable)
		throws SysDBException;

	
	void updateTableInfo(
		TableInfo table, boolean columnChanged, boolean indexChanged)
		throws SysDBException;

	void clusterPersist(
		Cluster cluster, Policy ply, BucketInfo[] buckets)
		throws SysDBException;

	
	void alarmPersist(
		List<AlarmInfo> alarmList)
		throws SysDBException;

	
	void alarmSwitchPersist(
		boolean alarmSwitch)
		throws SysDBException;

	
	void clientConfigPersist(
		DBIConfig config)
		throws SysDBException;

	
	void ddbConfigPersist(
		Cluster cluster, DDBConfig config)
		throws SysDBException;

	
	void deletePartitionsDump(
		String tableName, List<String> partitions)
		throws SysDBException;

	
	void addPlan(
		SPlan plan)
		throws SysDBException;

	
	void deletePlan(
		String planName)
		throws SysDBException;

	
	void modifyPlan(
		SPlan plan)
		throws SysDBException;

	
	void removeMigTasks(
		int[] ids)
		throws SysDBException;

	
	void modifyMigTaskStatus(
		int id, int status)
		throws SysDBException;

	
	void cleanSysdb(
		long alarmTime, long dbnTime, long xaTime)
		throws SysDBException;

	
	void deleteAlarms(
		String time)
		throws SysDBException;

	
	void deleteDBNLoad(
		String time)
		throws SysDBException;

	
	void deleteXATran(
		String time)
		throws SysDBException;

	
	void deleteBucketLoad(
		String time)
		throws SysDBException;

	
	void deleteResourceClose(
		String time)
		throws SysDBException;

	
	void modifyXABStatus(
		String gid, byte status)
		throws SysDBException;

	
	void insertUser(
		User user)
		throws SysDBException;

	
	void updateUser(
		User user)
		throws SysDBException;

	
	void delUser(
		String username)
		throws SysDBException;

	
	
	
	
	
	
	
	
	
	
	void changeUserType(
		String username, int type)
		throws SysDBException;

	
	void changeUserDesc(
		String username, String desc)
		throws SysDBException;

	
	void changeUserAdminHost(
		String username, Collection<String> adminIps)
		throws SysDBException;

	
	void changeUserHost(
		String username, Collection<String> clientIps, Collection<String> qsIps)
		throws SysDBException;

	
	void changeUserExpireTime(
		String username, Date expire)
		throws SysDBException;

	
	void changeUserPass(
		String username, String password)
		throws SysDBException;

	
	void changeUserQuota(
		String username, long quota, long slaveQuota)
		throws SysDBException;

	
	void changeUserGrant(
		String username, Set<EntityPrivilege> newEntityPrivileges,
		Set<EntityPrivilege> purgedEntityPrivileges)
		throws SysDBException;

	List<MigTaskInfo> getAllMigTasks()
		throws SysDBException;

	
	void updateMigTask(
		MigTaskInfo task)
		throws SysDBException;

	
	void updateAssignedMigTaskID(
		int id)
		throws SysDBException;

	
	void addBucketNoByPly(
		String policyName)
		throws SysDBException;

	


	
	void updateCardinalityStat(
		Cluster cluster)
		throws SysDBException;

	
	void recordOpStats(
		long clientId, int taskId, int resultId, List<Stat> opStats)
		throws SysDBException;

	
	void recordDdbStmtStats(
		long clientId, int taskId, int resultId, List<StmtStat> ddbStats)
		throws SysDBException;

	
	void recordMysqlStats(
		long clientId, int taskId, int resultId, List<StmtStat> mysqlStats)
		throws SysDBException;

	
	void recordBucketStats(
		long clientId, int taskId, int resultId, List<BucketStat> bucketStatList)
		throws SysDBException;

	
	void recordColumnStats(
		long clientId, int taskId, int resultId, List<ColumnStat> columnStatList)
		throws SysDBException;

	
	void recordIndexStats(
		long clientId, int taskId, int resultId, List<IndexStat> indexStatList)
		throws SysDBException;
	
	
	void recordTableMemcachedStats(int clientId, int taskId, int resultId,
			List<TableMemcachedStat> tableMemcachedStatList)
			throws SysDBException;

	
	void insertStatTask(
		StatTask task)
		throws SysDBException;

	
	void deleteStatTask(
		int taskId)
		throws SysDBException;

	
	void deleteStatResult(
		int taskId, int[] resultIds)
		throws SysDBException;

	
	void updateStatResultDesc(
		int taskId, int resultId, String desc)
		throws SysDBException;

	
	void updateStatTask(
		StatTask task)
		throws SysDBException;

	
	int getStatTaskMaxID()
		throws SysDBException;

	
	boolean addBXARecords(
		long clientId, String dbUrl, List<Xid> commitXidList,
		List<Xid> rollbackXidList)
		throws SysDBException;

	
	void insertResourceClose(
		String ip, long asId, long id, long parentId, String resourceType,
		int closeType, long cTime, long aTime, long rTime, long optCount,
		String opts, String extra)
		throws SysDBException;

	
	void recordTableStat(
		List<TableStat> tableStatList)
		throws SysDBException;

	void alterTable(
		TableInfo table, SAlterTable alterTable)
		throws SysDBException;

	
	void changeUserRole(
		String username, int[] roleRights)
		throws SysDBException;

	
	void switchRep(
		Database masterDb, Database slaveDb)
		throws SysDBException;

	
	void updateTaskMigResults(
		MigTaskInfo task)
		throws SysDBException;

	
	void updateTaskMigResult(
		int taskId, TableMigResult migResult)
		throws SysDBException;

	
	void insertTrigger(
		Trigger trigger)
		throws SysDBException;

	
	void delTrigger(
		String triggerName)
		throws SysDBException;

	
	void updateTrigger(
		Trigger trigger)
		throws SysDBException;

	
	long insertMigTask(
		OnlineMigTaskInfo task)
		throws SysDBException;

	
	void insertRoutine(
		Routine routine)
		throws SysDBException;

	
	void delRoutine(
		String routineName)
		throws SysDBException;

	
	void updateRoutine(
		Routine routine)
		throws SysDBException;

	
	void updateMigTaskStatus(
		long migTaskId, int status)
		throws SysDBException;

	
	void updateMigTaskList(
		Cluster cluster, List<MigTaskInfo> migTaskList, boolean updateBucket)
		throws SysDBException;

	
	void updateDbnDirty(
		String url, int isDirty, Collection<String> dirtyPlys)
		throws SysDBException;

	
	void refreshStartId()
		throws SysDBException;

	
	void updateTablePly(
		String tableName, Policy ply)
		throws SysDBException;

	
	void updateTableModel(
		String tableName, String mdlName)
		throws SysDBException;

	
	void updatePolicyComment(
		String policyName, String newComment)
		throws SysDBException;

	
	void updateColumnComment(
		String tableName, String columnName, String newComment)
		throws SysDBException;

	
	void deleteMigTask(
		long taskId)
		throws SysDBException;

	
	void updateMigStepStatus(
		long migTaskId, int stepStatus)
		throws SysDBException;

	
	long insertOnlineAlterTask(
		OnlineAlterTaskInfo task)
		throws SysDBException;

	
	void updateOnlineAlterStatus(
		long taskid, int status)
		throws SysDBException;

	
	void deleteOnlineAlterTask(
		long taskid)
		throws SysDBException;

	
	void updateOnlineAlterDbStatus(
		long taskid, String url, int status)
		throws SysDBException;

	
	void updateOnlineAlterParameter(
		long taskid, long chunksize, long sleeptime)
		throws SysDBException;

	
	void storeSlowlogInfo(
		String inputStr, String nodeName, Calendar taskTime)
		throws SysDBException;

	
	ObjectTable readSlowlogInfo(
		SSlowlogHistory sql)
		throws SysDBException;

	
	void insertDbnCluster(
		DbnCluster dbnCluster)
		throws SysDBException;

	
	void insertDbnClusterIndex(
		String indexName, String tableSpace, boolean isReverse,
		String clusterName)
		throws SysDBException;

	
	void alterDbnClusterSize(
		String name, int size)
		throws SysDBException;

	
	void dropDbnClusterIndex(
		String clusterName)
		throws SysDBException;

	
	void dropDbnCluster(
		String clusterName)
		throws SysDBException;

	
	void dropIndex(
		String tableName, String indexName)
		throws SysDBException;

	
	void updateMigSourceLog(
		long migTaskId, String url, String newlog, String newpos)
		throws SysDBException;

	
	void insertHashFunction(
		HashFunction hashFunction)
		throws SysDBException;

	
	void updateHashFunction(
		HashFunction hashFunction)
		throws SysDBException;

	
	void deleteHashFunction(
		String name)
		throws SysDBException;

	
	void insertDataValidateTask(
		DataValidateTask task)
		throws SysDBException;

	
	void updateDataValidateTask(
		DataValidateTask task)
		throws SysDBException;

	
	void updateDataValidateSleepTime(
		long taskId, int sleepTime)
		throws SysDBException;

	
	void updateDataValidateTaskStatus(
		long taskId, int status)
		throws SysDBException;

	
	void updateDataValidateDb(
		long taskId, DataValidateDb db)
		throws SysDBException;

	
	void deleteDataValidateTask(
		long taskId)
		throws SysDBException;

	
	void updateZkAddr(
		String addr)
		throws SysDBException;

	
	void saveOnlineMigStartId(
		OnlineMigStartId migStartId)
		throws SysDBException;
	
	
	void insertQSInfo(QueryServerInfo qsInfo) throws SysDBException;

	
	void deleteQSInfo(QueryServerInfo qsInfo) throws SysDBException;
	
    
	public void insertAutoSwitchRecord(String url, long time,
			String binlogFile, long binlogPos) throws SysDBException;
	
	
	void updateTableMemcachedVersion(String tableName, String newVersion)
			throws SysDBException;

	
	void changeTableMemcachedConfig(String tableName, boolean useMemcached,
			String memcachedKeyIndex) throws SysDBException;
	
	
	public List<String> getColumnDefaultValue(
			List<SAlterTableAddColumn> addColumnOpList) throws SysDBException;
}