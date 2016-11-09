package com.jhh.hdb.proxyserver.handlers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ��Ӧִ��һ��statement
 * 
 *
 */
public class SQLExecutorStmt implements SQLExecutor {
	private SessionContext sessionContext;
	private String sql;
	private Connection con;

	public SQLExecutorStmt(SessionContext sessionContext, String sql,
			Connection con) {
		super();
		this.sessionContext = sessionContext;
		this.sql = sql;
		this.con = con;
	}

	private Statement newStatement() throws SQLException {
		Statement stmt = con.createStatement();
		if (GlobalContext.getInstance().getConfig().isUseStreamFetch())
			stmt.setFetchSize(GlobalContext.getInstance().getConfig().getStreamFetchSize());
		return stmt;
	}

	public MySQLCmd execute() throws SQLException {
		Statement stmt = null;
		try {
			stmt = newStatement();
			int affectedRow = stmt.executeUpdate(sql);
			return new OkCmd((short) 0, affectedRow, 0, sessionContext
					.getServerStatus().getStatus(), (short) 0,
					"execute successful");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public MySQLCmd executeInsert() throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = newStatement();
			int affectedRow = stmt.executeUpdate(sql);
			long insertId = 0L;//����������ֶΣ������0
			rs = stmt.getGeneratedKeys();
			if (rs != null && rs.next()) {
				insertId = rs.getLong(1);
			}
			return new OkCmd((short) 0, affectedRow, insertId, sessionContext
					.getServerStatus().getStatus(), (short) 0,
					"execute successful");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public MySQLCmd executeQuery() throws SQLException {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = newStatement();
			rs = stmt.executeQuery(sql);
			return new ResultSetCmd(rs, stmt, false, sessionContext
					.getServerStatus().getStatus());
			// �Ȳ��ر�rs��stmt�����ⲿ����رա�con���رա�
		} catch (SQLException e) {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			throw e;
		}
	}

	public Connection getConnection() {
		return con;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}

}
