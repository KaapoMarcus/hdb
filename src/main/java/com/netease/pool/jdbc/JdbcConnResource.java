package com.netease.pool.jdbc;

import java.sql.Connection;

import com.netease.pool.Pool;
import com.netease.pool.Resource;


public final class JdbcConnResource extends Resource<JdbcConnection> {
	
	public JdbcConnResource(String name, JdbcConnection conn, Pool<?, ?> pool) {
		super(name, conn, pool);
	}
	
	
	public Connection getConnection() {
		return get().getConnection();
	}
}
