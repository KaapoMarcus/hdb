package com.netease.backend.db.common.management;


public interface DDBStatus {

	
	public static final int	STATUS_NORMAL			= 1;
	
	public static final int	STATUS_STOPPING			= 2;
	
	public static final int	STATUS_STOP				= 3;
	
	public static final int	STATUS_MIGRATION		= 4;
	
	public static final int	STATUS_BACKUP			= 5;
	
	public static final int	STATUS_DUMP				= 6;
	
	public static final int	STATUS_CONFIG			= 7;
	
	public static final int	STATUS_UPDATE			= 8;
	
	public static final int	STATUS_MIGRATION_FAILED	= 10;
}
