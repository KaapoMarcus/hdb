package com.jhh.hdb.proxyserver.commands;

/**
 * 关闭preparestatement命令
 * 
 *
 */
public class StmtCloseCmd extends CommandCmd {

	private int statementId;

	@Override
	public MySQLCmdType getType() {
		return MySQLCmdType.stmtClose;
	}

	public int getStatementId() {
		return statementId;
	}

	public void setStatementId(int statementId) {
		this.statementId = statementId;
	}

	@Override
	public String toString() {
		return "[StmtCloseCmd] StatementId:" + this.statementId;
	}
	
}
