package com.netease.backend.db.common.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netease.backend.db.common.exceptions.SysDBException;
import com.netease.backend.db.common.management.dbi.DBIConfig;
import com.netease.backend.db.common.management.dbi.DBISignature;
import com.netease.backend.db.common.schema.AlarmInfo;
import com.netease.backend.db.common.schema.DataValidateTask;
import com.netease.backend.db.common.schema.Database;
import com.netease.backend.db.common.schema.DbnCluster;
import com.netease.backend.db.common.schema.HashFunction;
import com.netease.backend.db.common.schema.IndexInfo;
import com.netease.backend.db.common.schema.MigTaskInfo;
import com.netease.backend.db.common.schema.Model;
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
import com.netease.backend.db.common.sql.plan.SPlan;
import com.netease.backend.db.common.stat.StatTask;
import com.netease.backend.db.common.stat.TableStat;


public interface ClusterAccess {

	
	DBIConfig getClientConfig()
		throws SysDBException;

	
	DDBConfig getDDBConfig()
		throws SysDBException;

	
	List<AlarmInfo> getAlarms()
		throws SysDBException;

	
	Map<String, Database> getDatabases()
		throws SysDBException;

	
	Map<String, Policy> getPolicies()
		throws SysDBException;

	
	Map<String, Policy> getPolicies(
		Map<String, Database> databases)
		throws SysDBException;

	
	Policy getPolicy(
		String policyName)
		throws SysDBException;

	
	Map<String, HashFunction> getHashFunctions()
		throws SysDBException;

	
	Map<String, TableInfo> getTables()
		throws SysDBException;

	Map<String, TableInfo> getTables(
		Map<String, Policy> policies)
		throws SysDBException;

	TableStat getTableStats(
		String tableName)
		throws SysDBException;

	
	Map<String, DbnCluster> getDbnClusters()
		throws SysDBException;

	Map<String, DbnCluster> getDbnClusters(
		Map<String, Policy> policies, Map<String, IndexInfo> indexes)
		throws SysDBException;

	DbnCluster getDbnCluster(
		String name)
		throws SysDBException;

	
	Map<String, Model> getModels()
		throws SysDBException;

	Map<String, Model> getModels(
		Map<String, TableInfo> tables)
		throws SysDBException;

	Map<String, IndexInfo> getClusterIndexes(
		String clusterName)
		throws SysDBException;

	Map<String, IndexInfo> getIndexesOfTable(
		String tableName)
		throws SysDBException;

	MasterStatus getMasterStatus()
		throws SysDBException;

	
	List<String> getPartitionsDump(
		String tableName)
		throws SysDBException;

	
	List<SPlan> getPlans(
		Cluster cluster)
		throws SysDBException;

	
	MigTaskInfo getMigTask(
		int id)
		throws SysDBException;

	
	List<StatTask> getStatTasks()
		throws SysDBException;

	List<TableMigResult> getTableMigResult(
		int taskId)
		throws SysDBException;

	
	ArrayList<MigTaskInfo> getMigTasks()
		throws SysDBException;

	Map<String, User> getUsers()
		throws SysDBException;

	
	void updateUserPrivileges(
		Map<String, User> users)
		throws SysDBException;

	
	HashMap<String, Long> getDelStartIds(
		String policyName)
		throws SysDBException;

	
	OnlineMigTaskInfo getOnlineMigTask(
		long taskId)
		throws SysDBException;

	
	List<OnlineMigTaskInfo> getOnlineMigTasks()
		throws SysDBException;

	
	Map<String, Trigger> getTriggers()
		throws SysDBException;

	Map<String, Trigger> getTriggers(
		Map<String, TableInfo> tables, Map<Integer, Database> databases)
		throws SysDBException;

	
	Database getDatabaseById(
		int id)
		throws SysDBException;

	
	Map<String, Routine> getRoutines()
		throws SysDBException;

	Map<String, Routine> getRoutines(
		Map<Integer, Database> databases)
		throws SysDBException;

	
	List<OnlineAlterTaskInfo> getOnlineAlterTableTasks()
		throws SysDBException;

	
	OnlineAlterTaskInfo getOnlineAlterTableTask(
		long taskId)
		throws SysDBException;

	
	
	
	

	
	DataValidateTask getDataValidateTask(
		long oatID)
		throws SysDBException;

	
	Map<Long, Integer> getAllDataValidateTasksStatus()
		throws SysDBException;

	Map<Integer, DBISignature> getDBIMap()
		throws SysDBException;
	
	
	List<QueryServerInfo> getQueryServers() throws SysDBException;

	
	String getZkAddr()
		throws SysDBException;

	
	OnlineMigStartId getOnlineMigStartId(
		long taskId)
		throws SysDBException;

}