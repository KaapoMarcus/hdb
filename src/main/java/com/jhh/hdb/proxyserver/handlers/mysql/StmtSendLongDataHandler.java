package com.jhh.hdb.proxyserver.handlers.mysql;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.StmtSendLongDataCmd;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.jhh.hdb.proxyserver.session.StmtContext;
import com.jhh.hdb.proxyserver.session.StmtLongData;

/**
 * ����longdata���(blob)
 * 
 *
 */
public class StmtSendLongDataHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		StmtSendLongDataCmd stmtSendLongDataCmd = (StmtSendLongDataCmd) cmd;
		StmtContext stmtContext = sessionContext.getStmtContextMap().get(
				stmtSendLongDataCmd.getStatementID());
		if (stmtContext == null) {
			return null;
		}

		if (stmtContext.getErrorMsg() != null) {
			// ���֮ǰ�������쳣�����ٴ���
			return null;
		}

		if (stmtSendLongDataCmd.getParameterNumber() >= stmtContext
				.getParameterCount()) {
			stmtContext
					.setErrorMsg("parameter number too large, ParameterNumber:"
							+ stmtSendLongDataCmd.getParameterNumber()
							+ ",parameter count: "
							+ stmtContext.getParameterCount());
			return null;
		}

		StmtLongData longData = stmtContext.getLongDataParams()[stmtSendLongDataCmd
				.getParameterNumber()];
		if (longData == null) {
			longData = new StmtLongData();
			stmtContext.getLongDataParams()[stmtSendLongDataCmd
					.getParameterNumber()] = longData;
		}

		if (longData.getTotalLength() > GlobalContext.getInstance()
				.getConfig().getBlobLength()) {
			stmtContext
					.setErrorMsg("blob length too large, total length:"
							+ longData.getTotalLength()
							+ ",max length: "
							+ GlobalContext.getInstance().getConfig()
									.getBlobLength());
			return null;
		}

		longData.append(stmtSendLongDataCmd.getPayload());
		return null;
	}
}
