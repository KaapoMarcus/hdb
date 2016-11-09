package com.jhh.hdb.proxyserver.commands;

/**
 * 执行sql语句命令
 * 
 *
 */
public class QueryCmd extends CommandCmd {

	private String sql;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.query;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		if (sql.length() > 512)
			return "[QueryCmd] SQL:" + sql.substring(0, 128);
		return "[QueryCmd] SQL:" + sql;
	}
}
