package com.jhh.hdb.proxyserver.handlers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.jhh.hdb.proxyserver.session.SessionContext;

public class SQLExecutorFactoryImpl implements SQLExecutorFactory {

	public SQLExecutor newExecutor(SessionContext sessionContext, String sql,
			Connection con) {
		return new SQLExecutorStmt(sessionContext, sql, con);
	}

	public SQLExecutor newExecutor(SessionContext sessionContext,
			PreparedStatement pstmt) {
		return new SQLExecutorPStmt(sessionContext, pstmt);
	}

}
