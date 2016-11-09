package com.netease.backend.db.common.management.dbi;

public interface IDBITablesPIDManager {
	
	PIDManager addPIDManager(
		String name, int type);

	
	PIDManager getPIDManager(
		String tableName);

	
	PIDManager getPIDManager(
		String tableName, boolean getDefaultOnNonExist);

	
	boolean removePIDManager(
		String name);

}
