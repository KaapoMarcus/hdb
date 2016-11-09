package com.jhh.hdb.proxyserver.handlers.mysql;

import com.netease.backend.db.DBResultSet;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtFetchCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;

/**
 * fetch����¼
 * 
 *
 */
public class StmtFetchHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		StmtFetchCmd stmtFetchCmd = (StmtFetchCmd) cmd;

		if (stmtFetchCmd.getFetchRow() > GlobalContext.getInstance()
				.getConfig().getMaxPstmtFetchSize()) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"fetch preparedstatement failed, fetch row too big, max:"
							+ GlobalContext.getInstance().getConfig()
									.getMaxPstmtFetchSize());
		}
		StmtContext stmtContext = sessionContext.getStmtContextMap().get(
				stmtFetchCmd.getStatementId());
		if (stmtContext == null) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"fetch preparedstatement failed, can not found statement id:"
							+ stmtFetchCmd.getStatementId());
		}

		DBResultSet rs = stmtContext.getResultset();
		if (rs == null) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"fetch preparedstatement failed, cursor not opened");
		}

		ResultSetCmd resultSetCmd = new ResultSetCmd(rs,stmtContext.getPstmt(), 
				true, sessionContext.getServerStatus().getStatus());
		resultSetCmd.setFetchSize(stmtFetchCmd.getFetchRow());
		resultSetCmd.setStmtFetchBegin(false);
		return resultSetCmd;
	}
}
