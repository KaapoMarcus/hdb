package com.netease.pool.jdbc;

import com.netease.pool.Pool;
import com.netease.pool.PoolSetting;
import com.netease.pool.ReqSchedulePolicy;


public final class JdbcConnPool extends Pool<JdbcConnResource, JdbcConnArg> {
	public JdbcConnPool(String name, JdbcConnArg arg) {
		super(null, name, new PoolSetting<JdbcConnResource, JdbcConnArg>(arg, new JdbcConnFactory(), Integer.MAX_VALUE, ReqSchedulePolicy.DEFAULT));
	}
	
	public JdbcConnPool(String name, JdbcConnArg arg, int maxSize, ReqSchedulePolicy schedulePolicy) {
		super(null, name, new PoolSetting<JdbcConnResource, JdbcConnArg>(arg, new JdbcConnFactory(), maxSize, schedulePolicy));
	}
}
