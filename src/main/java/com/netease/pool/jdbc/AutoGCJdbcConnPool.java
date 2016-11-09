package com.netease.pool.jdbc;

import com.netease.pool.AutoGCPool;
import com.netease.pool.AutoGCPoolSetting;
import com.netease.pool.ReqSchedulePolicy;


public class AutoGCJdbcConnPool extends AutoGCPool<JdbcConnResource, JdbcConnArg> {
	
	public AutoGCJdbcConnPool(String name, JdbcConnArg arg, int gcThreshold) {
		super(null, name, new AutoGCPoolSetting<JdbcConnResource, JdbcConnArg>(arg,
			new JdbcConnFactory(),
			Integer.MAX_VALUE,
			ReqSchedulePolicy.DEFAULT,
			0,
			gcThreshold,
			gcThreshold / 5));
	}

	
	public AutoGCJdbcConnPool(String name, JdbcConnArg arg, int maxSize,
		ReqSchedulePolicy schedulePolicy, int gcInterval, int gcThreshold, int reserveSize) {
		super(null, name, new AutoGCPoolSetting<JdbcConnResource, JdbcConnArg>(arg,
			new JdbcConnFactory(),
			maxSize,
			schedulePolicy,
			reserveSize,
			gcThreshold,
			gcInterval));
	}
}
