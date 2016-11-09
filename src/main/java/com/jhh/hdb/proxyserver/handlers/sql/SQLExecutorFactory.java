package com.jhh.hdb.proxyserver.handlers.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * SQLExecutor�๤��
 * 
 *
 */
public interface SQLExecutorFactory {
	public SQLExecutor newExecutor(SessionContext sessionContext, String sql,
			Connection con);

	public SQLExecutor newExecutor(SessionContext sessionContext,
			PreparedStatement pstmt);
}
