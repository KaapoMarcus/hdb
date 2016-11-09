package com.jhh.hdb.proxyserver.commands;

/**
 * 设置数据库名称命令
 * 
 *
 */
public class InitDBCmd extends CommandCmd {
	private String database;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.initDb;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	@Override
	public String toString() {
		return "[InitDBCmd] Database:" + database;
	}
	
}
