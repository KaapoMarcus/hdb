package com.jhh.hdb.proxyserver.handlers.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.netease.backend.db.DBConnection;
import com.netease.backend.db.common.definition.DbnType;
import com.netease.backend.db.common.definition.Definition;
import com.netease.backend.db.common.schema.Database;
import com.netease.cli.StringTable;
import com.jhh.hdb.proxyserver.codec.ComBufferCache;
import com.jhh.hdb.proxyserver.commands.MySQLCmd;
import com.jhh.hdb.proxyserver.commands.response.ErrorCmd;
import com.jhh.hdb.proxyserver.commands.response.IsqlResultCmd;
import com.jhh.hdb.proxyserver.commands.response.OkCmd;
import com.jhh.hdb.proxyserver.commands.response.ResultSetCmd;
import com.jhh.hdb.proxyserver.config.SQLSupport;
import com.jhh.hdb.proxyserver.define.ErrorNoDef;
import com.jhh.hdb.proxyserver.server.GlobalContext;
import com.jhh.hdb.proxyserver.session.SessionContext;

/**
 * �ַ�SQL��䣬������Ĳ�ͬ����ִ����Ӧ�߼�
 * 
 *
 */
public class SQLDispatch {
	/**���ù�����־*/
	private static Logger logger = Logger
			.getLogger(Definition.LOGGER_QUERY_SERVER);
	
	public static MySQLCmd dispatch(SessionContext sessionContext, String sql,
			SQLExecutor executor) {
		if (sql == null || sql.length() == 0) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL, "sql can not be null or empty");
		}
		Connection con = sessionContext.getDbConnection();
