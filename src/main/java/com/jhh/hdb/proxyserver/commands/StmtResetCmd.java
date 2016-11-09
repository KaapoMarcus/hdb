package com.jhh.hdb.proxyserver.commands;

/**
 * Reset (empty) the parameter buffers for a prepared statement. 
 * Mostly used in connection with COM_LONG_DATA
 * 
 *
 */
public class StmtResetCmd extends CommandCmd {

	private int statementId;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.stmtReset;
	}

	public int getStatementId() {
		return statementId;
	}

	public void setStatementId(int statementId) {
		this.statementId = statementId;
	}

	@Override
	public String toString() {
		return "[StmtResetCmd] StatementId:" + this.statementId;
	}
}
