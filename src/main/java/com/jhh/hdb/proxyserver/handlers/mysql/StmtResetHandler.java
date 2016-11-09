package com.jhh.hdb.proxyserver.handlers.mysql;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtResetCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;

/**
 * ����preparestatement�����֮ǰ��longdata���
 * 
 *
 */
public class StmtResetHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		StmtResetCmd stmtResetCmd = (StmtResetCmd) cmd;

		StmtContext stmtContext = sessionContext.getStmtContextMap().get(
				stmtResetCmd.getStatementId());
		if (stmtContext == null) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL,
					"reset long data failed, can not found statement id:"
							+ stmtResetCmd.getStatementId());
		}

		stmtContext.resetLongData();

		return new OkCmd(sessionContext.getServerStatus().getStatus(), "reset long data ok");
	}

}