/*		if (con == null) {
			return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
					ErrorNoDef.SQL_GENERAL, "user has not been authenticated. ");
		}*/

		try {
			// ȥ��ע�ͷ�
			String normalSql = sql;
			if (sql.startsWith("/*")) {
				int end = sql.indexOf("*/", 2);
				if (end != -1)
					normalSql = sql.substring(end + 2, sql.length()).trim();
			}

			// ����begin����
			if (normalSql.equalsIgnoreCase("BEGIN") || isStartTransaction(normalSql)) {
				/*con.setAutoCommit(false);
				sessionContext.getServerStatus().begin();
				return new OkCmd(sessionContext.getServerStatus().getStatus(),
						"BEGIN success");*/
				/*return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL,
						"BEGIN/START TRANSACTION is not supported");*/
				
				
				sessionContext.getServerStatus().begin();
				return executor.execute();
			}

			// ����SET AUTOCOMMIT=true/false
			if (startWithIgnoreCase(normalSql, "SET")) {
				String subSql = normalSql.substring("SET".length()).trim();
				String[] splits = subSql.split("=");
				if (splits.length == 2) {
					String key = splits[0].trim();
					String value = splits[1].trim();
					if (key.equalsIgnoreCase("AUTOCOMMIT")) {
						int valueInt = convertToBoolean(value);
						if (valueInt == 1) {
							con.setAutoCommit(true);
							sessionContext.getServerStatus()
									.setAutoCommitTrue();
							return new OkCmd(sessionContext.getServerStatus()
									.getStatus(), "set AUTOCOMMIT true success");
						} else if (valueInt == 0) {
							con.setAutoCommit(false);
							sessionContext.getServerStatus()
									.setAutoCommitFalse();
							return new OkCmd(sessionContext.getServerStatus()
									.getStatus(),
									"set AUTOCOMMIT false success");
						} else
							return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
									ErrorNoDef.SQL_GENERAL,
									"sql syntax wrong, value must be 1/0 or true/false");
					} else if (key.equalsIgnoreCase("DEFAULTXA")) {
						int valueInt = convertToBoolean(value);
						DBConnection dbConn = (DBConnection)con;
						if (valueInt == 1) {
							dbConn.setDefaultXaTransaction(true);
							return new OkCmd(sessionContext.getServerStatus()
									.getStatus(), "set DEFAULTXA true success");
						} else if (valueInt == 0) {
							dbConn.setDefaultXaTransaction(false);
							return new OkCmd(sessionContext.getServerStatus()
									.getStatus(),
									"set DEFAULTXA false success");
						} else
							return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
									ErrorNoDef.SQL_GENERAL,
									"sql syntax wrong, value must be 1/0 or true/false");
					}
				}
			}

			// ����COMMIT
			if (normalSql.equalsIgnoreCase("COMMIT")) {
				con.commit();
				sessionContext.getServerStatus().commitOrRollback();
				return new OkCmd(sessionContext.getServerStatus().getStatus(),
						"commit succeed");
			}

			// ����ROLLBACK
			if (normalSql.equalsIgnoreCase("ROLLBACK")) {
				con.rollback();
				sessionContext.getServerStatus().commitOrRollback();
				return new OkCmd(sessionContext.getServerStatus().getStatus(),
						"rollback succeed");
			}

			// ��������sql���
			int behavior = GlobalContext.getInstance().getConfig()
					.getSqlSupport().getBehavior(normalSql);
			switch (behavior) {
			case SQLSupport.BEHAVIOR_QUERY: {
				// query
				if (logger.isDebugEnabled())
					logger.debug("request queryed: " + sql);
				sessionContext.getServerStatus().execute();
				return executor.executeQuery();
			}
			case SQLSupport.BEHAVIOR_INSERT: {
				// insert
				if (logger.isDebugEnabled())
					logger.debug("request inserted: " + sql);
				sessionContext.getServerStatus().execute();
				return executor.executeInsert();
			}
			case SQLSupport.BEHAVIOR_EXECUTE: {
				// execute on server
				if (logger.isDebugEnabled())
					logger.debug("request executed: " + sql);
				sessionContext.getServerStatus().execute();
				return executor.execute();
			}
			case SQLSupport.BEHAVIOR_IGNORE: {
				//ignore
				if (logger.isDebugEnabled())
					logger.debug("request ignored: " + sql);

				//��ͬ���ѳɹ�ִ�У���ǰ�ķ�����״̬Ӧ�ø���
				sessionContext.getServerStatus().execute();
				return new OkCmd(sessionContext.getServerStatus().getStatus(),
						"server ignore:" + sql);
			}
			case SQLSupport.BEHAVIOR_QUERY_DBN_CACHE: {/*
				// query on dbn and cache
				if (logger.isDebugEnabled())
					logger.debug("request queryed on server: " + sql);

				List<byte[]> cacheResult = ComBufferCache.get(sql);//ͳһ�ô�д

				sessionContext.getServerStatus().execute();

				if (cacheResult != null) {
					//ֱ�ӷ��ػ���Ľ��
					return new ResultSetCmd(cacheResult);
				} else {
					//ֱ�ӵ�һ��MySQL�ڵ���ִ�в����ؽ��
					DBConnection dbCon = (DBConnection) con;
					String url = null;
					for (Database db : dbCon.getDefaultCluster().getDbMap()
							.values()) {
						if (db.isEnabled() && db.inNormalStatus()
								&& db.getDbnType() == DbnType.MySQL) {
							url = db.getURL();
							break;
						}
					}

					if (url == null)
						throw new SQLException(
								"there is no enable mysql dbn in ddb");

					Connection mysqlCon = DriverManager.getConnection(url,
							sessionContext.getUsername(), sessionContext
									.getPassword());
					Connection oldCon = executor.getConnection();
					executor.setConnection(mysqlCon);
					ResultSetCmd rsCmd = (ResultSetCmd) executor.executeQuery();
					executor.setConnection(oldCon);
					rsCmd.closeConWhenExecuted(mysqlCon);
					rsCmd.setKey(sql);
					return rsCmd;
				}
			*/}
			case SQLSupport.BEHAVIOR_ISQL: {
				if (logger.isDebugEnabled())
					logger.debug("request isql on server: " + sql);
				
				sessionContext.getServerStatus().execute();
				
				try {
					final StringTable st = sessionContext.getIsqlExecutor().execute(sql);
					return new IsqlResultCmd(st, sessionContext.getServerStatus().getStatus());
				} catch (Exception e) {
					throw new SQLException("execute isql request '" + sql + "' failed: " + e.getMessage());
				}
			}
			case SQLSupport.BEHAVIOR_REFUSE: {
				if (logger.isDebugEnabled())
					logger.debug("request refused: " + sql);
				return new ErrorCmd(ErrorNoDef.ERROR_GENERAL,
						ErrorNoDef.SQL_GENERAL,
						"the command is refused on server:" + sql);
			}
			default:
				throw new IllegalArgumentException(
						"return behavior type unrecognized:" + behavior);
			}
		} catch (SQLException e) {
			if (logger.isDebugEnabled())
				logger.error("SQL Exception:", e);
			return new ErrorCmd(e);
		}

	}
	
	private static boolean startWithIgnoreCase(String str, String prefix) {
		if (str.length() < prefix.length())
			return false;
		String strPrefix = str.substring(0, prefix.length());
		return strPrefix.equalsIgnoreCase(prefix);
	}

	private static boolean isStartTransaction(String sql){
		if (startWithIgnoreCase(sql, "START ")) {
			String subSql = sql.substring("START ".length()).trim();
			if(subSql.equalsIgnoreCase("TRANSACTION")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * if 1/true return 1; if 0/false return 0; else return -1 
	 * @param value
	 * @return
	 */
	private static int convertToBoolean(String value) {
		if (value.equalsIgnoreCase("1")
				|| value.equalsIgnoreCase("TRUE")) {
			return 1;
		} else if (value.equalsIgnoreCase("0")
				|| value.equalsIgnoreCase("FALSE")) {
			return 0;
		} else {
			return -1;
		}
	}
}
