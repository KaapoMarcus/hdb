package com.jhh.hdb.proxyserver.handlers.mysql;

import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.QueryCmd;
import com.jhh.hdb.proxyserver.handlers.sql.SQLDispatch;
import com.jhh.hdb.proxyserver.handlers.sql.SQLExecutor;
import com.jhh.hdb.proxyserver.handlers.sql.SQLExecutorFactory;
import com.jhh.hdb.proxyserver.handlers.sql.SQLExecutorFactoryImpl;
import com.jhh.hdb.proxyserver.session.SessionContext;
import com.test.stats.sql.Main;

/**
 * �������з�preparestatement��sql���
 * 
 *
 */
public class QueryHandler implements MySQLCmdHandler {
	private SQLExecutorFactory sqlExecutorFactory = new SQLExecutorFactoryImpl();

	public MySQLCmd handleMessage(SessionContext sessionContext, MySQLCmd cmd) {
		String sql = ((QueryCmd) cmd).getSql().trim();
		if (sql.toLowerCase().contains("from")) {
			try {
				return Main.get_do(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SQLExecutor executor = sqlExecutorFactory.newExecutor(sessionContext,
				sql, sessionContext.getDbConnection());
		return SQLDispatch.dispatch(sessionContext, sql, executor);
	}

	public void setSqlExecutorFactory(SQLExecutorFactory sqlExecutorFactory) {
		this.sqlExecutorFactory = sqlExecutorFactory;
	}

}
