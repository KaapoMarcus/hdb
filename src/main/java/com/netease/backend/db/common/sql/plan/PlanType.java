package com.netease.backend.db.common.sql.plan;


public enum PlanType {
	
	SCHEMA_MODIFY,
	
	USER_MANAGEMENT,
	
	DATA_BACKUP,
	
	DATA_EXPORT,
	
	DATA_MIGRATION,
	
	PARTITION,
	
	STATISTICS,
	
	TABLE_STAT_COLLECT,
	
	BUCKETNO_ADD,
	
	SYSDB_CLEAN,
	
	INVALID,
	
	NOTHING,
	
	OPTION,
	
	USE_DBNS,
	
	DML,
	
	OTHER
}
