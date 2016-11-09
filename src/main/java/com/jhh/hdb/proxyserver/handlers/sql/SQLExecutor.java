package com.jhh.hdb.proxyserver.handlers.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;

/**
 * SQL���ִ����
 * 
 *
 */
public interface SQLExecutor {
	public MySQLCmd executeQuery() throws SQLException;

	public MySQLCmd executeInsert() throws SQLException;

	public MySQLCmd execute() throws SQLException;
	
	public Connection getConnection();
	
	public void setConnection(Connection con) throws SQLException;
}
