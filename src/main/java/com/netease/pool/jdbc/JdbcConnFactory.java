package com.netease.pool.jdbc;

import com.netease.pool.Factory;
import com.netease.pool.Pool;


public final class JdbcConnFactory implements Factory<JdbcConnResource, JdbcConnArg> {
	
	public JdbcConnResource createResource(JdbcConnArg arg, Pool<?, ?> pool)
			throws Exception {
		return new JdbcConnResource("", new JdbcConnection(arg), pool);
	}
}
