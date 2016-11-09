package com.jhh.hdb.proxyserver.handlers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ��Ӧִ��һ��preparestatement
 * 
 *
 */
public class SQLExecutorPStmt implements SQLExecutor {
	private SessionContext sessionContext;
	private PreparedStatement pstmt;

	public SQLExecutorPStmt(SessionContext sessionContext,
			PreparedStatement pstmt) {
		super();
		this.sessionContext = sessionContext;
		this.pstmt = pstmt;

	}

	public MySQLCmd execute() throws SQLException {
		int affectedRow = pstmt.executeUpdate();
		return new OkCmd((short) 0, affectedRow, 0, sessionContext
				.getServerStatus().getStatus(), (short) 0, "execute successful");
	}

	public MySQLCmd executeInsert() throws SQLException {
		ResultSet rs = null;
		try {
			int affectedRow = pstmt.executeUpdate();
			long insertId = 0L;//����������ֶΣ������0
			rs = pstmt.getGeneratedKeys();
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
			} catch (Exception e) {
			}
		}
	}

	public MySQLCmd executeQuery() throws SQLException {
		ResultSet rs = null;
		try {
			rs = pstmt.executeQuery();
			return new ResultSetCmd(rs, pstmt, true, sessionContext
					.getServerStatus().getStatus());
			// �Ȳ��ر�rs��stmt�����ⲿ����رա�con���رա�
		} catch (SQLException e) {
			if (rs != null) {
				rs.close();
			}
			throw e;
		}
	}

	public Connection getConnection() {
		return null;
	}

	public void setConnection(Connection con) throws SQLException {
		throw new SQLException("SQLExecutorPStmt can not change connection");
	}

}
