package com.jhh.hdb.proxyserver.handlers.mysql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.netease.backend.db.DBConnection;
import com.netease.backend.db.DBPreparedStatement;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtPrepareCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.PrepareOkCmd;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;

/**
 * �ڷ�������һ��preparestatement���
 * 
 *
 */
public class StmtPrepareHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		StmtPrepareCmd stmtPrepareCmd = (StmtPrepareCmd) cmd;
		DBConnection con = (DBConnection) sessionContext.getDbConnection();

		try {
			DBPreparedStatement pstmt = (DBPreparedStatement) con
					.prepareStatement(stmtPrepareCmd.getSql());

			if (GlobalContext.getInstance().getConfig().isUseStreamFetch())
				pstmt.setFetchSize(GlobalContext.getInstance().getConfig()
						.getStreamFetchSize());

			int pstmtId = sessionContext.newPstmtId();
			sessionContext.getStmtContextMap().put(pstmtId,
					new StmtContext(pstmtId, pstmt.getParameterCount(), pstmt));

			ResultSetMetaData metaData = pstmt.getMetaData();
			return new PrepareOkCmd(pstmtId, (short) pstmt.getParameterCount(),
					metaData, sessionContext.getServerStatus().getStatus());
		} catch (SQLException e) {
			return new ErrorCmd(e);
		}

	}
}
