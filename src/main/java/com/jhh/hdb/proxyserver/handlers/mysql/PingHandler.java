package com.jhh.hdb.proxyserver.handlers.mysql;

import java.sql.SQLException;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * ����ping����
 * 
 *
 */
public class PingHandler implements MySQLCmdHandler {

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		try {
			if (sessionContext.getDbConnection().isClosed()) {
				return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL,
						"database connection is already closed.");
			} else {
				return new OkCmd(sessionContext.getServerStatus().getStatus(),
						"server ok");
			}
		} catch (SQLException e) {
			return new ErrorCmd(e);
		}
	}

}
