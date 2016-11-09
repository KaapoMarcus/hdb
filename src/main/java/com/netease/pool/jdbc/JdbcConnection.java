package com.netease.pool.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.netease.pool.Disposable;


public final class JdbcConnection implements Disposable {
	
	private Connection conn;

	
	public JdbcConnection(JdbcConnArg arg) throws SQLException {
		this.conn = DriverManager.getConnection(arg.getUrl(), arg.getUser(),
				arg.getPassword());
	}

	
	public void dispose() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public Connection getConnection() {
		return conn;
	}
}
