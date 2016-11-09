package com.jhh.hdb.proxyserver.commands;

/**
 * 在服务器上缓存一个preparestatement
 * 
 *
 */
public class StmtPrepareCmd extends CommandCmd {

	private String sql;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.stmtPrepare;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "[StmtPrepareCmd] SQL:" + sql;
	}
}
